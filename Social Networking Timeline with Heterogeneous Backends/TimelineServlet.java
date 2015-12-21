package cc.cmu.edu.minisite;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;



import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Cluster;
import org.apache.hadoop.hbase.rest.client.RemoteHTable;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.JSONArray;
import org.json.JSONObject;

public class TimelineServlet extends HttpServlet {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/p1";
	static final String USER = "root";
	static final String PASSWD = "15319project";
	static Connection conn = null;
	static Statement statement = null;
	static Client client;
	static AmazonDynamoDBClient dynamoDB;
	static{
		try {
			//connect to mysql and hbase
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASSWD);
			System.out.println("Connected to database...");
			Cluster cluster = new Cluster();
			cluster.add("52.91.166.199", 8080);
			client = new Client(cluster);
		} catch (Exception e) {
			// TODO: handle exception
		}
		AWSCredentials credentials = null;
        try {
        	//connect to DynamoDB
            credentials = new ProfileCredentialsProvider("default").getCredentials();
			dynamoDB = new AmazonDynamoDBClient(credentials);
			Region usWest2 = Region.getRegion(Regions.US_EAST_1);
			dynamoDB.setRegion(usWest2);
        } catch (Exception e) {
        	 throw new AmazonClientException(
                     "Cannot load the credentials from the credential profiles file. " +
                     "Please make sure that your credentials file is at the correct " +
                     "location (/Users/baiyuanchen/.aws/credentials), and is in valid format.",
                     e);
        }
	}
	
    public TimelineServlet() throws Exception {

    }

    @Override
    protected void doGet(final HttpServletRequest request, 
            final HttpServletResponse response) throws ServletException, IOException {
    	JSONObject result = new JSONObject();
        String id = request.getParameter("id");
        //select use's profile
    	try {
    		statement = conn.createStatement();
        	String sql = "SELECT * FROM up WHERE id = "+id+";";
        	System.out.println(sql);
        	ResultSet resultSet = statement.executeQuery(sql);
        	while(resultSet.next()){
    			String name = resultSet.getString("name");
    			String url = resultSet.getString("url");
    			System.out.println("user profile "+name+"=="+url);
    			result.put("name",name);
    			result.put("profile",url);
    		}
        	resultSet.close();
    		statement.close();
		} catch (SQLException e) {
			// TODO: handle exception
		}
        //connect to the first hbase table
    	//this table saves the followee -> followers data
    	//example:  1111,112 323 4434 5454 545
		RemoteHTable frtable = new RemoteHTable(client, "links");
        JSONArray fr = new JSONArray();
        PriorityQueue<followerInfo> frQueue = new PriorityQueue<followerInfo>();
        Get frget = new Get(Bytes.toBytes(id));
        Result frResult = frtable.get(frget);
        
        String flist = null;
        try {
			frResult = frtable.get(frget);
			for(KeyValue keyValue:frResult.raw()){
	        	String qual = new String(keyValue.getQualifier());
	        	if(qual.equals("follower")){
	        		flist = new String(keyValue.getValue());
	        	}
	        }
			//a saves all the followers id
			String[] a = flist.split(" ");
			for(String b :a){
				try {
					//select every followers' name and profile from database
					statement = conn.createStatement();
					String sql2 = "SELECT * FROM up WHERE id = " + b + ";";
					System.out.println(sql2);
					ResultSet resultSet2 = statement.executeQuery(sql2);
					while(resultSet2.next()){
						String name = resultSet2.getString("name");
						String url = resultSet2.getString("url");
						followerInfo f = new followerInfo(name, url);
						frQueue.offer(f);
					}
					resultSet2.close();
					statement.close();
				} catch (SQLException e) {
					// TODO: handle exception
				}	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //put the followers' name and profile in the json data
        while(!frQueue.isEmpty()){
	        JSONObject follower = new JSONObject();
	        follower.put("name", frQueue.peek().name);
	        follower.put("profile", frQueue.peek().url);
	        frQueue.poll();
	        fr.put(follower);
        }
        result.put("followers", fr);
        //get the followees post
        int[] feID = null;
        try {
        	//connect the 2nd hbase table 
        	//saves follower->followees
              RemoteHTable feTable = new RemoteHTable(client, "links_rev");
              System.out.println("connect to links_rev");
              Get get1 = new Get(Bytes.toBytes(id));
              String feraw = null;
              Result fer = feTable.get(get1);
              for(KeyValue keyValue:fer.raw()){
              	String qual = new String(keyValue.getQualifier());
              	if(qual.equals("followee")){
              		feraw = new String(keyValue.getValue());
              	}
              }       
              //a saves all the followees' id
              String[] a = feraw.split(" ");
              feID = new int[a.length];
              PriorityQueue<Reply> repQueue = new PriorityQueue<Reply>();
              for(String b :a){
              	int fe = Integer.parseInt(b);
              	System.out.println("id:\t"+fe);
              	List<Reply> post30 = new ArrayList<Reply>();
              	Reply item = new Reply();
              	//get followee's post in dynamoDB
              	item.setUserID(fe);
              	DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDB);
              	DynamoDBQueryExpression<Reply> queryExpression = new DynamoDBQueryExpression<Reply>()
                .withHashKeyValues(item);  
              	List<Reply> rlist = dynamoDBMapper.query(Reply.class, queryExpression);
              	if(rlist.size() > 30){
              		for(int k =rlist.size()-1,count = 0;count<30; k--,count++){
                  	  post30.add(rlist.get(k));
                    }
              	}
	            else{
              		for(int k =rlist.size()-1;k > -1; k--){
                  	  post30.add(rlist.get(k));
                    }
                }
                for(Reply rep: post30){
              	  repQueue.offer(rep);
                } 
	          }
             //put arraylist to json
              List<JSONObject> last30 = new ArrayList<JSONObject>();
              int cnt = 0;
              while(!repQueue.isEmpty()&&cnt <30){
            	  last30.add(0,new JSONObject(repQueue.poll().Post));
            	  cnt++;
              }
              result.put("posts",last30);              
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
      // implement the functionalities in doGet method.
        // you can add any helper methods or classes to accomplish this task

        PrintWriter writer = response.getWriter();           
        writer.write(String.format("returnRes(%s)", result));
        writer.close();
    }
    
    //class to save the follower's info
    public static class followerInfo implements Comparable<followerInfo>{
    	String name;
    	String url;
    	public followerInfo(String name, String url){
			// TODO Auto-generated constructor stub    		
    		this.name = name;
    		this.url = url;
    	}
    	//if name is tie use the url to arrange order
    	public int  compareTo(followerInfo user) {
			return this.name.compareTo(user.name)==0? this.url.compareTo(user.url):this.name.compareTo(user.name);
		}
    }
    //class to get the data from dynamoDB
    //learn from (http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/JavaQueryScanORMModelExample.html)
    @DynamoDBTable(tableName="cxnmbyyc")
    public static class Reply implements Comparable<Reply>{
        private int UserID;
        private String Timestamp;
        private String Post;

        @DynamoDBHashKey(attributeName="UserID")
        public int getUserID() { return UserID; }
        public void setUserID(int UserID) { this.UserID = UserID; }
        
        @DynamoDBRangeKey(attributeName="Timestamp")
        public String getTimestamp() { return Timestamp; }
        public void setTimestamp(String Timestamp) { this.Timestamp = Timestamp; }

        @DynamoDBAttribute(attributeName="Post")
        public String getPost() { return Post; }
        public void setPost(String Post) { this.Post = Post; }  
        
        //override the compareTo, in order to let the reply data from dynamodb
        //arrange in the descending order 
        @Override
        public int compareTo(Reply e) {
        	long tut = 0;
        	long eut = 0;
        	//compare the pid
        	//and make it a reverse order
			if(this.Timestamp.equals(e.Timestamp)){
				JSONObject j = new JSONObject(this.Post);
				int pid = j.getInt("pid");
				JSONObject ej = new JSONObject(e.Post);
				int epid = ej.getInt("pid");
				if(pid > epid)
					return -1;
				else 
					return 1;
			}
			//change timestamp to unix timestamp in order to compare
			//and make it a reverse order
			else{
				String pattern = "yyyy-MM-dd hh:mm:ss";
				DateFormat dateFormat = new SimpleDateFormat(pattern);
				try {
					Date tdate = dateFormat.parse(this.Timestamp);
					Date edate = dateFormat.parse(e.Timestamp);
					tut = (long) tdate.getTime()/1000;
					eut = (long) edate.getTime()/1000;
					
				} catch (ParseException e2) {
					// TODO: handle exception
				}	
			}
			if(tut > eut)
				return -1;
			else
				return 1;
		}
    }
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

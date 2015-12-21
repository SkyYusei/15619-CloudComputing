package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.json.JSONObject;

import sun.print.resources.serviceui;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Cluster;
import org.apache.hadoop.hbase.rest.client.RemoteHTable;
import org.apache.hadoop.hbase.rest.protobuf.generated.CellMessage.Cell;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.JSONArray;

public class FollowerServlet extends HttpServlet {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/p1";
	static final String USER = "root";
	static final String PASSWD = "15319project";
	static Connection conn = null;
	static Statement statement1 = null;
	static Statement statement2 = null;
	
	
    public FollowerServlet() {

    }
    public static class followerInfo implements Comparable<followerInfo>{
    	String name;
    	String url;
    	public followerInfo(String name, String url){
			// TODO Auto-generated constructor stub    		
    		this.name = name;
    		this.url = url;
    	}
    	public int  compareTo(followerInfo user) {
			return this.name.compareTo(user.name)==0? this.url.compareTo(user.url):this.name.compareTo(user.name);
		}
    }
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
    		conn = DriverManager.getConnection(DB_URL,USER,PASSWD);
    		System.out.println("Connected to mysql...");
    		
		} catch (Exception e) {
			// TODO: handle exception
		}
    	//connect to hbase use rest api
    	Cluster cluster = new Cluster();  
        cluster.add("52.91.166.199", 8080);  
        Client client = new Client(cluster);
        RemoteHTable table = new RemoteHTable(client, "links");
        System.out.println("Connected to hbase...");
        String id = request.getParameter("id");
        //in order to store the follower info
        PriorityQueue<followerInfo> fInfo = new PriorityQueue<followerInfo>();       
        JSONObject result = new JSONObject();
        JSONArray followers = new JSONArray();
        Get get = new Get(Bytes.toBytes(id));
        Result hresult = null;
        String flist = null;
        try {
        	//get the data from hbase
			hresult = table.get(get);
			for(KeyValue keyValue:hresult.raw()){
	        	String qual = new String(keyValue.getQualifier());
	        	if(qual.equals("follower")){
	        		flist = new String(keyValue.getValue());
	        	}
	        }
			//a contains all followers id
			String[] a = flist.split(" ");
			for(String b :a){
				try {
					//use the id to get the name and url
					statement1 = conn.createStatement();
					String sql = "SELECT * FROM up WHERE id = " + b + ";";
					System.out.println(sql);
					ResultSet resultSet = statement1.executeQuery(sql);
					while(resultSet.next()){
						String name = resultSet.getString("name");
						String url = resultSet.getString("url");
						followerInfo f = new followerInfo(name, url);
						fInfo.offer(f);
					}
					resultSet.close();
				} catch (Exception e) {
					// TODO: handle exception
				}	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //put queue's data to json
        while(!fInfo.isEmpty()){
	        JSONObject follower = new JSONObject();
	        follower.put("name", fInfo.peek().name);
	        follower.put("profile", fInfo.peek().url);
	        fInfo.poll();
	        followers.put(follower);
        }
       
        result.put("followers", followers);
        PrintWriter writer = response.getWriter();
        writer.write(String.format("returnRes(%s)", result.toString()));
        writer.close();
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}


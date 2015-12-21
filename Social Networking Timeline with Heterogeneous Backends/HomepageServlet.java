package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
import org.json.JSONArray;
import org.json.JSONObject;

public class HomepageServlet extends HttpServlet {
	
	static AmazonDynamoDBClient dynamoDB;

    /**
     * The only information needed to create a client are security credentials
     * consisting of the AWS Access Key ID and Secret Access Key. All other
     * configuration, such as the service endpoints, are performed
     * automatically. Client parameters, such as proxies, can be specified in an
     * optional ClientConfiguration object when constructing a client.
     *
     * @see com.amazonaws.auth.BasicAWSCredentials
     * @see com.amazonaws.auth.ProfilesConfigFile
     * @see com.amazonaws.ClientConfiguration
     */
    static  {
    	 /*
         * Before running the code:
         *      Fill in your AWS access credentials in the provided credentials
         *      file template, and be sure to move the file to the default location
         *      (/Users/baiyuanchen/.aws/credentials) where the sample code will load the
         *      credentials from.
         *      https://console.aws.amazon.com/iam/home?#security_credential
         *
         * WARNING:
         *      To avoid accidental leakage of your credentials, DO NOT keep
         *      the credentials file in your source directory.
         */
        AWSCredentials credentials = null;
        try {
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

    public HomepageServlet() {

    }
	
    @Override
    protected void doGet(final HttpServletRequest request, 
            final HttpServletResponse response) throws ServletException, IOException {
	    String id = request.getParameter("id");	
	    JSONObject result = new JSONObject();
        //some of these are from the aws dynamoDB's java default code
        try {
            String tableName = "cxnmbyyc";
            Reply reply = new Reply();
            reply.setUserID(Integer.parseInt(id));
            DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDB);
            DynamoDBQueryExpression<Reply> queryExpression = new DynamoDBQueryExpression<Reply>()
            .withHashKeyValues(reply);
            
            boolean f = false;
            List<Reply> list = dynamoDBMapper.query(Reply.class, queryExpression);
            
            //merage json to an array list
            List<JSONObject> posts = new ArrayList<JSONObject>();
            for(Reply e :list){
            	posts.add(new JSONObject(e.Post));
            }
            result.put("posts",posts);
     
       } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }

        PrintWriter writer = response.getWriter();           
        writer.write(String.format("returnRes(%s)", result));
        writer.close();
    }
    
    @DynamoDBTable(tableName="cxnmbyyc")
    public static class Reply {
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
        
       
    }
    @Override
    protected void doPost(final HttpServletRequest request, 
            final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

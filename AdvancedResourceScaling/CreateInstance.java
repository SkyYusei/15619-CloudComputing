import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;

public class  CreateInstance{
		
		public String instanceId = null;
		public AmazonEC2 ec2;
		public String DNS=null;
		public String status;

		public void createInstance(String imageId, String instanceType, String SGID) {
			
			AWSCredentials credentials = null;
		    try {
		        credentials = new ProfileCredentialsProvider("default").getCredentials();  //get credential
		    } catch (Exception e) {
		        throw new AmazonClientException(
		                "Cannot load the credentials from the credential profiles file. " +
		                "Please make sure that your credentials file is at the correct " +
		                "location (/Users/baiyuanchen/.aws/credentials), and is in valid format.",
		                e);
		    }
		    ec2 = new AmazonEC2Client(credentials);
		    RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		    runInstancesRequest.withImageId(imageId)    //launch instance 
		    .withInstanceType(instanceType)
		    .withMinCount(1)
		    .withKeyName("15319demo")
		    .withMaxCount(1) 
		    .withSubnetId("subnet-6695de4d")
		    .withSecurityGroupIds(SGID); //choose security group
		    RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
		    Instance instance = runInstancesResult.getReservation().getInstances().get(0);
		    CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		    createTagsRequest.withResources(instance.getInstanceId())
		    .withTags(new Tag("Project","2.2"));
		    instanceId =  instance.getInstanceId();
		    ec2.createTags(createTagsRequest);
}}

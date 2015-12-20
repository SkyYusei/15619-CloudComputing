import java.util.List;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.Reservation;

public class GetInstanceInfo {
	public AmazonEC2 ec2;
	public String DNS=null;
	public String status;
	
	public void getInstanceInfo(String instanceId) {
		// TODO Auto-generated method stub
		AWSCredentials credentials = new ProfileCredentialsProvider("default").getCredentials();
		ec2 = new AmazonEC2Client(credentials);
		DescribeInstanceStatusRequest describeInstanceRequest = new DescribeInstanceStatusRequest().withInstanceIds(instanceId);
	    DescribeInstanceStatusResult describeInstanceResult = ec2.describeInstanceStatus(describeInstanceRequest);
	    List<InstanceStatus> state = describeInstanceResult.getInstanceStatuses();   //get instance status 
	    while (state.size() < 1) { 
	        // Do nothing, just wait, have thread sleep if needed
	        describeInstanceResult = ec2.describeInstanceStatus(describeInstanceRequest);
	        state = describeInstanceResult.getInstanceStatuses();
	    }
	    status = state.get(0).getInstanceState().getName();  //make sure instance is running
	    
	    DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
	    List<Reservation> reservations = describeInstancesRequest.getReservations();
	   
	    for (Reservation reservation : reservations) {
	        for (Instance instance1 : reservation.getInstances()) {
	          if (instance1.getInstanceId().equals(instanceId))
	            DNS = instance1.getPublicDnsName();   //get instance's dns
	        }
	      }
	}
}

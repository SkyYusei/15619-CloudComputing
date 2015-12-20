import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.InstanceMonitoring;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyResult;
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.Tag;



public class AwsConsoleApp {

    static AmazonEC2      ec2;
    static AmazonAutoScalingClient asClient;
    static AmazonCloudWatchClient cloudWatchClient;
    static AmazonElasticLoadBalancingClient elb;
    static String SGID="";
    static String lgDns = "";
    static String subcode = "uS0WkBgqRUVJbiHK6Vus8VeLb9KziALm";
    static String subnetid = "subnet-6695de4d";
    static String ELBDNS;
    static String LGSGID = "sg-db716dbc";
    public static void main(String[] args) throws Exception {
    	init();    	
    	//Create SecurityGroup
    	
    	CreateSecurityGroup();    	    	
    	
    	//Create Load Generator
    	CreateInstance lg = new CreateInstance();
    	lg.createInstance("ami-312b5154", "m3.medium",LGSGID);
    	System.out.println("LG created!");
    	
    	//Get Load  Genertor's DNS
    	GetInstanceInfo lgInfo = new GetInstanceInfo();
    	lgInfo.getInstanceInfo(lg.instanceId);
		lgDns = lgInfo.DNS;
    	System.out.println(lgDns);
    	
    	//System.exit(0); 
    	
    	//Create Load Balancer
    	CreateLoadBalancer();   
    	System.out.println(ELBDNS); 
    	
    	//System.exit(0);
    	
    	//Create LaunchConfiguration
    	CreateLaunchConfiguration(); 	        
    	//AutoScaling Group
        CreateASG();   	       
        
        //set policy
        String scaleOut = CreatePolicy("out",1);
        String scaleIn = CreatePolicy("in",-1);       
        //add policy
        AddPolicyOut(scaleOut);
        AddPolicyIn(scaleIn);   		 
        Thread.sleep(180000);
        //sub code
        boolean subFlage = false;
        while (!subFlage){	
        	try {
        		System.out.println(SubUrl("http://" + lgDns + "/password?passwd=" + subcode));
        		subFlage = true;
        	} catch (IOException e) {
				e.printStackTrace();        		
        	}
        }
                             
        Thread.sleep(360000);
        
        //warm up 1
        try {
        	System.out.println(SubUrl("http://" + lgDns + "/warmup?dns=" + ELBDNS));		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        
        Thread.sleep(1000 * 360);
      //warm up 2
        try {
        	System.out.println(SubUrl("http://" + lgDns + "/warmup?dns=" + ELBDNS));		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        Thread.sleep(1000 * 360);
       
        // start test
        try {
        	System.out.println(SubUrl("http://" + lgDns + "/junior?dns=" + ELBDNS));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
       
        //wait until test finished
        Thread.sleep(60*60*1000);
        
        //terminate everything except load generator         
        TerminanteEveryThing();
       
        
}

    
    
	private static void TerminanteEveryThing() throws Exception {
		// TODO Auto-generated method stub
		// Terminate Load balancer
		System.out.println("Start Deleting LB");
		DeleteLoadBalancerRequest deleteLoadBalancerRequest = new DeleteLoadBalancerRequest();
		deleteLoadBalancerRequest.withLoadBalancerName("project22");		
		elb.deleteLoadBalancer(deleteLoadBalancerRequest);
		System.out.println("LB Deleted!");
		
		
		//ASG and DCs
		UpdateAutoScalingGroupRequest updateAutoScalingGroupRequest = new UpdateAutoScalingGroupRequest();
		updateAutoScalingGroupRequest.withAutoScalingGroupName("project22") // as above          
		  							.withMinSize(0)
		  							.withMaxSize(0);	
		asClient.updateAutoScalingGroup(updateAutoScalingGroupRequest);
		System.out.println("deleting DC and wait 100s");
		
		Thread.sleep(100000);
		DeleteAutoScalingGroupRequest deleteAutoScalingGroupRequest = new DeleteAutoScalingGroupRequest();
		deleteAutoScalingGroupRequest.withAutoScalingGroupName("project22");
		asClient.deleteAutoScalingGroup(deleteAutoScalingGroupRequest);
		System.out.println("ASG Deleted!");
		
		//Launch Configuration
		DeleteLaunchConfigurationRequest deleteLaunchConfigurationRequest = new DeleteLaunchConfigurationRequest();
		deleteLaunchConfigurationRequest.withLaunchConfigurationName("project22");
		asClient.deleteLaunchConfiguration(deleteLaunchConfigurationRequest);
		System.out.println("Launch Configuration Deleted!");
		
		
		System.out.println("wait 300s to delete Security");;
		// SecurityGroup
		Thread.sleep(300000);
		DeleteSecurityGroupRequest deleteSecurityGroupRequest = new DeleteSecurityGroupRequest();
		deleteSecurityGroupRequest.withGroupId(SGID);
		ec2.deleteSecurityGroup(deleteSecurityGroupRequest);
		System.out.println("SG Deleted!");
	}

	private static String SubUrl(String url) throws IOException {
		// TODO Auto-generated method stub
		URL urlsubcode = new URL(url);
		HttpURLConnection urlcon = (HttpURLConnection)urlsubcode.openConnection();
		urlcon.connect();
        String re = "";
        String page = "";
		BufferedReader buffer = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
		while ((re = buffer.readLine()) != null) {
			page += re;
		}
		buffer.close();
		return page;
	}


	private static void AddPolicyIn(String scaleIn) {
		// TODO Auto-generated method stub
		PutMetricAlarmRequest putMetricAlarmRequest = new PutMetricAlarmRequest();
        putMetricAlarmRequest.withAlarmName("AlarmName-down")
        .withMetricName("CPUUtilization");

        List<Dimension> dimensions = new ArrayList<>();
        Dimension dimension = new Dimension();
        dimension.withName("AutoScalingGroupName")
        .withValue("project22");
        dimensions.add(dimension);
        putMetricAlarmRequest.withDimensions(dimensions);

        //CPU <20% 60S -1
        putMetricAlarmRequest.withNamespace("AWS/EC2")
        .withComparisonOperator(ComparisonOperator.LessThanOrEqualToThreshold)
        .withStatistic(Statistic.Average)
        .withUnit(StandardUnit.Percent)
        .withThreshold(20d)
        .withPeriod(60)
        .withEvaluationPeriods(1);

        List<String> actions = new ArrayList<>();
        actions.add(scaleIn); 
        putMetricAlarmRequest.withAlarmActions(actions);

        cloudWatchClient.putMetricAlarm(putMetricAlarmRequest);
	}


	public static  void AddPolicyOut(String scaleOut) {
		// TODO Auto-generated method stub
		PutMetricAlarmRequest putMetricAlarmRequest = new PutMetricAlarmRequest();
        putMetricAlarmRequest.withAlarmName("AlarmName-up")
        .withMetricName("CPUUtilization");

        List<Dimension> dimensions = new ArrayList<>();
        Dimension dimension = new Dimension();
        dimension.withName("AutoScalingGroupName")
        		 .withValue("project22");
        dimensions.add(dimension);
        putMetricAlarmRequest.withDimensions(dimensions);
// CPU >85  60S +1
        putMetricAlarmRequest.withNamespace("AWS/EC2")
        .withComparisonOperator(ComparisonOperator.GreaterThanOrEqualToThreshold)
        .withStatistic(Statistic.Average)
        .withUnit(StandardUnit.Percent)
        .withThreshold(85d)
        .withPeriod(60)
        .withEvaluationPeriods(1);

        List<String> actions = new ArrayList<>();
        actions.add(scaleOut); 
        putMetricAlarmRequest.setAlarmActions(actions);
        cloudWatchClient.putMetricAlarm(putMetricAlarmRequest);
	}


	public static String CreatePolicy(String string, int i) {
		// TODO Auto-generated method stub
		PutScalingPolicyRequest putScalingPolicyRequest = new PutScalingPolicyRequest();
        putScalingPolicyRequest.withAutoScalingGroupName("project22")
       	       .withPolicyName(string) 
       	       .withScalingAdjustment(i)
       	       .withAdjustmentType("ChangeInCapacity")
       	       .withCooldown(60);
	     PutScalingPolicyResult result = asClient.putScalingPolicy(putScalingPolicyRequest);
	     return result.getPolicyARN(); 
	}

	public static void init() throws Exception {
	
	    AWSCredentials credentials = null;
	    try {
	        credentials = new ProfileCredentialsProvider("default").getCredentials();
	    } catch (Exception e) {
	        throw new AmazonClientException(
	                "Cannot load the credentials from the credential profiles file. " +
	                "Please make sure that your credentials file is at the correct " +
	                "location (/Users/baiyuanchen/.aws/credentials), and is in valid format.",
	                e);
	    }
	    ec2 = new AmazonEC2Client(credentials);     
	    asClient = new AmazonAutoScalingClient(credentials);
	    cloudWatchClient = new AmazonCloudWatchClient(credentials);
	    elb = new AmazonElasticLoadBalancingClient(credentials);
	}


	public static void CreateSecurityGroup() throws Exception {
		// TODO Auto-generated method stub
		CreateSecurityGroup createSecurityGroup = new CreateSecurityGroup();
    	createSecurityGroup.createSecurityGroup();
    	SGID = CreateSecurityGroup.SGID;
    	System.out.println("Security Group Created!");
	}


	public static void CreateASG() {
		// TODO Auto-generated method stub
		com.amazonaws.services.autoscaling.model.Tag asgtag = new com.amazonaws.services.autoscaling.model.Tag();
        asgtag.withKey("Project")
         	  .withValue("2.2")
         	  .withPropagateAtLaunch(true);
        
        CreateAutoScalingGroupRequest createAutoScalingGroupRequest = new CreateAutoScalingGroupRequest();
        
        createAutoScalingGroupRequest.withAutoScalingGroupName("project22")
        		  .withLaunchConfigurationName("project22") // as above        
        		  .withVPCZoneIdentifier(subnetid)    
        		  .withMinSize(2)
        		  .withMaxSize(3) 
        		  .withTags(asgtag)      
        		  .withHealthCheckType("ELB")
        		  .withLoadBalancerNames("project22")
        		  .withHealthCheckGracePeriod(100)
        		  .withDefaultCooldown(60);
        
        asClient.createAutoScalingGroup(createAutoScalingGroupRequest);
        System.out.println("ASG Created!");
	}


	public static void CreateLaunchConfiguration() {
		// TODO Auto-generated method stub
		CreateLaunchConfigurationRequest createLaunchConfigurationRequest = new CreateLaunchConfigurationRequest();
        
    	createLaunchConfigurationRequest.withLaunchConfigurationName("project22")
        		 .withImageId("ami-3b2b515e")
        		 .withInstanceType("m3.large")
        		 .withSecurityGroups(SGID);        
        
        InstanceMonitoring monitoring = new InstanceMonitoring();
        monitoring.withEnabled(Boolean.FALSE);
        createLaunchConfigurationRequest.withInstanceMonitoring(monitoring);
        asClient.createLaunchConfiguration(createLaunchConfigurationRequest);
        System.out.println("Launch Configuratuon Created!");
	}


	public static void CreateLoadBalancer() {
		// TODO Auto-generated method stub
		com.amazonaws.services.elasticloadbalancing.model.Tag lbtag = new Tag();
    	lbtag.withKey("Project")
        	 .withValue("2.2");
    	
    	CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest();
    	createLoadBalancerRequest.withLoadBalancerName("project22")
    			 .withSubnets(subnetid)
    			 .withSecurityGroups(SGID)
    			 .withTags(lbtag);
        List<Listener> listeners = new ArrayList<Listener>(1);
        listeners.add(new Listener("HTTP", 80, 80));
        
        createLoadBalancerRequest.withListeners(listeners);
        ELBDNS = elb.createLoadBalancer(createLoadBalancerRequest).getDNSName();
        
        HealthCheck healthCK = new HealthCheck()
        	    .withHealthyThreshold(2)
        	    .withInterval(30)
        	    .withTarget("HTTP:80/heartbeat?lg="+lgDns)
        	    .withTimeout(5)
        	    .withUnhealthyThreshold(10);

        
        ConfigureHealthCheckRequest healthCheckReq = new ConfigureHealthCheckRequest()
        	    .withHealthCheck(healthCK)
        	    .withLoadBalancerName("project22");
        ConfigureHealthCheckResult confChkResult = new ConfigureHealthCheckResult()
        	    .withHealthCheck(healthCK);
        
        
        createLoadBalancerRequest.setListeners(listeners);
        CreateLoadBalancerResult lbResult=elb.createLoadBalancer(createLoadBalancerRequest);
        ConfigureHealthCheckResult healthResult = elb.configureHealthCheck(healthCheckReq);
        System.out.println("LB Created!");
	}
    }

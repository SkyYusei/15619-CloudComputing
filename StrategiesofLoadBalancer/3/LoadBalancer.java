import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.services.storagegateway.model.DeleteChapCredentialsRequest;

public class LoadBalancer {
	private static final int THREAD_POOL_SIZE = 4;
	private final ServerSocket socket;
	private final DataCenterInstance[] instances;

	public LoadBalancer(ServerSocket socket, DataCenterInstance[] instances) {
		this.socket = socket;
		this.instances = instances;
	}

	// Complete this function
	public void start() throws IOException {
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		boolean statusCode = true;
		int count = 0;
		boolean startFlag = false;
		int DeadDC = 0;
		int beginCount = 0;
		int endCount = 0;
		String DCinfo = "";
		while (true) {
			count ++;
			for(int i = 0; i<3; i++){

				//check whether there is a dead data center
				statusCode = getDCStatus(i);
				if(!statusCode&&!startFlag){
					System.out.println("Dead DC :  "+i);
					startFlag = true;
					instances[i]=null;
					DeadDC = i;
					CreateInstance newDC = new CreateInstance();
					//create a new DC and warm up
					newDC.createInstance("ami-ed80c388", "m3.medium", "ALL");
					DCinfo = newDC.instanceId;
					//begin count
					beginCount = count;
				}
			}

			//do only if there is a dead DC
			if(startFlag){
				//get present count
				endCount = count;
				//add DC to LB after 450 steps   30 may changes depend on situation
				if((endCount - beginCount) >30 ){
					startFlag = false;
					GetInstanceInfo DC = new GetInstanceInfo();
					DC.getInstanceInfo(DCinfo);
					instances[DeadDC] = new DataCenterInstance("new DC", "http://"+DC.DNS);
					System.out.println(DeadDC+" : "+DC.DNS);
					count = 0;
				}
			}

			//LB working without dead DC
			for(int j = 0; j<50; j++){
			for(int i =0 ; i<3 ; i++){
				if(instances[i] != null){
					Runnable requestHandler = new RequestHandler(socket.accept(), instances[i]);
					executorService.execute(requestHandler);
				}
			}}
		}
	}

	private boolean getDCStatus(int i){
		// TODO Auto-generated method stub
		try{
			URL urlDC = new URL(instances[i].getUrl());
			HttpURLConnection httpURLConnection = (HttpURLConnection)urlDC.openConnection();
			httpURLConnection.setConnectTimeout(10000);
			httpURLConnection.connect();
			//can get statuscode when DC is alive
			int statusCode = httpURLConnection.getResponseCode();
			System.out.println(i+" : "+statusCode);
			httpURLConnection.disconnect();
			return true;	
		}
		catch(Exception e){
			
		}
		return false;
		//Pattern pattern = Pattern.compile("(\\d+\\.\\d+)");	
		
	}
	
	
}


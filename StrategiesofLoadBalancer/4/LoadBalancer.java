import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		double aveCPU = (double)0;
		double CPULoad = (double)0;
		double sum = (double)0;
		int num = 0;
		int count1 =0;
		String DCinfo = "";
		while (true) {
			count ++;

			//health check every sending 150 sockets
			for(int i = 0; i<3; i++){
				statusCode = getDCStatus(i);
				if(!statusCode&&!startFlag){
					System.out.println("Dead DC :  "+i);
					startFlag = true;
					instances[i]=null;
					DeadDC = i;
					CreateInstance newDC = new CreateInstance();
					newDC.createInstance("ami-ed80c388", "m3.medium", "ALL");
					DCinfo = newDC.instanceId;
					beginCount = count;
				}
			}
			if(startFlag){
				endCount = count;
				//add new DC to LB after 1000 loops, 1000 may changes depends on situation
				if((endCount - beginCount) >1000 ){
					startFlag = false;
					GetInstanceInfo DC = new GetInstanceInfo();
					DC.getInstanceInfo(DCinfo);
					instances[DeadDC] = new DataCenterInstance("new DC", "http://"+DC.DNS);
					System.out.println(DeadDC+" : "+DC.DNS);
					count = 0;
				}
			}
			sum = 0;

			//get average CPU load
			for(int i =0; i <3 ;i ++){
				if(instances[i]!= null){
					sum += getCPULoad(i);
					num++;
				}
			}
			aveCPU = sum/num;
			System.out.println("--------------------"+aveCPU);
			System.out.println("========================="+num);
			num = 0;
			for(int j = 0; j<50; j++){
			for(int i =0 ; i<3 ; i++){
				if(instances[i] != null){
					if(count1 == 5){
						CPULoad = getCPULoad(i);
						count1 =0;
					}
					//send socket to DC whose load is below the average
					if(CPULoad <(aveCPU)-(double)2){
						Runnable requestHandler = new RequestHandler(socket.accept(), instances[i]);
						executorService.execute(requestHandler);
					}
				count1++;
				}
			}
			}
		}
	}

	//get statusCode to determine whether DC is alive
	private boolean getDCStatus(int i){
		// TODO Auto-generated method stub
		try{
			URL urlDC = new URL(instances[i].getUrl());
			HttpURLConnection httpURLConnection = (HttpURLConnection)urlDC.openConnection();
			httpURLConnection.setConnectTimeout(10000);
			httpURLConnection.connect();
			int statusCode = httpURLConnection.getResponseCode();
			System.out.println(i+" : "+statusCode);
			httpURLConnection.disconnect();
			return true;	
		}
		catch(Exception e){
		}
		return false;	
	}
	
	//get CPU load from DC
	private Double getCPULoad(int i) throws IOException {
		// TODO Auto-generated method stub
		URL urlDC = new URL(instances[i].getUrl()+":8080/info/cpu");
		Pattern pattern = Pattern.compile("(\\d+\\.\\d+)");
		String content = null;
		Double CPULoad = (double) 0;
		while(content==null){
			HttpURLConnection httpURLConnection = (HttpURLConnection)urlDC.openConnection();
			httpURLConnection.setConnectTimeout(10000);
			httpURLConnection.connect();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			content = bufferedReader.readLine();
            }
		if(content != null){
			Matcher matcher = pattern.matcher(content);
			if (matcher.find()) {
	            CPULoad = Double.parseDouble(matcher.group());
	        }
		}
		System.out.println(i+" :=========================== "+CPULoad);
		return CPULoad;	
	}
	
	
}



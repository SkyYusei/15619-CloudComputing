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
		Double CPULoad = (double)0;
		int j = 5 ;
		while (true) {
			// By default, it will send all requests to the first instance			
			
			//get CPU load after every 5 counts
			for(int i =0 ; i <3 ; i++){
				if(j == 5){
					CPULoad = getCPULoad(i);
					j = 0;
				}

				//sent socket if cpu load < 40
				if(CPULoad < (double)40){
					Runnable requestHandler = new RequestHandler(socket.accept(), instances[i]);
					executorService.execute(requestHandler);
					}
				j++;
				}
		}
	}

	private Double getCPULoad(int i) throws IOException {
		// TODO Auto-generated method stub
		URL urlDC = new URL(instances[i].getUrl()+":8080/info/cpu");
		
		//get cpu load from html
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
		System.out.println(i+" : "+CPULoad);
		return CPULoad;	
	}
	
	
}

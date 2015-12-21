import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.sql.Timestamp;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class Coordinator extends Verticle {

	//Default mode: replication. Possible string values are "replication" and "sharding"
	private static String storageType = "replication";


	public class OpThread implements Comparable<OpThread>{
		Thread thread;
		long timestamp;
		
		//Set-up the Operation Thread and override compareTo to let the thread
		//can be compared in the queue and ordered by timestamp
		public OpThread(Thread thread, long timestamp){
			this.timestamp = timestamp;
			this.thread = thread;
		}
		
		@Override
		public int compareTo(OpThread e){
			return (this.timestamp < e.timestamp)?(-1):1;
		}
	}
	/**
	 * TODO: Set the values of the following variables to the DNS names of your
	 * three dataCenter instances
	 */
	
	//to store timestamp and operation :  PUT=true  GET=false
	private static ConcurrentHashMap<Long, Boolean> opTypeMap = new ConcurrentHashMap<>();
	//store key and operation threads
	private static ConcurrentHashMap<String, PriorityBlockingQueue<OpThread>> threadQueueMap = new ConcurrentHashMap<>();
	//store the operation flag, when the flag >=1, it can do GET, when do get
	//the flag will add after get the flag will -1 and close to 0
	//and flag = 0, it can do GET AND PUT
	//when doing PUT the flag will -1 and <0
	//flag < 0, it can only do NOTHING
	private static ConcurrentHashMap<String, AtomicInteger> opFlagMap = new ConcurrentHashMap<>();
	private static final String dataCenter1 = "ec2-54-165-158-60.compute-1.amazonaws.com";
	private static final String dataCenter2 = "ec2-54-165-121-220.compute-1.amazonaws.com";
	private static final String dataCenter3 = "ec2-54-152-17-141.compute-1.amazonaws.com";
	// PUT:true   GET:false
	static{
		Thread controlThread = new Thread(new Runnable(){
			@Override
			public void run(){
				for(;;){
					for(String key: threadQueueMap.keySet()){
						//GET THE KEY'S QUEUE
						PriorityBlockingQueue<OpThread> opQueue = threadQueueMap.get(key);
						if(!opQueue.isEmpty()){
							int flag = opFlagMap.get(key).intValue();
							// IF STATUS=0 AND OPERATION = PUT, DO PUT AND STATUS-1 = -1
							//NOTHING CAN BE DONE AFTER THAT
							if(flag == 0 && opTypeMap.get((opQueue.peek().timestamp)) == true){
								opFlagMap.get(key).decrementAndGet();
								//System.out.println("begin put!\t"+opQueue.peek().timestamp);
								opQueue.poll().thread.start();
							}
							
							//IF STATUS >= 0, CAN DO GET IN THE QUEUE UNTIL MEET THE PUT
							else if(flag >= 0){
								while(!opQueue.isEmpty()&&opTypeMap.get(opQueue.peek().timestamp) == false){
									//System.out.println("begin get!\t"+opQueue.peek().timestamp);
									opFlagMap.get(key).incrementAndGet();
									opQueue.poll().thread.start();
								}
							}
						}
					}
					try {
						Thread.sleep(5);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		});
		controlThread.start();
	}

	@Override
	public void start() {
		//DO NOT MODIFY THIS
		KeyValueLib.dataCenters.put(dataCenter1, 1);
		KeyValueLib.dataCenters.put(dataCenter2, 2);
		KeyValueLib.dataCenters.put(dataCenter3, 3);
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);
		routeMatcher.get("/put", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String value = map.get("value");
				//You may use the following timestamp for ordering requests
                final long timestamp = System.currentTimeMillis();
                //INITIAL threadQueueMap and put key and queue in
                threadQueueMap.putIfAbsent(key, new PriorityBlockingQueue<OpThread>());
                PriorityBlockingQueue<OpThread> opQueue = threadQueueMap.get(key);
                //initial opFlagMap and put key and status code in
                opFlagMap.putIfAbsent(key, new AtomicInteger());
                //initial opTypeMap and put timestamp and operation type in
                opTypeMap.putIfAbsent(timestamp, true);
                final AtomicInteger flag = opFlagMap.get(key);
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						if(storageType.equals("replication")){
							try{
								KeyValueLib.PUT(dataCenter1, key, value);
								KeyValueLib.PUT(dataCenter2, key, value);
								KeyValueLib.PUT(dataCenter3, key, value);
							}
							catch(Exception e){
							}
						}
						else if(storageType.equals("sharding")){
							//just add every str and mod3 to get set to the 
							//3-long array
							int sum = 0;
							for(int ch: key.toCharArray()){
								sum+=ch;
							};
							int hashKey = sum%3;
							try {
								switch ((hashKey)) {
								case 1:
									KeyValueLib.PUT(dataCenter1, key, value);
									break;
								case 2:
									KeyValueLib.PUT(dataCenter2, key, value);
									break;
								case 0:
									KeyValueLib.PUT(dataCenter3, key, value);
									break;
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						//reset the status code to 0
						flag.incrementAndGet();
						System.out.println("putting\t"+key+"\t"+value+"\t"+flag+"\t"+timestamp);}
				});
				OpThread opThread = new OpThread(t, timestamp);
				System.out.println("PUT TO QUEUE\t"+key+"\t"+value+"\t"+flag+"\t"+timestamp);
				opQueue.offer(opThread);
				req.response().end(); //Do not remove this
			}
		});

		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String loc = map.get("loc");
				//INITIAL threadQueueMap and put key and queue in
				threadQueueMap.putIfAbsent(key, new PriorityBlockingQueue<OpThread>());
				//initial opFlagMap and put key and status code in
				opFlagMap.putIfAbsent(key, new AtomicInteger());
				final AtomicInteger flag = opFlagMap.get(key);
				PriorityBlockingQueue<OpThread> opQueue = threadQueueMap.get(key);
				//You may use the following timestamp for ordering requests
				final long timestamp = System.currentTimeMillis();
				//initial opTypeMap and put timestamp and operation type in
				opTypeMap.putIfAbsent(timestamp, false);
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						String value = "";
						if(storageType.equals("replication")){
							try {
								if(loc.equals("1"))
									value = KeyValueLib.GET(dataCenter1, key);
								else if(loc.equals("2"))
									value = KeyValueLib.GET(dataCenter2, key);
								else 
									value = KeyValueLib.GET(dataCenter3, key);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						if(storageType.equals("sharding")){
							if(loc.equals(null)){
								try {
									int sum = 0;
									for(int ch: key.toCharArray()){
										sum+=ch;
									};
									//just add every str and mod3 to get set to the 
									//3-long array
									int hashKey = sum%3;
									if(hashKey == 1)
										value = KeyValueLib.GET(dataCenter1, key);
									else if(hashKey == 2)
										value = KeyValueLib.GET(dataCenter2, key);
									else if(hashKey == 0)
										value = KeyValueLib.GET(dataCenter3, key);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
							else{
								try {
									if(loc.equals("1"))
										value = KeyValueLib.GET(dataCenter1, key);
									else if(loc.equals("2"))
										value = KeyValueLib.GET(dataCenter2, key);
									else 
										value = KeyValueLib.GET(dataCenter3, key);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
							
						}
						
						System.out.println("getting\t"+key+"\t"+value+"\t"+flag+"\t"+timestamp);
						//reset the status code to 0
						flag.decrementAndGet();
						
						req.response().end(value); 
					}
				});
				OpThread opThread = new OpThread(t ,timestamp);
				
				System.out.println("GET TO QUEUE\t" +key+"\t"+flag+"\t"+timestamp);
				
				opQueue.offer(opThread);
			}
		});

		routeMatcher.get("/storage", new Handler<HttpServerRequest>() {
                        @Override
                        public void handle(final HttpServerRequest req) {
                                MultiMap map = req.params();
                                storageType = map.get("storage");
                                //This endpoint will be used by the auto-grader to set the 
				//consistency type that your key-value store has to support.
                                //You can initialize/re-initialize the required data structures here
                                req.response().end();
                        }
                });

		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().putHeader("Content-Type", "text/html");
				String response = "Not found.";
				req.response().putHeader("Content-Length",
						String.valueOf(response.length()));
				req.response().end(response);
				req.response().close();
			}
		});
		server.requestHandler(routeMatcher);
		server.listen(8080);
	}
}

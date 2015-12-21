import java.awt.RenderingHints.Key;
import java.nio.charset.MalformedInputException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;

import org.omg.CORBA.ValueMember;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class KeyValueStore extends Verticle {
	//store the operation flag, when the flag >=1, it can do GET, when do get
		//the flag will add after get the flag will -1 and close to 0
		//and flag = 0, it can do GET AND PUT
		//when doing PUT the flag will -1 and <0
		//flag < 0, it can only do NOTHING
	private static ConcurrentHashMap<String, ArrayList<StoreValue>> store = null;
	private static ConcurrentHashMap<Long, Boolean> opTypeMap = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String , PriorityBlockingQueue<OpThread>> threadQueueMap = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, AtomicInteger>opFlagMap = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, PriorityBlockingQueue<Long>> aheadMap = new ConcurrentHashMap<>();
	public KeyValueStore() {
		store = new ConcurrentHashMap<String, ArrayList<StoreValue>>();	
	}
	public class OpThread implements Comparable<OpThread>{
		Thread thread;
		long timestamp;
		int opType;
		public OpThread(Thread thread, long timestamp, int opType){
			this.timestamp = timestamp;
			this.thread = thread;
			this.opType = opType;
		}
		public int  compareTo(OpThread e) {
			return (this.timestamp < e.timestamp)?(-1):1;			
		}
	}
	
	static{
		Thread ctrlThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(;;){
					for(String key: threadQueueMap.keySet()){
						PriorityBlockingQueue<OpThread> opThreads = threadQueueMap.get(key);
						int flag = opFlagMap.get(key).intValue();
						if(!opThreads.isEmpty()){
							// IF STATUS=0 AND OPERATION = PUT, DO PUT AND STATUS-1 = -1
							//NOTHING CAN BE DONE AFTER THAT
							long time = opThreads.peek().timestamp;
							long limit = 70000;
							if(aheadMap.containsKey(key)&&!aheadMap.get(key).isEmpty()){
								limit = aheadMap.get(key).peek().longValue();
							}
							if(time <= limit){
								if(flag ==0 && opThreads.peek().opType == 1){
									opFlagMap.get(key).decrementAndGet();
									opThreads.poll().thread.start();
								}
								//IF STATUS >= 0, CAN DO GET IN THE QUEUE UNTIL MEET THE PUT
								else if(flag != -1){
									while(!opThreads.isEmpty()&&opThreads.peek().opType==0){
										if(opThreads.peek().timestamp <= limit){
											opFlagMap.get(key).incrementAndGet();
											opThreads.poll().thread.start();
											if(!aheadMap.get(key).isEmpty()){
												limit=aheadMap.get(key).peek().longValue();
											}
										}
										else
											break;
									}
								}
							}
						}
						
					}
					try {
						Thread.sleep(500);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		});
		ctrlThread.start();
	}
	

	@Override
	public void start() {
		final KeyValueStore keyValueStore = new KeyValueStore();
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
				String value = map.get("value");
				String consistency = map.get("consistency");
				Integer region = Integer.parseInt(map.get("region"));
				Long timestamp = Long.parseLong(map.get("timestamp"));
				timestamp = Skews.handleSkew(timestamp, region);
				/* TODO: You will need to adjust the timestamp here for some consistency levels */
				System.out.println("PUT--------"+timestamp+"\t"+key+"\t"+value);;
				final StoreValue sv = new StoreValue(timestamp, value);
			
				if(consistency.equals("eventual")){
					synchronized(this){
						if(!keyValueStore.store.containsKey(key)){
							ArrayList<StoreValue> valueMore = new ArrayList<>();
							valueMore.add(sv);
							keyValueStore.store.put(key,valueMore);
						}
						else{
							keyValueStore.store.get(key).add(sv);
						}
					}
					String response = "stored";
					req.response().putHeader("Content-Type", "text/plain");
					req.response().putHeader("Content-Length",
							String.valueOf(response.length()));
					req.response().end(response);
					req.response().close();
				}
				
				else if(consistency.equals("strong")){
					threadQueueMap.putIfAbsent(key, new PriorityBlockingQueue<OpThread>());
					PriorityBlockingQueue<OpThread> opThreads = threadQueueMap.get(key);
					opTypeMap.putIfAbsent(timestamp, true);

					opFlagMap.putIfAbsent(key, new AtomicInteger());
					final AtomicInteger flag = opFlagMap.get(key);
					System.out.println("PUT\t:"+key+" value:"+value+" region:"+region+" time:"+timestamp);
					Thread t = new Thread(new Runnable() {
						public void run() {
							keyValueStore.store.putIfAbsent(key, new ArrayList<StoreValue>());
							keyValueStore.store.get(key).add(sv);
							System.out.println("put end");
							flag.incrementAndGet();
							String response = "stored";
							req.response().putHeader("Content-Type", "text/plain");
							req.response().putHeader("Content-Length",
									String.valueOf(response.length()));
							req.response().end(response);
							req.response().close();
						}
					});
					OpThread opThread = new OpThread(t, timestamp, 1);
					opThreads.offer(opThread);
				}
				if(consistency.equals("causal")){
					synchronized(this){
						if(!keyValueStore.store.containsKey(key)){
							ArrayList<StoreValue> valueMore = new ArrayList<>();
							valueMore.add(sv);
							keyValueStore.store.put(key,valueMore);
						}
						else{
							keyValueStore.store.get(key).add(sv);
						}
					}
					String response = "stored";
					req.response().putHeader("Content-Type", "text/plain");
					req.response().putHeader("Content-Length",
							String.valueOf(response.length()));
					req.response().end(response);
					req.response().close();
				}

				/* TODO: Add code to store the object here. You may need to adjust the timestamp */

				
			}
		});
		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String consistency = map.get("consistency");
				final Long timestamp = Long.parseLong(map.get("timestamp"));
				
				/* TODO: Add code here to get the list of StoreValue associated with the key 
				 * Remember that you may need to implement some locking on certain consistency levels */
				if(consistency.equals("eventual")){
					ArrayList<StoreValue> values = null;
					synchronized(this){
						values = keyValueStore.store.get(key);
					}
					String response = "";
					if (values != null) {
						for (StoreValue val : values) {
							response = response + val.getValue() + " ";
						}
					}
					req.response().putHeader("Content-Type", "text/plain");
					if (response != null)
						req.response().putHeader("Content-Length",
								String.valueOf(response.length()));
					req.response().end(response);
					req.response().close();
				}
				else if(consistency.equals("strong")){
					threadQueueMap.putIfAbsent(key, new PriorityBlockingQueue<OpThread>());
					opTypeMap.putIfAbsent(timestamp, false);
					PriorityBlockingQueue<OpThread> opThreads = threadQueueMap.get(key);
					
					opFlagMap.putIfAbsent(key, new AtomicInteger());
					final AtomicInteger flag = opFlagMap.get(key);
					Thread t = new Thread(new Runnable() {
						public void run() {
							ArrayList<StoreValue> values = null;
							values = keyValueStore.store.get(key);
							flag.decrementAndGet();
							String response = "";
							if (values != null) {
								for (StoreValue val : values) {
									response = response + val.getValue() + " ";
								}
							}
							req.response().putHeader("Content-Type", "text/plain");
							if (response != null)
								req.response().putHeader("Content-Length",
										String.valueOf(response.length()));
							req.response().end(response);
							req.response().close();
						}
					});
					OpThread opThread = new OpThread(t, timestamp, 0);
					opThreads.offer(opThread);
				}
				
				else if(consistency.equals("causal")){
					ArrayList<StoreValue> values = null;
					synchronized(this){
						values = keyValueStore.store.get(key);
					}
					String response = "";
					if (values != null) {
						for (StoreValue val : values) {
							response = response + val.getValue() + " ";
						}
					}
					req.response().putHeader("Content-Type", "text/plain");
					if (response != null)
						req.response().putHeader("Content-Length",
								String.valueOf(response.length()));
					req.response().end(response);
					req.response().close();
				}
				/* Do NOT change the format the response. It will return a string of
				 * values separated by spaces */
				
			}
		});
		// Handler for when the AHEAD is called
		routeMatcher.get("/ahead", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final Long timestamp = Long.parseLong(map.get("timestamp"));
				Thread t = new Thread(new Runnable() {
					public void run() {
						aheadMap.putIfAbsent(key, new PriorityBlockingQueue<Long>());
						PriorityBlockingQueue<Long> aheads = aheadMap.get(key);
						Long limit = timestamp;
						aheads.offer(limit);
						System.out.println("get AHEAD: "+key+" timestamp:"+timestamp);
					}
				});
				t.start();
				
				
				req.response().putHeader("Content-Type", "text/plain");
				req.response().end();
				req.response().close();
			}
		});
		// Handler for when the COMPLETE is called
		routeMatcher.get("/complete", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final Long timestamp = Long.parseLong(map.get("timestamp"));
				Thread t = new Thread(new Runnable() {
					public void run() {
						aheadMap.get(key).poll();
						System.out.println("get COM: "+key+" timestamp:"+timestamp);
					}
				});
				t.start();
				req.response().putHeader("Content-Type", "text/plain");
				req.response().end();
				req.response().close();
			}
		});
		// Clears this stored keys. Do not change this
		routeMatcher.get("/reset", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				keyValueStore.store.clear();
				req.response().putHeader("Content-Type", "text/plain");
				req.response().end();
				req.response().close();
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

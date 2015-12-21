import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import javax.print.attribute.standard.RequestingUserName;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.transform.Templates;

import java.util.LinkedList;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class Coordinator extends Verticle {

	// This integer variable tells you what region you are in
	// 1 for US-E, 2 for US-W, 3 for Singapore
	private static int region = KeyValueLib.region;

	// Default mode: Strongly consistent
	// Options: causal, eventual, strong
	private static String consistencyType = "strong";

	/**
	 * TODO: Set the values of the following variables to the DNS names of your
	 * three dataCenter instances. Be sure to match the regions with their DNS!
	 * Do the same for the 3 Coordinators as well.
	 */
	private static final String dataCenterUSE = "ec2-54-152-149-143.compute-1.amazonaws.com";
	private static final String dataCenterUSW = "ec2-52-91-39-22.compute-1.amazonaws.com";
	private static final String dataCenterSING = "ec2-54-208-107-237.compute-1.amazonaws.com";

	private static final String coordinatorUSE = "ec2-54-152-99-53.compute-1.amazonaws.com";
	private static final String coordinatorUSW = "ec2-54-85-205-97.compute-1.amazonaws.com";
	private static final String coordinatorSING = "ec2-54-88-124-70.compute-1.amazonaws.com";
	private static final String[] DCdns = {"123",dataCenterUSE,dataCenterUSW,dataCenterSING};
	private static final String[] COdns = {"123",coordinatorUSE,coordinatorUSW,coordinatorSING};

	public static int  hash(String string) {
		int sum = 0;
		for(char ch :string.toCharArray()){
			sum+=ch;
		}
		if(sum%3 ==0)
			return 3;
		else return sum%3;
	}
	@Override
	public void start() {
		KeyValueLib.RESET();
		KeyValueLib.dataCenters.put(dataCenterUSE, 1);
		KeyValueLib.dataCenters.put(dataCenterUSW, 2);
		KeyValueLib.dataCenters.put(dataCenterSING, 3);
		KeyValueLib.coordinators.put(coordinatorUSE, 1);
		KeyValueLib.coordinators.put(coordinatorUSW, 2);
		KeyValueLib.coordinators.put(coordinatorSING, 3);
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);

		routeMatcher.get("/put", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				final MultiMap map = req.params();
				final String key = map.get("key");
				final String value = map.get("value");
				final Long timestamp = Long.parseLong(map.get("timestamp"));
				final String forwarded = map.get("forward");
				final String forwardedRegion = map.get("region");
				Thread t = new Thread(new Runnable() {
					public void run() {
					/* TODO: Add code for PUT request handling here
					 * Each operation is handled in a new thread.
					 * Use of helper functions is highly recommended */
						final int DCnum = hash(key);	
						try {
							if(consistencyType.equals("eventual")){
								System.out.println("EVEN");
								if((forwarded == "true")||(Coordinator.region == DCnum)){
									System.out.println(Coordinator.region+"\tCOO PUT\t"+map.get("timestamp"));
									for(int i = 1;i<4;i++){
										final int I = i;
										new Thread(new  Runnable() {
											public void run() {
												try {
													KeyValueLib.PUT(Coordinator.DCdns[I],key,value,timestamp.toString(),consistencyType);
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}).start();
									}
								}
								else{
									System.out.println(Coordinator.region+"\tForward\t"+DCnum);
									KeyValueLib.FORWARD(COdns[DCnum],key,value,timestamp.toString());
								}
							}
							if(consistencyType.equals("strong")){
								System.out.println("STRONG");
								if(DCnum == Coordinator.region||forwarded == "true"){
									//use to make sure every put is done
									final CountDownLatch countDownLatch = new CountDownLatch(3);
									final Long time;
									if(forwarded == null){
										time = timestamp;
										try {
											KeyValueLib.AHEAD(key,time.toString());
										} catch (Exception e) {
											// TODO: handle exception
										}
									}
									else{
										time = Skews.handleSkew(timestamp.longValue(), Integer.parseInt(forwardedRegion));
									}
									try {
										for(int i = 1;i<4;i++){
											final int I = i;
											new Thread(new  Runnable() {
												public void run() {
													try {
														System.out.println("put  key:"+key+" value:"+value+" time: "+time);
														KeyValueLib.PUT(Coordinator.DCdns[I],key,value,time.toString(),consistencyType);
														countDownLatch.countDown();
													} catch (Exception e) {
														// TODO: handle exception
													}
													
												}
											}).start();
										}
										countDownLatch.await();
										System.out.println("COM key:"+key+" time:"+time);
										KeyValueLib.COMPLETE(key,time.toString());
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
								else{
									String FCooDNS = COdns[DCnum];
									try {
										KeyValueLib.AHEAD(key,timestamp.toString());
										KeyValueLib.FORWARD(FCooDNS,key,value,timestamp.toString());
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							}
							if(consistencyType.equals("causal")){
								System.out.println("CAUSUAL");
								if(DCnum == Coordinator.region||forwarded == "true"){
									final CountDownLatch countDownLatch = new CountDownLatch(3);
									final Long time = (forwarded != null)? Skews.handleSkew(timestamp.longValue(), Integer.parseInt(forwardedRegion)):timestamp;;;
									if(forwarded == null){
										try {
											KeyValueLib.AHEAD(key,time.toString());
										} catch (Exception e) {
											// TODO: handle exception
										}
									}
									
									try {
										//start put
										for(int i = 1;i<4;i++){
											final int I = i;
											new Thread(new  Runnable() {
												public void run() {
													try {
														System.out.println("put  key:"+key+" value:"+value+" time: "+time);
														KeyValueLib.PUT(Coordinator.DCdns[I],key,value,time.toString(),consistencyType);
														countDownLatch.countDown();
													} catch (Exception e) {
														// TODO: handle exception
													}
													
												}
											}).start();
										}
										countDownLatch.await();
										System.out.println("COM key:"+key+" time:"+time);
										KeyValueLib.COMPLETE(key,time.toString());
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
								else{
									String FCooDNS = COdns[DCnum];
									try {
										KeyValueLib.AHEAD(key,timestamp.toString());
										KeyValueLib.FORWARD(FCooDNS,key,value,timestamp.toString());
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
				t.start();
				req.response().end(); // Do not remove this
			}
		});

		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final Long timestamp = Long.parseLong(map.get("timestamp"));
				System.out.println("GET\t");
				
				Thread t = new Thread(new Runnable() {
					public void run() {
					/* TODO: Add code for GET requests handling here
					 * Each operation is handled in a new thread.
					 * Use of helper functions is highly recommended */
						String currentDNS = DCdns[region];
						try {
							String res = KeyValueLib.GET(currentDNS,key,timestamp.toString(),consistencyType);
							req.response().end(res);
						} catch (Exception e) {
							req.response().end("");
							// TODO: handle exception
						}
					}
				});
				t.start();
			}
		});
		/* This endpoint is used by the grader to change the consistency level */
		routeMatcher.get("/consistency", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				consistencyType = map.get("consistency");
				req.response().end();
			}
		});
		/* BONUS HANDLERS BELOW */
		routeMatcher.get("/forwardcount", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().end(KeyValueLib.COUNT());
			}
		});

		routeMatcher.get("/reset", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				KeyValueLib.RESET();
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

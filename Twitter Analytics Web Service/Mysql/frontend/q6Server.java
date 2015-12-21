import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Locale;
import java.util.Calendar;
import java.util.HashMap;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;
import java.sql.*;

import java.math.BigInteger;
import java.sql.SQLException;



public class Server extends Verticle {
	public static class Transaction {
		private AtomicInteger atomicInteger;
		private HashMap<Long, String> tagMap;
		private HashMap<Long, String> textMap;
		public Transaction() {
			atomicInteger = new AtomicInteger(1);
			tagMap = new HashMap<>();
			textMap = new HashMap<>();
		}
	}
	
	private static ConcurrentHashMap<Integer, Transaction> TransMap = new ConcurrentHashMap<>();	
	static final String DB_URL[] = new String[] {
			"jdbc:mysql://172.31.4.83:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8",
			"jdbc:mysql://172.31.9.33:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8"
	};
	static Connection[] conn = new com.mysql.jdbc.Connection[2];

	static Statement stmt = null;
	static final String USER = "root";
	static final String PASS = "laozhao";
	private static Item[] items = null;

	
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
            for(int i =0 ; i <2 ; i++){
				conn[i] = DriverManager.getConnection(DB_URL[i], USER, PASS);
			}
//System.out.println("----------------------------------"+conn2);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	@Override
	public void start() {

		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);

		routeMatcher.get("/", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {

				req.response().end(""); // Do not remove this
			}
		});

	

		routeMatcher.get("/q6", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				final MultiMap map = req.params();
				final int tid = Integer.parseInt(map.get("tid"));
				final String opt = map.get("opt");
				final String tag = map.get("tag");
				
				
				if(opt.charAt(0) == 'a'){
					req.response().end("i2.8xlarge,6775-2675-0992\n"+tag+"\n");
				}	
				if (opt.charAt(0) != 's' && opt.charAt(0) != 'e'){
					new Thread(new Runnable() {
						public void run() {
							int seq = Integer.parseInt(map.get("seq"));
							StringBuilder result = new StringBuilder();
							long tweetid = Long.parseLong(map.get("tweetid"));
//							long shard  = tweetid%10;
							long dbHash = tweetid%10;

							if(TransMap.get(tid)==null){
								TransMap.putIfAbsent(tid, new Transaction());
							}
							Transaction transaction = TransMap.get(tid);
							AtomicInteger atomicInteger = transaction.atomicInteger;
							HashMap<Long, String> tagMap = transaction.tagMap;
							HashMap<Long, String > textMap = transaction.textMap;
							
							if(opt.charAt(0) == 'r'){
								if(textMap.get(tweetid) == null){
									try {
										Statement stmt;
										String sql;
										int a = dbHash >4?1:0;
										stmt = conn[a].createStatement();
										sql = "SELECT text FROM ccq6db WHERE id = " + tweetid;
										ResultSet rs = stmt.executeQuery(sql);
										while (rs.next()) {
											result.append(rs.getString("text"));
											textMap.put(tweetid,rs.getString("text"));
										}
										rs.close();
										stmt.close();
									} catch (SQLException se) {
										se.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else 
									result.append(textMap.get(tweetid));			
							}
							synchronized(atomicInteger){
								while(atomicInteger.get() < seq){
									try {
										atomicInteger.wait();
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}								
								switch (opt.charAt(0)) {
								case 'a':									
									tagMap.put(tweetid, tag);
									break;
								case 'r':
									String currenttag = tagMap.get(tweetid);
									if(currenttag != null){
										result.append(currenttag).append("\n");
										req.response().end("i2.8xlarge,6775-2675-0992\n"+result.toString());
									} else {
										result.append("\n");
										req.response().end("i2.8xlarge,6775-2675-0992\n"+result.toString());
									}
									break;
								default:
									break;
								}
								if(seq ==  atomicInteger.get()){
									atomicInteger.incrementAndGet();
								}
								atomicInteger.notifyAll();
								if(seq == 5){
									TransMap.remove(tid);
								}
							}	
						}
					}).start();
				} else {
					req.response().end("i2.8xlarge,6775-2675-0992\n0\n");
				}
			}
		});	
	

		server.requestHandler(routeMatcher);
		server.listen(80);
	}
}

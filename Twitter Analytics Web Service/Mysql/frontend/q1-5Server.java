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

class Decoder {

	static int[][][] transMat = new int[50][][];

	static {
		for (int i = 0; i < 50; i++) {
			transMat[i] = new int[i][i];
		}

		for (int i = 1; i < 50; i++) {
			int count = 0;
			for (int v = 0; v < i; v++) {
				for (int h = 0; h < v + 1; h++) {
					transMat[i][h][v - h] = count;
					count++;
				}
			}
			for (int v = 1; v < i; v++) {
				for (int h = v; h < i; h++) {
					transMat[i][h][i - h + v - 1] = count;
					count++;
				}
			}
		}
	}

	public static final BigInteger b = new BigInteger(
			"8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773");
	public static final BigInteger twentyFive = new BigInteger("25");

	public static String decode(String product, String cString) {

		BigInteger a = new BigInteger(product);

		char[] cText = cString.toCharArray();
		char[] result = new char[cText.length];
		int tLength = cText.length;

		char offset = (char) (1 + a.divide(b).mod(twentyFive).intValue());

		int i = (int) Math.sqrt(tLength);

		for (int v = 0; v < i; v++) {
			for (int h = 0; h < i; h++) {
				int n = transMat[i][v][h];
				int ch = cText[i * v + h] - offset;
				result[n] = (char) (ch < 65 ? ch + 26 : ch);
			}
		}

		return new String(result);
	}
}

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
			"jdbc:mysql://172.31.4.83:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8",
			"jdbc:mysql://172.31.4.83:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8",
			"jdbc:mysql://172.31.4.83:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8",
			"jdbc:mysql://172.31.4.83:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8",
			"jdbc:mysql://172.31.9.33:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8",
			"jdbc:mysql://172.31.9.33:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8",
			"jdbc:mysql://172.31.9.33:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8",
			"jdbc:mysql://172.31.9.33:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8",
			"jdbc:mysql://172.31.9.33:3306/ccq6?useUnicode=yes&characterEncoding=UTF-8"
	};
	static Connection[] conn = new com.mysql.jdbc.Connection[10];
	
	
	
	static int number = 0;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL2 = "jdbc:mysql://localhost:3306/ccq2?useUnicode=yes&characterEncoding=UTF-8";
	static final String DB_URL3 = "jdbc:mysql://localhost:3306/ccq3?useUnicode=yes&characterEncoding=UTF-8";
	static final String DB_URL4 = "jdbc:mysql://localhost:3306/ccq4?useUnicode=yes&characterEncoding=UTF-8";
	static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
	static final DateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

	static Connection conn2 = null;
	static Connection conn3 = null;
	static Connection conn4 = null;
         static Statement stmt = null;
	static final String USER = "root";
	static final String PASS = "laozhao";
	private static Item[] items = null;

	
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn2 = DriverManager.getConnection(DB_URL2, USER, PASS);
			conn3 = DriverManager.getConnection(DB_URL3, USER, PASS);
			conn4 = DriverManager.getConnection(DB_URL4, USER, PASS);
                       for(int i =0 ; i <10 ; i++){
				conn[i] = DriverManager.getConnection(DB_URL[i], USER, PASS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static SimpleDateFormat df;
	static Calendar cal = Calendar.getInstance();

	static {
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT-4"));

	}

	static {
		BufferedReader bufferedReader = null;
		String line = "";
		Item item = null;
		final int N = 53767998; // number of all valid userids
		items = new Item[N];
		int i = 0;

		try {
			bufferedReader = new BufferedReader(new FileReader("/home/ubuntu/q5/q5"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("finish breader");
		// create the array of all valid userids
		try {
			while ((line = bufferedReader.readLine()) != null) {
				String[] parts = line.split(",");
				item = new Item((int)(Long.parseLong(parts[0])-1000000000), Integer.parseInt(parts[1]));
				items[i++] = item;
				if (i % 1000000 == 0)
					System.out.println("i=" + i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("items[] created");

        try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static class Item {
		private int userId;
		private int acount; //accumulative count

		public Item(int userId, int acount) {
			super();
			this.userId = userId;
			this.acount = acount;
		}

		public String toString() {
			return "userId=" + userId + "\t" + "acount=" + acount;
		}
	}
    /*
	* binary search for the nearest low valid userid in the range
	*/
	public int binarySearchLow(Item[] a, int key) {

		int low = 0;
		int high = a.length - 1;
		int mid;
		// return index
		if (key < a[0].userId)
			return 0;
		else if (key > a[a.length - 1].userId)
			return a.length - 1;
		else {
			while (low <= high) {
				mid = (low + high) / 2;
				if (key == a[mid].userId)
					return mid;
				if (key < a[mid].userId)
					high = mid - 1;
				else
					low = mid + 1;
			}
			return low;

		}
	}
   /*
	* binary search for the nearest high valid userid in the range
	*/
	public int binarySearchHigh(Item[] a, int key) {

		int low = 0;
		int high = a.length - 1;
		int mid;
		// return index
		if (key < a[0].userId)
			return 0;
		else if (key > a[a.length - 1].userId)
			return a.length - 1;
		else {
			while (low <= high) {
				mid = (low + high) / 2;
				if (key == a[mid].userId)
					return mid;
				if (key < a[mid].userId)
					high = mid - 1;
				else
					low = mid + 1;
			}
			return high;

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

		routeMatcher.get("/q1", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				String number = map.get("key");

				String msg = map.get("message");

				req.response().end("i2.8xlarge,6775-2675-0992\n" + df.format(cal.getTime()) + "\n"
						+ Decoder.decode(number, msg) + "\n");
			}
		});

		routeMatcher.get("/q3", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().end("i2.8xlarge,6775-2675-0992\nPositive Tweets\n\nNegative Tweets\n");
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
										stmt = conn[(int) dbHash].createStatement();
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
		routeMatcher.get("/q2", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String id = map.get("userid");
				final String raw_time = map.get("tweet_time");

				java.util.Date parsedDate = null;
				try {
					parsedDate = myFormat.parse(raw_time);

				} catch (Exception e) {
				}
				final long time = parsedDate.getTime() / 1000;
				StringBuilder result = new StringBuilder();
				result.append("i2.8xlarge,6775-2675-0992\n");

				try {
                                       	stmt = conn2.createStatement();

					String sql = "SELECT text FROM ccq2db WHERE userid = " + id + " AND timestamp = " + time + "";
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						result.append(rs.getString("text")).append('\n');
					}

					rs.close();
					stmt.close();
				} catch (SQLException se) {

					se.printStackTrace();
				} catch (Exception e) {

					e.printStackTrace();
				}

				req.response().putHeader("content-type", "application/json; charset=utf-8");
				req.response().end(result.toString());  
			
                               }
		});

		routeMatcher.get("/q4", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String hashtag = map.get("hashtag");
				final String n = map.get("n");

				StringBuilder result = new StringBuilder();
				result.append("i2.8xlarge,6775-2675-0992\n");

				try {

					stmt = conn4.createStatement();
					String sql = "SELECT text FROM ccq4db WHERE hashtag = '" + hashtag + "' LIMIT " + n;
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						result.append(rs.getString("text")).append('\n');
					}

					rs.close();
					stmt.close();
				} catch (SQLException se) {

					se.printStackTrace();
				} catch (Exception e) {

					e.printStackTrace();
				}

				req.response().putHeader("content-type", "application/json; charset=utf-8");
				req.response().end(result.toString());
			}
		});

		routeMatcher.get("/q5", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String userid_min = map.get("userid_min");
				final String userid_max = map.get("userid_max");

				StringBuilder result = new StringBuilder();
				result.append("i2.8xlarge,6775-2675-0992\n");
                // binary search for the nearest valid userid in the range
				// to avoid exceeding int_max, deduct 1000000000 from original userids
				int _userid_min = binarySearchLow(items, (int)(Long.parseLong(userid_min)-1000000000));
				int _userid_max = binarySearchHigh(items, (int)(Long.parseLong(userid_max)-1000000000));
				// if _userid_min==0, then set 0
				int acount_min = _userid_min==0?0:items[_userid_min-1].acount;  //
				int acount_max = items[_userid_max].acount;
				result.append((acount_max - acount_min) + "\n");
				req.response().putHeader("content-type", "application/json; charset=utf-8");
				req.response().end(result.toString());
			}
		});

		server.requestHandler(routeMatcher);
		server.listen(80);
	}
}

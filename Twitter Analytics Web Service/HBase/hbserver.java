import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.TimeZone;
import java.math.BigInteger;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;


//this is the decoder for q1
//and return the decoded msg
class Decoder {

	static int[][][] transMat = new int[50][][];
	
	static {
		for (int i = 0; i < 50; i++) {
			transMat[i] = new int[i][i];
		}
		
		for (int i = 1; i < 50; i++) {
			int count = 0;
			for(int v = 0; v < i; v++){
				for(int h = 0; h < v+1; h++){
					transMat[i][h][v-h] = count;
					count++;
				}	
			} 
			for(int v =1; v<i; v++){
				for(int h =v; h < i; h++){
					transMat[i][h][i-h+v-1]=count;
					count++;
				}
			}
		}
	}
	
	public static final BigInteger b = new BigInteger("8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773");
	public static final BigInteger twentyFive = new BigInteger("25");

	public static String decode(String product, String cString) {
		BigInteger a = new BigInteger(product);

		char[] cText = cString.toCharArray();
		char[]result = new char[cText.length];
		int tLength = cText.length;

		char offset = (char)(1 + a.divide(b).mod(twentyFive).intValue());
		
		int i = (int) Math.sqrt(tLength);
		
		for(int v = 0; v < i; v++){
			for(int h = 0; h < i; h++){
				int n = transMat[i][v][h];
				int ch = cText[i*v+h] - offset;
				result[n] = (char)(ch < 65 ? ch + 26 : ch);
			}
		}
		
		return new String(result);
	}
}


public class ServerHBase extends Verticle {
  		
		static HConnection connection = null;
		static HTableInterface table = null;
		static HTableInterface table2 = null;
		static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

		//whenever load the code, just setup connection first
  		static{
  			Configuration configuration = HBaseConfiguration.create();
			try {
				//set the configuration and start the connection
				configuration.set("hbase.zookeeper.property.clientPort", "2181");
				configuration.set("hbase.zookeeper.quorum", "172.31.1.31");
				configuration.set("hbase.master", "172.31.1.31:60000");
				connection = HConnectionManager.createConnection(configuration);
				table = connection.getTable("q2");
				table2 = connection.getTable("q4");
			} catch (ZooKeeperConnectionException e1) {
	// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (IOException e) {
	// TODO Auto-generated catch block
				e.printStackTrace();
			}
  		}

		static SimpleDateFormat df;
		static Calendar cal = Calendar.getInstance();
		static {
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("GMT-4"));
		
		}


		
		@Override
		public void start() {
		
			final RouteMatcher routeMatcher = new RouteMatcher();
			final HttpServer server = vertx.createHttpServer();
			
			//main page for health check
			routeMatcher.get("/", new Handler<HttpServerRequest>() {
				@Override
				public void handle(final HttpServerRequest req) {
					
					req.response().end(""); 
				}
			});

			//response for q1
			routeMatcher.get("/q1", new Handler<HttpServerRequest>() {
				@Override
				public void handle(final HttpServerRequest req) {
					MultiMap map = req.params();
					String number = map.get("key");
					String msg = map.get("message");
					req.response().end("i2.8xlarge,6775-2675-0992\n" + df.format(cal.getTime())  + "\n" + Decoder.decode(number, msg) + "\n");
				}
			});	

			//response for q3
			//becasue we fail to load data, just reply null
		   routeMatcher.get("/q3", new Handler<HttpServerRequest>() {
                @Override
                public void handle(final HttpServerRequest req) {
                	req.response().end("i2.8xlarge,6775-2675-0992\nPositive Tweets\n\nNegative Tweets\n");
                }
             });

		   	//response for q2
		  	//just combine userid and timestamp and use it to get output format data
			routeMatcher.get("/q2", new Handler<HttpServerRequest>() {
				@Override
				public void handle(final HttpServerRequest req) {
					MultiMap map = req.params();
					final String rTime = map.get("tweet_time");
					final String id = map.get("userid");
					java.util.Date parsedDate = null;
					try {
						pDate = format.parse(rTime);			
					}catch (Exception e) {
						
					}
					final String time = Long.toString(pDate.getTime()/1000);
					Get get = new Get(Bytes.toBytes(id+time));  
			        Result result = null;
					try {
						result = table.get(get);
		        		String text = null;
	        			for (KeyValue keyValue : result.raw()) {  		            
			        		String qualifier = new String(keyValue.getQualifier());
			        		if(qualifier.equals("text")) {
			        			text = new String(keyValue.getValue()).replace("%&^$&%$&(&^",",").replace("\\n","\n");
			        		}        
				        }  			        
				        response = "i2.8xlarge,6775-2675-0992\n"+text +"\n";
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
					req.response().putHeader("content-type", "application/json; charset=utf-8");
					req.response().end(response);	
				}
			});
			

			//response for the q4
			//combine hashtag and n    
			//just get data from hbase
			routeMatcher.get("/q4", new Handler<HttpServerRequest>() {
				@Override
				public void handle(final HttpServerRequest req) {
					MultiMap map = req.params();
					final String hashtag = map.get("hashtag");
					final String n = map.get("n");
					Get get = new Get(Bytes.toBytes(hashtag+"+"+n));
			        Result result = null;
					try {
						result = table2.get(get);
		        		String text = null;
	        			for (KeyValue keyValue : result.raw()) {  		            
			        		String qualifier = new String(keyValue.getQualifier());	
			        		if(qualifier.equals("text")) {
			        			//System.out.println(new String(keyValue.getValue()));
			        			text = new String(keyValue.getValue()).replace("%&^$&%$&(&^",",").replace("\\\\n","\n").replace("\\n","\n");
			        		}        
			        	}  			        
				        response = "i2.8xlarge,6775-2675-0992\n"+text ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
					req.response().putHeader("content-type", "application/json; charset=utf-8");
					req.response().end(response);		
				}
			});
		
	
			server.requestHandler(routeMatcher);
			server.listen(80);
		}
}

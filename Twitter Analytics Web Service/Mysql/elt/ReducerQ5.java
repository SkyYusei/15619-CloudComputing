import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.google.gson.Gson;

public class Reducer {
	

	
	static final Gson GSON = new Gson();

	
	public static void main(String[] args) {
		
		BufferedReader bufferedReader = null;
		PrintStream bufferedWriter = null;
		String userid = "";
		String prevUser = "";
		HashSet<String> tweetIDs = new HashSet<>();
		
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
			bufferedWriter = new PrintStream(System.out, true, "UTF-8");
			String inputLine = null;
			int count = 0;
			while ((inputLine = bufferedReader.readLine()) != null) {
				if (inputLine.length() < 1) {
					break;
				}
				String[] strings = inputLine.split("\\t");
				userid = strings[0];
				if (prevUser.length() == 0 || prevUser.equals(userid)) {
					if (tweetIDs.add(strings[1])) {
						++count;
					}
				} else {
					
					bufferedWriter.println(prevUser + "\t" + count);
					count = 1;
					tweetIDs.clear();
				}	
				prevUser = userid;
			}
			bufferedWriter.println(userid + "\t" + count);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}

}


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.ParseException;

import com.google.gson.Gson;

public class Mapper {
	
	static class RawTweet {
		private long id;
		private String id_str;
		private User user;


		public RawTweet() {
			this.id = -1;
			this.id_str = null;
			this.user = null;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getId_str() {
			return id_str;
		}

		public void setId_str(String id_str) {
			this.id_str = id_str;
		}


		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		
		public boolean checkValidation() throws ParseException {
			if (id <= 0) {
				if (id_str.length() == 0) {
					return false;
				} else {
					id = Long.parseLong(id_str);
				}
			}
			
			if (user == null || !user.checkValidation()) {
				return false;
			}
			
			return true;
		}

		@Override
		public String toString() {
			return "RawTweet [id=" + id + ", id_str=" + id_str
					+ ", user=" + user + "]";
		}
	}
	
	static class User {
		private long id;
		private String id_str;

		
		@Override
		public String toString() {
			return "User [id=" + id + ", id_str=" + id_str
					+ ", ]";
		}

		public boolean checkValidation() {
			if (id <= 0) {
				if (id_str.length() == 0) {
					return false;
				} else {
					id = Long.parseLong(id_str);
				}
			}
			return true;
		}
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getId_str() {
			return id_str;
		}
		public void setId_str(String id_str) {
			this.id_str = id_str;
		}
		public User() {
			this.id = -1;
			this.id_str = null;
		}

		
	}
	
	private static final Gson GSON = new Gson();
	
	public static void main(String[] args) {
		BufferedReader bufferedReader = null;
		PrintStream bufferedWriter = null;

		try {

			bufferedReader = new BufferedReader(
					new InputStreamReader(System.in, "utf-8"));
			bufferedWriter = new PrintStream(System.out, true, "UTF-8");

			String inputLine = null;
			while ((inputLine = bufferedReader.readLine()) != null) {

				RawTweet realTweet = GSON.fromJson(inputLine, RawTweet.class);
				
				if (realTweet != null && realTweet.checkValidation()) {
					bufferedWriter.println(realTweet.user.id+"\t"+ realTweet.id);
				}
			}

			bufferedReader.close();
			bufferedWriter.close();
		
		} catch (Exception e) {

		}
	}

}



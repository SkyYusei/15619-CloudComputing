import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import com.google.gson.Gson;


public class Mapper {
	
	static class RawTweet {
		private long id;
		private String id_str;
		private String text;
		

		public RawTweet() {
			this.id = -1;
			this.id_str = null;
			this.text = null;
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

		
		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
		
		public boolean checkValidation() throws ParseException {
			if (id <= 0) {
				if (id_str.length() == 0) {
					return false;
				} else {
					id = Long.parseLong(id_str);
				}
			}
			
			return true;
		}

		@Override
		public String toString() {
			return "RawTweet [id=" + id + ", id_str=" + id_str
					+ ", created_at="
					+ ", text=" + text + "]";
		}
	}
	

	
	static class RealTweet {
		private long id;
		private String text;
		
		public RealTweet(RawTweet rawTweet) {
			this.id = rawTweet.id;
			this.text = rawTweet.text;
		}
		

		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}

		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}
	
	private static final Gson GSON = new Gson();
	
	public static void main(String[] args) {

		BufferedReader bufferedReader = null;
		PrintStream bufferedWriter = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
			bufferedWriter = new PrintStream(System.out, true, "UTF-8");
			
			String inputLine = null;
			while ((inputLine = bufferedReader.readLine()) != null) {
				RawTweet rawTweet = GSON.fromJson(inputLine, RawTweet.class);
				if (rawTweet != null && rawTweet.checkValidation()) {
					RealTweet realTweet = new RealTweet(rawTweet);
					
					bufferedWriter.println(rawTweet.id + "\t" + GSON.toJson(realTweet));
				} 
			}
		} catch (Exception e) {
		} finally {
			try {
				bufferedReader.close();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class Reducer {
	
	
	static class Tweet {
		private long id;
		private String text;

		
		public Tweet() {}

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
	
	
	static final HashMap<String, Integer> SCORE_HASH_MAP = new HashMap<>();
	
	static final HashMap<Integer, String> STARMAP_HASH_MAP = new HashMap<>();
	
	static final Pattern pattern = Pattern.compile("[a-zA-Z0-9]+", Pattern.MULTILINE | Pattern.UNICODE_CHARACTER_CLASS);
	
	static final Gson GSON = new Gson();
	
	static final HashSet<String> BAN_LIST_SET = new HashSet<String>(Arrays.asList(new String[]{
			"15619cctest", "4r5e", "5h1t", "5hit", "goddamn", "a55", "anal", "anus", "arse", 
			"ass", "assfucker", "assfukka", "assho", "assram", "asswhole", "b!tch", "b00bs", 
			"b17ch", "b1tch", "balls", "ballsack", "bastard", "beastial", "beastiality", "bellend", 
			"bestial", "bestiality", "biatch", "bitch", "bloody", "blowjob", "blowjobs", "boiolas", 
			"bollock", "bollok", "boner", "boob", "boobs", "booobs", "boooobs", "booooobs", "booooooobs", 
			"breasts", "buceta", "bugger", "bum", "bunnyfucker", "butt", "buttmuch", "buttplug", 
			"c0ck", "c0cksucker", "carpetmuncher", "cawk", "chink", "cipa", "cl1t", "clit", "clitoris", 
			"clits", "cnut", "cock", "cockface", "cockhead", "cockmunch", "cockmuncher", "cocks", 
			"cocksuck", "cocksucked", "cocksucker", "cocksucking", "cocksucks", "cocksuka", "cocksukka",
			"cok", "cokmuncher", "coksucka", "coon", "cox", "crap", "cum", "cummer", "cumming", 
			"cums", "cumshot", "cunilingus", "cunillingus", "cunnilingus", "cunt", "cuntlick", "cuntlicker", 
			"cuntlicking", "cunts", "cyalis", "cyberfuc", "cyberfuck", "cyberfucked", "cyberfucker", 
			"cyberfuckers", "cyberfucking", "d1ck", "damn", "dick", "dickhead", "dildo", "dildos", "dink", 
			"dinks", "dirsa", "dlck", "dogfucker", "doggin", "dogging", "donkeyribber", "doosh", "duche", 
			"dyke", "ejaculate", "ejaculated", "ejaculates", "ejaculating", "ejaculatings", "ejaculation", 
			"ejakulate", "f4nny", "fag", "fagging", "faggitt", "faggot", "faggs", "fagot", "fagots", "fags", 
			"fannyflaps", "fannyfucker", "fanyy", "fcuk", "fcuker", "fcuking", "feck", "fecker", "felching", 
			"fellate", "fellatio", "fingerfuck", "fingerfucked", "fingerfucker", "fingerfuckers", "fingerfucking",
			"fingerfucks", "fistfuck", "fistfucked", "fistfucker", "fistfuckers", "fistfucking", "fistfuckings", 
			"fistfucks", "flange", "fook", "fooker", "fuck", "fucka", "fucked", "fucker", "fuckers", "fuckhead", 
			"fuckheads", "fuckin", "fucking", "fuckings", "fuckingshitmotherfucker", "fuckme", "fucks", "fuckwhit", 
			"fuckwit", "fudgepacker", "fuk", "fuker", "fukker", "fukkin", "fuks", "fukwhit", "fukwit", "fux", "fux0r", 
			"gangbang", "gangbanged", "gangbangs", "gaysex", "goatse", "hardcoresex", "hell", "heshe", "hoar", "hoare", 
			"hoer", "homo", "hore", "horniest", "horny", "hotsex", "jackoff", "jap", "jerk", "jerkoff", "jism", "jiz", 
			"jizm", "jizz", "kawk", "knob", "knobead", "knobed", "knobend", "knobhead", "knobjocky", "knobjokey", "kock", 
			"kondum", "kondums", "kum", "kummer", "kumming", "kums", "kunilingus", "l3ich", "l3itch", "labia", "lmao", 
			"lmfao", "lust", "lusting", "m0f0", "m0fo", "m45terbate", "ma5terb8", "ma5terbate", "masochist", "masterb8", 
			"masterbat", "masterbat3", "masterbate", "masterbation", "masterbations", "masturbate", "mof0", "mofo", 
			"mothafuck", "mothafucka", "mothafuckas", "mothafuckaz", "mothafucked", "mothafucker", "mothafuckers", "mothafuckin", 
			"mothafucking", "mothafuckings", "mothafucks", "motherfuck", "motherfucked", "motherfucker", "motherfuckers", 
			"motherfuckin", "motherfucking", "motherfuckings", "motherfuckka", "motherfucks", "muff", "mutha", "muthafecker", 
			"muthafuckker", "muther", "mutherfucker", "n1gga", "n1gger", "nazi", "nigg3r", "nigg4h", "nigga", "niggah", 
			"niggas", "niggaz", "nigger", "niggers", "nobhead", "nobjocky", "nobjokey", "numbnuts", "nutsack", "omg", "p0rn",
			"pawn", "penis", "penisfucker", "phonesex", "phuck", "phuk", "phuked", "phuking", "phukked", "phukking", "phuks", 
			"phuq", "pigfucker", "pimpis", "piss", "pisser", "pissers", "pisses", "pissflaps", "pissin", "pissing", "pissoff",
			"poop", "prick", "pricks", "pron", "pube", "pusse", "pussi", "pussies", "pussy", "pussys", "queer", "rectum", 
			"rimjaw", "rimming", "schlong", "scroat", "scrote", "scrotum", "semen", "sex", "sh!t", "sh1t", "shit", "shitdick",
			"shite", "shited", "shitey", "shitfuck", "shitfull", "shithead", "shiting", "shitings", "shits", "shitted", "shitter",
			"shitters", "shitting", "shittings", "shitty", "skank", "slut", "smegma", "smut", "snatch", "sonofabitch", "spunk", 
			"teets", "tit", "titfuck", "tittiefucker", "titties", "tittyfuck", "tittywank", "titwank", "tosser", "turd", "tw4t", 
			"twat", "twathead", "twatty", "twunt", "twunter", "v14gra", "v1gra", "vagina", "viagra", "vulva", "w00se", "wang", 
			"wank", "wanker", "wanky", "whoar", "whore", "whore4r5e", "whoreanal", "whoreshit", "wtff"
		
	}));
	
	
	public static void main(String[] args) {
		
		
		BufferedReader bufferedReader = null;
		PrintStream bufferedWriter = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
			bufferedWriter = new PrintStream(System.out, true, "UTF-8");
			String inputLine = null;
			String prevId = "";
			while ((inputLine = bufferedReader.readLine()) != null) {
				String inputString = inputLine;
				
				int spacePos = inputString.indexOf('\t');
				String id = inputLine.substring(0, spacePos);
				
				if (id.equals(prevId)) {
					continue;
				}
				prevId = id;
				Tweet tweet = GSON.fromJson(inputString.substring(spacePos + 1), Tweet.class);	
				Matcher matcher = pattern.matcher(tweet.text);
				int start = 0;
				StringBuilder sBuilder = new StringBuilder();
				while (matcher.find()) {
					String token = matcher.group();
					String lowerToken = token.toLowerCase();
					int length = token.length();
					
					if (BAN_LIST_SET.contains(lowerToken)) {
						int wordStart = matcher.start();
						int wordEnd = matcher.end();
						sBuilder.append(tweet.text.substring(start, wordStart))
								.append(token.charAt(0))
								.append(STARMAP_HASH_MAP.get(length - 2))
								.append(token.charAt(length - 1));
						start = wordEnd;
					}
					
				}
				sBuilder.append(tweet.text.substring(start));
				tweet.text = sBuilder.toString();
				bufferedWriter.println(GSON.toJson(tweet));

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
	static {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < 30; i++) {
			STARMAP_HASH_MAP.put(i, stringBuilder.toString());
			stringBuilder.append('*');
		}
	}
}

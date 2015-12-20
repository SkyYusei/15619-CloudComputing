import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URLConnection;

public class Request {
	private final String path;
	private final Socket socket;
	private final DataCenterInstance instance;
	private final BufferedReader incomingRequestReader;
	private Response response;

	public Request(Socket socket, DataCenterInstance instance) throws IOException {
		this.socket = socket;
		this.instance = instance;
		this.incomingRequestReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.path = extractPath(incomingRequestReader);
	}

	public Response getResponse() {
		return response;
	}

	/**
	 * Execute this request and get a response back from the data center instance
	 * @throws IOException
	 */
	public void execute() throws IOException {
		String urlToCall = generateUrlToCall();
		URLConnection conn = instance.executeRequest(urlToCall);
		response = new ResponseBuilder().withResponseInputStream(conn.getInputStream())
				.withContentLength(conn.getContentLength())
				.withContentType(conn.getContentType())
				.withHttpStatusCode(((HttpURLConnection) conn).getResponseCode())
				.withHttpStatusResponseMessage(((HttpURLConnection) conn).getResponseMessage())
				.build();
	}

	/**
	 * Close all sockets and streams
	 * @throws IOException
	 */
	public void close() throws IOException {
		response.close();
		incomingRequestReader.close();
		socket.close();
	}

	/**
	 * Generate the url to call by appending the path to instance's dns
	 * @return url to call
	 */
	private String generateUrlToCall() {
		return new String(instance.getUrl() + path);
	}

	/**
	 * Extract the path from the incoming request
	 * @param inputStream
	 * @return path
	 * @throws IOException
	 */
	private String extractPath(BufferedReader inputStream) throws IOException {
		String inputLine = inputStream.readLine();
        if (inputLine==null || inputLine.equals("")){
            return "";
        }
		String[] tokens = inputLine.split(" ");
		return tokens[1];
	}
}

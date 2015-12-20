import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RequestHandler implements Runnable {
	private static final int BUFFER_SIZE = 32768;
	private final Socket socket;
	private final DataCenterInstance instance;

	public RequestHandler(Socket socket, DataCenterInstance instance) {
		this.socket = socket;
		this.instance = instance;
	}

	public void run() {
		try {
			Request request = new Request(socket, instance);
			request.execute();
			Response response = request.getResponse();
			sendToClient(response);
			request.close();
		} catch (Exception ex) {
			System.out.println("Exception occured when running RequestHandler: " + ex);
			ex.printStackTrace();
		}
	}

	/**
	 * Forward the Data Center Instance's response back to the Load Generator
	 * @param response
	 * @throws IOException
	 */
	private void sendToClient(Response response) throws IOException {
		DataOutputStream outputStreamToClient = new DataOutputStream(socket.getOutputStream());
		byte[] buffer = new byte[BUFFER_SIZE];

		int bytesRead = response.getResponseFromDataCenter().read(buffer, 0, BUFFER_SIZE);
		outputStreamToClient.write(response.generateHeaders().getBytes());
		outputStreamToClient.write(buffer, 0, bytesRead);
		outputStreamToClient.flush();
		outputStreamToClient.close();
	}
}

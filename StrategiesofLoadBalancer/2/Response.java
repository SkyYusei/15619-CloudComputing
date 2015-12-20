import java.io.IOException;
import java.io.InputStream;

public class Response {
	private final InputStream responseFromDataCenter;
	private final Integer contentLength;
	private final Integer httpStatusCode;
	private final String contentType;
	private final String httpStatusResponseMessage;

	public Response(InputStream responseFromDataCenter, Integer contentLength, Integer httpStatusCode,
			String contentType, String httpStatusResponseMessage) {
		this.responseFromDataCenter = responseFromDataCenter;
		this.contentLength = contentLength;
		this.httpStatusCode = httpStatusCode;
		this.contentType = contentType;
		this.httpStatusResponseMessage = httpStatusResponseMessage;
	}

	public InputStream getResponseFromDataCenter() {
		return responseFromDataCenter;
	}

	public Integer getContentLength() {
		return contentLength;
	}

	public Integer getHttpStatusCode() {
		return httpStatusCode;
	}

	public String getContentType() {
		return contentType;
	}

	public String getHttpStatusResponseMessage() {
		return httpStatusResponseMessage;
	}

	public void close() throws IOException {
		responseFromDataCenter.close();
	}

	public String generateHeaders() {
		String headers = "";
		headers += "HTTP/1.0 " + httpStatusCode + " " + httpStatusResponseMessage + "\r\n";
		headers += "Content-Type: " + contentType + "\r\n";
		headers += "Content-Length: " + contentLength + "\r\n";
		headers += "\r\n";

		return headers;
	}
}

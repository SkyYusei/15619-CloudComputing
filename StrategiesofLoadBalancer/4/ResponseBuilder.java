import java.io.InputStream;

public class ResponseBuilder {
	private InputStream responseFromDataCenter;
	private Integer contentLength;
	private Integer httpStatusCode;
	private String contentType;
	private String httpStatusResponseMessage;
	boolean alreadyUsed;

	public Response build() {
		if (alreadyUsed) {
			throw new IllegalStateException("Cannot reuse a builder");
		}
		alreadyUsed = true;
		return new Response(responseFromDataCenter, contentLength, httpStatusCode, contentType,
				httpStatusResponseMessage);
	}

	public ResponseBuilder withResponseInputStream(InputStream inputStream) {
		this.responseFromDataCenter = inputStream;
		return this;
	}

	public ResponseBuilder withContentLength(int contentLength) {
		this.contentLength = contentLength;
		return this;
	}

	public ResponseBuilder withHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
		return this;
	}

	public ResponseBuilder withContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public ResponseBuilder withHttpStatusResponseMessage(String httpStatusResponseMessage) {
		this.httpStatusResponseMessage = httpStatusResponseMessage;
		return this;
	}
}

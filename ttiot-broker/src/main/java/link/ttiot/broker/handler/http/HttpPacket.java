package link.ttiot.broker.handler.http;


import java.util.HashMap;
import java.util.Map;


public class HttpPacket   {


	private String headerString;

	protected Map<String, String> headers = new HashMap<>();

	public HttpPacket() {

	}

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	public void addHeaders(Map<String, String> headers) {
		if (headers != null) {
			this.headers.putAll(headers);
		}
	}

	public String getHeader(String key) {
		return headers.get(key);
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getHeaderString() {
		return headerString;
	}

	public void removeHeader(String key, String value) {
		headers.remove(key);
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setHeaderString(String headerString) {
		this.headerString = headerString;
	}


}

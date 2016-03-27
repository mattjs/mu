package com.mu.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class HttpResponse {
	private ResponseStatusCode code;
	private String body;
	
	private static String CLOSE_CONNECTION =
		new Header("Connection", "close").value();
	private String contentType =
		new ContentTypeHeader(ContentType.HTML, Charset.UTF8)
			.getHeader().value();
	private Header contentLength = new Header("Content-Length");

	public enum ContentType {
		HTML("text/html");
		
		private String value;
		
		private ContentType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
	
	public enum Charset {
		UTF8("UTF-8");
		
		private String value;
		
		private Charset(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	public class ContentTypeHeader {
		private ContentType contentType;
		private Charset charset;
		
		public ContentTypeHeader(ContentType contentType, Charset charset) {
			this.contentType = contentType;
			this.charset = charset;
		}
		
		public Header getHeader() {
			return new Header("Content-Type", getHeaderValue());
		}
		
		public String getHeaderValue() {
			String value = contentType.getValue();
			if (charset != null) {
				value += "; charset=" + charset.getValue();
			}
			return value;
		}
	}
	
	public void writeHttpResonse(OutputStream stream) throws IOException {
		PrintStream pstream = new PrintStream(stream);
		pstream.println(code.getResponseHeader());
		pstream.println(contentType);
		pstream.println(contentLength.value());
		pstream.println(CLOSE_CONNECTION);
		pstream.println();
		pstream.println(body);
		pstream.flush();
		pstream.close();
	}
	
	public void setResponseStatusCode(ResponseStatusCode code) {
		this.code = code;
	}
	
	public void setBody(String body) {
		this.body = body;
		contentLength.setValue(String.valueOf(body.length()));
	}
	
	public void setContentType(ContentTypeHeader contentTypeHeader) {
		contentType = contentTypeHeader.getHeader().value();
	}
}
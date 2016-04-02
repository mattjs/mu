package com.mu.http;

import java.io.OutputStream;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {
	private ResponseStatusCode code;
	private OutputStream outputStream;
	
	private static String closeConnection =
		new Header("Connection", "close").value();
	private Header contentLength = new Header("Content-Length");
	private Charset charset = Charset.UTF8;
	private ContentType contentType;

	public enum ContentType {
		TEXT("text/plain"),
		HTML("text/html"),
		CSS("text/css"),
		PNG("image/png"),
		PDF("application/pdf"),
		JS("text/javascript"),
		JSON("application/json");
		
		private String value;
		
		private ContentType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
	
	public static Map<String, ContentType> contentTypeByValue = 
		Arrays.asList(ContentType.values())
			.stream()
			.collect(Collectors.toMap(ct -> ct.getValue(), ct -> ct));
	
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
	
	public void setOuputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	public void renderHTML(String html) {
		contentType = ContentType.HTML;
		renderText(html);
	}
	
	public void renderText(String text) {
		setContentLength(text.length());
		PrintStream pstream = writeHeaders();
		pstream.println(text);
		pstream.flush();
		pstream.close();		
	}
	
	public PrintStream writeHeaders() {
		PrintStream pstream = new PrintStream(outputStream);
		pstream.println(code.getResponseHeader());
		pstream.println(getContentTypeHeader().value());
		pstream.println(contentLength.value());
		pstream.println(closeConnection);
		pstream.println();
		pstream.flush();
		return pstream;
	}
	
	private Header getContentTypeHeader() {
		return new ContentTypeHeader(contentType, charset).getHeader();
	}
	
	public void setResponseStatusCode(ResponseStatusCode code) {
		this.code = code;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	
	public static ContentType getContentTypeByValue(String contentType) {
		return contentTypeByValue.get(contentType);
	}
	
	public void setContentLength(long bytes) {
		contentLength.setValue(String.valueOf(bytes));
	}
}
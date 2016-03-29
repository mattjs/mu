package com.mu.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

import java.net.URLConnection;

import com.mu.http.HttpRequest;
import com.mu.http.HttpResponse;
import com.mu.http.ResponseStatusCode;
import com.mu.http.HttpResponse.ContentType;

public class StaticServer {
	public static String PUBLIC_FOLDER = "/public/";
	private static String UP_DIR = "..";
	
	public static void serve(HttpRequest request, HttpResponse response) {
		System.out.println("Static serving : " + request.getRoute());
		if (request.getRoute().contains(UP_DIR)) {
			response.setResponseStatusCode(ResponseStatusCode.BadRequest);
			response.renderHTML("Bad Request");
			return;
		}
		File f = new File(request.getRoute().substring(1));
		if (f.exists()) {
			try {
				response.setResponseStatusCode(ResponseStatusCode.OK);
				response.setContentLength(f.length());
				response.setContentType(guessContentType(f));
				FileInputStream fis = new FileInputStream(f);
				OutputStream output = response.getOutputStream();
				response.writeHeaders();
				copyStream(fis, output);
				fis.close();
				output.close();
			} catch (FileNotFoundException e) {
				notFound(response);
			} catch (IOException e) {
				response.setResponseStatusCode(ResponseStatusCode.InternalServerError);
				response.renderHTML("Internal Server Error");
			}
		} else {
			notFound(response);
		}
	}
	
	private static ContentType guessContentType(File f) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
		String guess = URLConnection.guessContentTypeFromStream(bis);
		ContentType contentType = HttpResponse.getContentTypeByValue(guess);
		if (contentType == null) {
			contentType = guessTextFileByExtension(f.getName());
		}
		return contentType;
	}
	
	private enum Extension {
		CSS(".css", ContentType.CSS),
		PDF(".pdf", ContentType.PDF);
		
		private String extension;
		private ContentType contentType;
		
		private Extension(String extension, ContentType contentType) {
			this.extension = extension;
			this.contentType = contentType;
		}
		
		public String getExtension() {
			return extension;
		}
		
		public ContentType getContentType() {
			return contentType;
		}
	}
	
	public static Map<String, ContentType> contentTypeByExtension = 
		Arrays.asList(Extension.values())
			.stream()
			.collect(Collectors.toMap(e -> e.getExtension(), e -> e.getContentType()));
	
	private static ContentType guessTextFileByExtension(String fileName) {
		ContentType contentType = null;
		int lastIndex = fileName.lastIndexOf(".");
		if (lastIndex != -1) {
			String extension = fileName.substring(lastIndex);
			if (contentTypeByExtension.containsKey(extension)) {
				contentType = contentTypeByExtension.get(extension);
			}
		}
		return contentType != null ? contentType : ContentType.TEXT;
	}
	
	private static void notFound(HttpResponse response) {
		response.setResponseStatusCode(ResponseStatusCode.NotFound);
		response.renderHTML("File not found");
	}
	
	private static final int EOF = -1;
	private static int copyStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024 * 4];
		int length = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			length += n;
		}
		return length;
	}
}
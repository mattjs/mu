package com.mu.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {
	public enum RequestType {
		GET,
		POST,
		PUT,
		DELETE;
	}
	
	private RequestType requestType;
	private String route;
	private List<Header> headers = new ArrayList<>(); 
	
	public HttpRequest(InputStream stream) throws IOException {
		InputStreamReader raw = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(raw);
		String inputLine = reader.readLine();
		while (inputLine != null && inputLine.length() > 0) {
			if (requestType == null) {
				parseRouteAndType(inputLine);
			} else {
				headers.add(Header.parse(inputLine));
			}
			inputLine = reader.readLine();
		}
	}
	
	private void parseRouteAndType(String value) {
		String[] values = value.split(" ");
		requestType = RequestType.valueOf(values[0]);
		route = values[1];
	}
	
	public RequestType getRequestType() {
		return requestType;
	}
	
	public String getRoute() {
		return route;
	}
	
	public List<Header> getHeaders() {
		return headers;
	}
}
package com.mu;

import com.mu.http.HttpRequest;

public class Controller {
	private HttpRequest request;
	
	public HttpRequest getRequest() {
		return request;
	}
	
	public void setRequest(HttpRequest request) {
		this.request = request;
	}
}
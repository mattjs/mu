package com.mu.http;

public enum ResponseStatusCode {
	OK(200),
	BadRequest(400),
	NotFound(404),
	InternalServerError(500);
	
	private int code;
		
	private ResponseStatusCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String getResponseHeader() {
		return "HTTP/1.1 " + this.name();
	}
	
	public byte[] getResponseHeaderBytes() {
		return getResponseHeader().getBytes();
	}
}
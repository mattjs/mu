package com.mu.http;

public enum ResponseStatusCode {
    OK(200, "OK"),
    BadRequest(400, "Bad Request"),
    NotFound(404, "Not Found"),
    InternalServerError(500, "Internal Server Error");
    
    private int code;
    private String phrase;
        
    private ResponseStatusCode(int code, String phrase) {
        this.code = code;
        this.phrase = phrase;
    }

    public String getResponseHeader() {
        return "HTTP/1.1 " + code + " " + phrase;
    }
    
    public byte[] getResponseHeaderBytes() {
        return getResponseHeader().getBytes();
    }
}
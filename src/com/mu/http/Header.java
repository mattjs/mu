package com.mu.http;

public class Header {
    private final String key;
    private String value;
    
    public Header(String key) {
        this.key = key;
    }
    
    public Header(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static Header parse(String value) {
        int index = value.indexOf(":");
        return new Header(value.substring(0, index), value.substring(index + 1));
    }
    
    public String value() {
        return key + ": " + value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return key + "=" + value;
    }
}
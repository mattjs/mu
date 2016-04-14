package com.mu.http;

public class Header {
    private final String key;
    private final String value;
    
    public Header(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static Header parse(String value) {
        int index = value.indexOf(":");
        return new Header(
            value.substring(0, index).trim(),
            value.substring(index + 1).trim());
    }
    
    public String value() {
        return key + ": " + value;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return key + "=" + value;
    }
}
package com.mu.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Charset {
    UTF8("UTF-8");
    
    private static final Map<String, Charset> BY_VALUE =
        Arrays.asList(Charset.values())
            .stream()
            .collect(Collectors.toMap(Charset::getValue, ct -> ct));
    
    private String value;
    
    private Charset(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static Charset getByValue(String value) {
        return BY_VALUE.get(value);
    }
}
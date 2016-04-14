package com.mu.http;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.mu.util.Charset;

/**
 * HttpRequest Content Type
 */
public class FormContentType {
    public enum Value {
        MULTIPART_FORM_DATA("multipart/form-data"),  
        FORM_URL_ENCODED("application/x-www-form-urlencoded");
        
        private String headerType;
        
        private Value(String headerType) {
            this.headerType = headerType;
        }
        
        public String getHeaderType() {
            return headerType;
        }
    }
    
    private static final Map<String, Value> CONTENT_TYPE_VALUE_BY_HEADER_TYPE =
        Arrays.asList(Value.values())
            .stream()
            .collect(Collectors.toMap(Value::getHeaderType, ct -> ct));
    
    private final Value value;
    private final Charset charset;
    
    public static FormContentType from(Header header) {
        String[] parts = header.getValue().split(";");
        String headerType = parts[0].trim();
        Value value = CONTENT_TYPE_VALUE_BY_HEADER_TYPE.get(headerType);
        Charset charset = null;
        if (parts.length > 1) {
            parts = parts[1].split("=");
            if (parts.length > 1) {
                charset = Charset.getByValue(parts[1].trim());
            }
        }
        return new FormContentType(value, charset);
    }
    
    private FormContentType(Value value, Charset charset) {
        this.value = value;
        this.charset = charset != null ? charset : Charset.UTF8;
    }
    
    public Value getValue() {
        return value;
    }
    
    public Charset getCharset() {
        return charset;
    }
}
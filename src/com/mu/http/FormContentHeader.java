package com.mu.http;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.mu.util.Charset;

/**
 * HttpRequest Content Type
 */
public class FormContentHeader {
    public enum FormContentType {
        MULTIPART_FORM_DATA("multipart/form-data"),  
        FORM_URL_ENCODED("application/x-www-form-urlencoded");
        
        private String headerType;
        
        private FormContentType(String headerType) {
            this.headerType = headerType;
        }
        
        public String getHeaderType() {
            return headerType;
        }
    }
    
    private static final Map<String, FormContentType> FORM_CONTENT_TYPE_BY_HEADER_TYPE =
        Arrays.asList(FormContentType.values())
            .stream()
            .collect(Collectors.toMap(FormContentType::getHeaderType, ct -> ct));
    
    private final FormContentType value;
    private final Charset charset;
    
    public static FormContentHeader from(Header header) {
        String[] parts = header.getValue().split(";");
        String headerType = parts[0].trim();
        FormContentType value = FORM_CONTENT_TYPE_BY_HEADER_TYPE.get(headerType);
        Charset charset = null;
        if (parts.length > 1) {
            parts = parts[1].split("=");
            if (parts.length > 1) {
                charset = Charset.getByValue(parts[1].trim());
            }
        }
        return new FormContentHeader(value, charset);
    }
    
    private FormContentHeader(FormContentType value, Charset charset) {
        this.value = value;
        this.charset = charset != null ? charset : Charset.UTF8;
    }
    
    public FormContentType getValue() {
        return value;
    }
    
    public Charset getCharset() {
        return charset;
    }
}
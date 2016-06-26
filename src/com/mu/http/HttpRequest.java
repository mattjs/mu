package com.mu.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mu.util.Charset;

public class HttpRequest {
    public enum RequestType {
        GET,
        POST,
        PUT,
        DELETE;
    }
    
    public static class Url {
        private String pathName;
        private String queryString;
        private String route;
        private Map<String, String> getParams = new HashMap<>();
        
        public Url(String route) {
            this.route = route;
            String[] parts = route.split("\\?");
            this.pathName = parts[0];
            if (parts.length > 1) {
                this.queryString = parts[1];
                this.getParams = parseQueryString(this.queryString, Charset.UTF8);
            }
        }
        
        public String getRoute() {
            return route;
        }
        
        public String getPathName() {
            return pathName;
        }
        
        public String getQueryString() {
            return queryString;
        }
        
        public int length() {
            return route.length();
        }
        
        @Override
        public String toString() {
            return route;
        }
    }
    
    private RequestType requestType;
    private Url url;
    private List<Header> headers = new ArrayList<>();
    private Long contentLength;
    private FormContentHeader formContentHeader;
    private Map<String, String> postParams = new HashMap<>();
    
    public HttpRequest(InputStream stream) throws IOException {
        InputStreamReader raw = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(raw);
        String inputLine = reader.readLine();
        while (inputLine != null && inputLine.length() > 0) {
            if (requestType == null) {
                parseRouteAndType(inputLine);
            } else {
                newHeader(inputLine);
            }
            inputLine = reader.readLine();
        }
        maybeReadAdditionalData(reader);
    }
    
    private void newHeader(String inputLine) {
        Header header = Header.parse(inputLine);
        if (header.getKey().equals("Content-Length")) {
            contentLength = Long.valueOf(header.getValue());
        } else if (header.getKey().equals("Content-Type")) {
            formContentHeader = FormContentHeader.from(header);
        }
    }
    
    private void maybeReadAdditionalData(BufferedReader reader) throws IOException {
        if (contentLength != null) {
            StringBuilder body = new StringBuilder();
            int c = 0;
            for (int i = 0; i < contentLength; i++) {
                c = reader.read();
                body.append((char) c);
            }
            parseBody(body.toString());
        }
    }
    
    // TODO: support multipart form data
    private void parseBody(String body) {
        if (formContentHeader.getValue() == FormContentHeader.FormContentType.FORM_URL_ENCODED) {
            postParams = parseQueryString(body, formContentHeader.getCharset());
        }
    }
    
    // TODO: Duplicate values are overwritten currently
    private static Map<String, String> parseQueryString(String str, Charset charset) {
        Map<String, String> result = new HashMap<>();
        try {
            String[] params = str.split("&");
            for (int i = 0; i < params.length; i++) {
                String[] parts = params[i].split("=");
                if (parts.length > 0) {
                    String value = "";
                    if (parts.length > 1) {
                        value = URLDecoder.decode(parts[1], charset.getValue());
                    }
                    result.put(parts[0], value);
                }
            }
        } catch (UnsupportedEncodingException e) {
            // TODO handle this
        }
        return result;
    }
    
    private void parseRouteAndType(String value) {
        String[] values = value.split(" ");
        requestType = RequestType.valueOf(values[0]);
        url = new Url(values[1]);
    }
    
    public RequestType getRequestType() {
        return requestType;
    }
    
    public Url getUrl() {
        return url;
    }
    
    public List<Header> getHeaders() {
        return headers;
    }
    
    public Map<String, String> getPostParams() {
        return postParams;
    }
    
    public Map<String, String> getGetParams() {
        return url.getParams;
    }
}
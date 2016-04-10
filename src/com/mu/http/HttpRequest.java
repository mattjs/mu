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
    
    public static class Url {
        private String pathName;
        private String queryString;
        private String route;
        
        public Url(String route) {
            this.route = route;
            String[] parts = route.split("\\?");
            this.pathName = parts[0];
            if (parts.length > 1) {
                this.queryString = parts[1];
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
}
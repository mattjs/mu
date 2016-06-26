package com.mu;

import com.mu.http.HttpRequest;
import com.mu.http.HttpResponse;
import com.mu.http.ResponseStatusCode;
import com.mu.http.HttpResponse.ContentType;

public abstract class Controller {
    protected HttpRequest request;
    protected HttpResponse response;
    
    public HttpRequest getRequest() {
        return request;
    }
    
    public void setRequest(HttpRequest request) {
        this.request = request;
    }
    
    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }
    
    public void renderHTML(String html) {
        this.response.setResponseStatusCode(ResponseStatusCode.OK);
        this.response.renderHTML(html);
    }
    
    public void renderJSON(String json) {
        this.response.setResponseStatusCode(ResponseStatusCode.OK);
        this.response.setContentType(ContentType.JSON);
        this.response.renderText(json);
    }
}
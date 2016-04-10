package com.mu;

import com.mu.http.HttpRequest;
import com.mu.http.HttpResponse;
import com.mu.http.ResponseStatusCode;
import com.mu.route.Route;
import com.mu.route.Router;
import com.mu.server.ServerRunner;
import com.mu.server.StaticServer;

public class MuApplication {
    private Router router;
    private int MAX_URL_LENGTH = 8000;
    
    public MuApplication() {
        router = new Router();
        ServerRunner.start(this);
    }
    
    public void handleRequest(HttpRequest request, HttpResponse response) {
        if (request.getUrl().length() > MAX_URL_LENGTH) {
            response.setResponseStatusCode(ResponseStatusCode.BadRequest);
            response.renderHTML("Bad Request");
            return;
        }
        
        if (request.getUrl().getPathName().startsWith(StaticServer.PUBLIC_FOLDER)) {
            StaticServer.serve(request, response);
        } else {
            Route route = router.route(request);
            if (route != null && route.getRequestType() == request.getRequestType()) {
                route.call(request, response);
            } else {
                response.setResponseStatusCode(ResponseStatusCode.NotFound);
                response.renderHTML("Route not found");
            }
        }
    }
    
    public static void main(String[] args) {
        new MuApplication();
    }
}
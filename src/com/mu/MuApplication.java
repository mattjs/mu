package com.mu;

import com.mu.http.HttpRequest;
import com.mu.http.HttpResponse;
import com.mu.route.Route;
import com.mu.route.Router;
import com.mu.server.ServerRunner;

public class MuApplication {
	private Router router;
	
	public MuApplication() {
		router = new Router();
		ServerRunner.start(this);
	}
	
	public void handleRequest(HttpRequest request, HttpResponse response) {
		Route route = router.route(request.getRoute());
		if (route != null &&
		    route.getRequestType() == request.getRequestType()) {
			response.setBody(route.call(request));
		} else {
			response.setBody("Route not found");
		}
	}
	
	public static void main(String[] args) {
		new MuApplication();
	}
}
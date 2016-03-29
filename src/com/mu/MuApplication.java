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
		if (request.getRoute().length() > MAX_URL_LENGTH) {
			response.setResponseStatusCode(ResponseStatusCode.BadRequest);
			response.renderHTML("Bad Request");
			return;
		}
		
		if (request.getRoute().startsWith(StaticServer.PUBLIC_FOLDER)) {
			StaticServer.serve(request, response);
		} else {
			Route route = router.route(request.getRoute());
			String html;
			if (route != null && route.getRequestType() == request.getRequestType()) {
				html = route.call(request);
				response.setResponseStatusCode(ResponseStatusCode.OK);
			} else {
				html = "Route not found";
				response.setResponseStatusCode(ResponseStatusCode.NotFound);
			}
			response.renderHTML(html);
		}
	}
	
	public static void main(String[] args) {
		new MuApplication();
	}
}
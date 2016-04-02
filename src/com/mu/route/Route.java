package com.mu.route;

import java.lang.reflect.Method;

import com.mu.Controller;
import com.mu.http.HttpRequest;
import com.mu.http.HttpResponse;

/**
 * TODO(mattjs) Support multiple request types at same route
 */
public class Route {
	private HttpRequest.RequestType requestType;
	private String route;
	private String controllerName;
	private String methodName;
	private Class<? extends Controller> controllerClass;
	
	public static Route from(String str) {
		Route route = new Route();
		str = str.replaceAll("\\s+", " ");
		String[] parts = str.split(" ");
		route.requestType = HttpRequest.RequestType.valueOf(parts[0]);
		route.route = parts[1];
		String[] controllerAndMethod = parts[2].split("\\.");
		route.controllerName = controllerAndMethod[0];
		route.methodName = controllerAndMethod[1];
		return route;
	}
	
	public void setControllerClass(Class<? extends Controller> clazz) {
		this.controllerClass = clazz;
	}
	
	public String getRoute() {
		return route;
	}
	
	public String getControllerName() {
		return controllerName;
	}
	
	public HttpRequest.RequestType getRequestType() {
		return requestType;
	}
	
	public String call(HttpRequest request, HttpResponse response) {
		String result = null;
		try {
			Controller controller = (Controller)controllerClass.newInstance();
			controller.setRequest(request);
			controller.setResponse(response);
			Method method = controller.getClass().getMethod(this.methodName);
			result = (String) method.invoke(controller);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
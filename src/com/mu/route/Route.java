package com.mu.route;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.mu.Controller;
import com.mu.http.HttpRequest;
import com.mu.http.HttpRequest.Url;
import com.mu.http.HttpResponse;

public class Route {
	private HttpRequest.RequestType requestType;
	private String controllerName;
	private String methodName;
	private Class<? extends Controller> controllerClass;
	private Url url;
	private List<Fragment> fragments;
	
	// Every fragment list will start with an empty string
	public static class Fragment {
		public String value;
		
		public Fragment(String value) {
			this.value = value;
		}
		
		public static List<Fragment> from(String pathName) {
			String[] values;
			if (pathName.equals("/")) {
				values = new String[]{
					""
				};
			} else {
				values = pathName.split("\\/");
				// Remove trailing slash
				if (values.length > 1
					&& values[values.length - 1].equals("/")) {
					values = Arrays.copyOfRange(values, 1, values.length);
				}
			}
			return Arrays.asList(values)
				.stream()
				.map(value -> new Fragment(value))
				.collect(Collectors.toList());
		}
		
		public String getValue() {
			return value;
		}
		
		public boolean matches(String value) {
			return this.value.equals(value);
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	public static Route from(String str) {
		Route route = new Route();
		str = str.replaceAll("\\s+", " ");
		String[] parts = str.split(" ");
		route.requestType = HttpRequest.RequestType.valueOf(parts[0]);
		route.url = new Url(parts[1]);
		route.fragments = route.generateFragments(); 
		String[] controllerAndMethod = parts[2].split("\\.");
		route.controllerName = controllerAndMethod[0];
		route.methodName = controllerAndMethod[1];
		return route;
	}
	
	private List<Fragment> generateFragments() {
		return Fragment.from(url.getPathName());
	}
	
	public void setControllerClass(Class<? extends Controller> clazz) {
		this.controllerClass = clazz;
	}
	
	public Url getUrl() {
		return url;
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
			e.printStackTrace();
		}
		return result;
	}
	
	public List<Fragment> getFragments() {
		return fragments;
	}
	
	@Override
	public String toString() {
		return requestType + " " + url;
	}
}
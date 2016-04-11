package com.mu.route;

import java.lang.reflect.Method;
import java.util.List;

import com.mu.Controller;
import com.mu.http.HttpRequest;
import com.mu.http.HttpResponse;

public class Route {
    private HttpRequest.RequestType requestType;
    private String controllerName;
    private String methodName;
    private Class<? extends Controller> controllerClass;
    private String path;
    private List<String> pathParts;
  
    public static Route from(String str) {
        Route route = new Route();
        str = str.replaceAll("\\s+", " ");
        String[] parts = str.split(" ");
        route.requestType = HttpRequest.RequestType.valueOf(parts[0]);
        route.path = parts[1];
        route.pathParts = TreeFactory.getPathNameParts(route.path); 
        String[] controllerAndMethod = parts[2].split("\\.");
        route.controllerName = controllerAndMethod[0];
        route.methodName = controllerAndMethod[1];
        return route;
    }
    
    public void setControllerClass(Class<? extends Controller> clazz) {
        this.controllerClass = clazz;
    }
    
    public String getPath() {
        return path;
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
    
    public List<String> getPathParts() {
        return pathParts;
    }
    
    @Override
    public String toString() {
        return requestType + " " + path;
    }
}
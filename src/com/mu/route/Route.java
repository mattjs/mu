package com.mu.route;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.mu.Controller;
import com.mu.http.HttpRequest;
import com.mu.http.HttpResponse;

public class Route {
    private HttpRequest.RequestType requestType;
    private String controllerName;
    private String methodName;
    private Class<? extends Controller> controllerClass;
    private String path;
    // Every fragment list will start with an empty string
    private List<Fragment> fragments;
    private final static String URL_ALLOWED_CHARS = 
        "[A-Za-z0-9]"; //\\-\\.\\_\\~\\:\\/\\?\\#\\[\\]\\@\\!\\$\\&\\'\\(\\)\\*\\+\s\\,\\;\\=]";
    
    public static class RouteException extends Exception {
        public RouteException(String msg) {
            super(msg);
        }
    }
    
    public static class Fragment {
        public String value;
        public Param param;
        
        public static class Param {
            public static final String OPEN_CHAR = "{";
            public static final String CLOSE_CHAR = "}";
            public Pattern pattern;
            
            // TODO : get param name
            public Param(Pattern pattern) {
                this.pattern = pattern;
            }
            
            public static Param from(String value) throws RouteException {
                Param param = null;
                if (value.startsWith(OPEN_CHAR)) {
                    if (value.endsWith(CLOSE_CHAR)) {
                        String regex = value.substring(1, value.length() - 1);
                        if (!regex.startsWith("(")) {
                            regex = "(?<" + regex + ">" + URL_ALLOWED_CHARS + "+)";
                        } else if (!regex.endsWith(")")) {
                            throw new RouteException("Regex should named groups only (?<{paramName}>{regex})");
                        }
                        try {
                            Pattern pattern = Pattern.compile(regex);
                            param = new Param(pattern);
                        } catch (PatternSyntaxException e) {
                            throw new RouteException("Regex invalid");
                        }
                    } else {
                        throw new RouteException("Route invalid");
                    }
                }
                return param;
            }
        }
        
        public Fragment(String value) {
            this.value = value;
        }
        
        public void validate() throws RouteException {
            this.param = Param.from(value);
        }
        
        public static List<Fragment> from(String pathName) {
            return getPathNameParts(pathName)
                .stream()
                .map(value -> new Fragment(value))
                .collect(Collectors.toList());
        }
        
        public static List<String> getPathNameParts(String pathName) {
            String[] values;
            if (pathName.equals("/")) {
                values = new String[]{
                    ""
                };
            } else {
                values = pathName.split("\\/");
                // Remove trailing slash (should redirect instead)
                if (values.length > 1
                    && values[values.length - 1].equals("/")) {
                    values = Arrays.copyOfRange(values, 1, values.length);
                }
            }
            return Arrays.asList(values);
        }
        
        public String getValue() {
            return value;
        }
        
        public boolean matches(String value) {
            if (this.param != null) {
                Matcher m = this.param.pattern.matcher(value);
                return m.matches();
            }
            return this.value.equals(value);
        }
        
        public boolean isParam() {
            return this.param != null;
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
        route.path = parts[1];
        route.fragments = route.generateFragments(); 
        String[] controllerAndMethod = parts[2].split("\\.");
        route.controllerName = controllerAndMethod[0];
        route.methodName = controllerAndMethod[1];
        return route;
    }
    
    private List<Fragment> generateFragments() {
        return Fragment.from(path);
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
    
    public List<Fragment> getFragments() {
        return fragments;
    }
    
    @Override
    public String toString() {
        return requestType + " " + path;
    }
}
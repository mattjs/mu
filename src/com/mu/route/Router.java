package com.mu.route;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.mu.Controller;
import com.mu.http.HttpRequest;
import com.mu.route.Tree.RouteMatch;
import com.mu.route.TreeFactory.RouteException;

/**
 * TODO(mattjs) Do some validation when routes are parsed.
 * ex. Check for duplicate routes
 */
public class Router {
    private String ROUTE_PATH = "conf/routes";
    
    //private Match match = new Match();
    private List<Route> routes = new ArrayList<>();
    private Tree tree;
    private Map<String, Class<? extends Controller>> controllers = new HashMap<>();
    
    public Router() {
        System.out.println("Loading routes");
        try {
            loadRoutes();
            loadControllers();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadControllers() throws Exception {
        ClassLoader classLoader = Router.class.getClassLoader();
        for (Route route : routes) {
            if (!controllers.containsKey(route.getControllerName())) {
                Class<? extends Controller> clazz =
                    (Class<? extends Controller>) classLoader.loadClass("controllers." + route.getControllerName());
                controllers.put(route.getControllerName(), clazz);
            }
            route.setControllerClass(controllers.get(route.getControllerName()));
        }
    }
    
    private void loadRoutes() throws Exception {
        File f = new File(ROUTE_PATH);
        if (f.exists()) {
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader raw = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(raw);
            String inputLine = reader.readLine();
            while (inputLine != null && inputLine.length() > 0) {
                if (!inputLine.startsWith("#")) {
                    newRoute(inputLine);
                }
                inputLine = reader.readLine();
            }
            reader.close();
            buildTree();
        } else {
            System.out.println("Routes file not found");
        }
    }
    
    private void newRoute(String line) {
        Route route = Route.from(line);
        routes.add(route);
    }
    
    private void buildTree() {
    	try {
			this.tree = TreeFactory.build(this.routes);
		} catch (RouteException e) {
			System.out.println("Error parsing routes");
			System.out.println(e);
		}
    }
    
    // TODO: cache route matches
    public Route route(HttpRequest request) {
    	String path = request.getUrl().getPathName();
    	List<String> pathParts = TreeFactory.getPathNameParts(path);
        RouteMatch routeMatch = tree.matchPath(pathParts);
        if (routeMatch.matched) {
            for (Route route : routeMatch.routes) {
                if (route.getRequestType() == request.getRequestType()) {
                    return route;
                }
            }
        }
        return null;
    }
}
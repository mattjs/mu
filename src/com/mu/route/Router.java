package com.mu.route;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.mu.Controller;

public class Router {
	private String ROUTE_PATH = "conf/routes";
	
	private Map<String, Route> routes = new HashMap<>();
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
		for (Route route : routes.values()) {
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
		} else {
			System.out.println("Routes file not found");
		}
	}
	
	private void newRoute(String line) {
		Route route = Route.from(line);
		routes.put(route.getRoute(), route);
	}
	
	public Route route(String route) {
		return routes.get(route);
	}
}
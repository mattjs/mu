package com.mu.route;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class Match {
	private Map<String, Partial> partialsByFirstPart = new HashMap<>();
	
	private static class Partial {
		public String part;
		public List<Route> routes = new ArrayList<>();
		public Map<String, Partial> childPartialsByFirstPart = new HashMap<>();
		
		public Partial(String part) {
			this.part = part;
		}
		
		public static Partial from(Route route, String[] parts, String part) {
			Partial partial = new Partial(part);
			partial.addRoute(route, parts);
			return partial;
		}
		
		public void addRoute(Route route, String[] parts) {
			maybeAddRoute(route);
			if (parts.length > 1) {
				Partial partial;
				if (childPartialsByFirstPart.containsKey(parts[1])) {
					partial = childPartialsByFirstPart.get(parts[1]);
				} else {
					partial = new Partial(newPart(parts[1]));
					childPartialsByFirstPart.put(parts[1], partial);
				}
				partial.addRoute(route, getChildren(parts));
			}
		}
		
		/**
		 * Index route (/) is represented by / instead of
		 * empty string to match value in HTTP header
		 */
		private String newPart(String newPart) {
			if (part.equals("/")) {
				return part + newPart;
			} else {
				return part + "/" + newPart;
			}
		}
		
		/**
		 * The route only gets added to the list when there is a
		 * full match.
		 */
		private void maybeAddRoute(Route route) {
			if (route.getUrl().getRoute().equals(part)) {
				routes.add(route);
			}
		}
		
		private String[] getChildren(String[] parts) {
			return Arrays.copyOfRange(parts, 1, parts.length);
		}
		
		public List<Route> findRoutes(String pathName, String[] parts) {
			if (parts.length > 1) {
				Partial partial = childPartialsByFirstPart.get(parts[1]);
				if (partial != null) {
					return partial.findRoutes(pathName, getChildren(parts));
				}
			} else if (pathName.equals(part)) {
				return routes;
			}
			return null;
		}
		
		@Override
		public String toString() {
			return "Part : '" + part + "'\nRoutes : " + routes;
		}
	}
	
	public void addRoute(Route route) {
		String[] parts = getParts(route.getUrl().getPathName());
		if (partialsByFirstPart.containsKey(parts[0])) {
			Partial partial = partialsByFirstPart.get(parts[0]);
			partial.addRoute(route, parts);
		} else {
			Partial partial = Partial.from(route, parts, "/");
			partialsByFirstPart.put(parts[0], partial);
		}
	}
	
	/**
	 *  Normalize route so that ending with / is equivalent to
	 *  one that does not. Index route (/) is transformed to
	 *  empty string
	 */
	private String[] getParts(String pathName) {
		String[] parts = pathName.split("\\/");
		if (parts.length > 1 && parts[parts.length - 1].isEmpty()) {
			parts = Arrays.copyOfRange(parts, 0, parts.length - 2);
		} else if (parts.length == 0) {
			parts = new String[1];
			parts[0] = "";
		}
		return parts;
	}
	
	public List<Route> findRoutes(String pathName) {
		String[] parts = getParts(pathName);
		Partial partial = partialsByFirstPart.get(parts[0]);
		if (partial != null) {
			return partial.findRoutes(pathName, parts);
		}
		return null;
	}
}
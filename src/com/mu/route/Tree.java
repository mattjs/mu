package com.mu.route;

import java.util.List;
import java.util.Map;

import com.mu.route.Leaf.ActiveMatch;

public class Tree {
	private Leaf root;
	
	public Tree() {
	  this.root = new Leaf(
		0,
		PathParam.from(""));
	}
	
	public static class RouteMatch {
		public final boolean matched;
		public final Map<String, String> params;
		public final List<Route> routes;
		
		public static RouteMatch noMatch() {
			return new RouteMatch(false);
		}
		
		private RouteMatch(boolean matched) {
			this.matched = matched;
			this.routes = null;
			this.params = null;
		}
		
		public RouteMatch(
			Map<String, String> params,
			List<Route> routes) {
			this.matched = true;
			this.params = params;
			this.routes = routes;
		}
	}
	
	public void add(Route route, List<PathParam> params) {
		this.root.add(route, params);
	}
	
	public RouteMatch matchPath(List<String> pathNameParts) {
		ActiveMatch match = new ActiveMatch(pathNameParts);
		this.root.matchRoute(match);
		if (!match.routes.isEmpty()) {
			return new RouteMatch(match.params, match.routes);
		} else {
			return RouteMatch.noMatch();
		}
	}
}
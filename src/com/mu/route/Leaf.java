package com.mu.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Leaf {
    private final int depth;
    private final PathParam pathParam;
    
    private Children children = new Children();
    private List<Route> routes = new ArrayList<>();
    
    // Separated by param type
    private class Children {
        public Map<String, Leaf> simple = new HashMap<>();
        public Map<String, Leaf> patterned = new HashMap<>();
    }
    
    public Leaf(
        int depth,
        PathParam param) {
        this.depth = depth;
        this.pathParam = param;
    }
    
    public PathParam getPathParam() {
        return pathParam;
    }
    
    public List<Route> getRoutes() {
        return routes;
    }
    
    public void addRoute(Route route) {
        this.routes.add(route);
    }
    
    public void add(Route route, List<PathParam> pathParams) {
        if (pathParams.size() > depth + 1) {
            PathParam pathParam = pathParams.get(depth + 1);
            newLeaf(
                pathParam,
                pathParam.isPatterened() ? children.patterned : children.simple,
                route,
                pathParams);
        } else {
            routes.add(route);
        }
    }
    
    private void newLeaf(
        PathParam param,
        Map<String, Leaf> children,
        Route route,
        List<PathParam> params) {
        if (children.containsKey(param.getValue())) {
            children.get(param.getValue()).add(route, params);
        } else {
            Leaf leaf = new Leaf(depth + 1, param);
            children.put(param.getValue(), leaf);
            leaf.add(route, params);
        }
    }
    
    static class ActiveMatch {
        public List<String> pathParts;
        public Map<String, String> params = new HashMap<>();
        public List<Route> routes = new ArrayList<>();
        
        public ActiveMatch(List<String> pathParts) {
            this.pathParts = pathParts;
        }
    }
    
    private void maybeMatchFurther(ActiveMatch activeMatch) {
        if (activeMatch.pathParts.size() > depth + 1) {
            String part = activeMatch.pathParts.get(depth + 1);
            if (this.children.simple.containsKey(part)) {
                this.children.simple.get(part).matchRoute(activeMatch);
            } else {
                for (Leaf leaf : this.children.patterned.values()) {
                    leaf.matchRoute(activeMatch);
                }
            }
        } else {
            activeMatch.routes = routes;
        }
    }
    
    public void matchRoute(ActiveMatch activeMatch) {
        List<String> pathParts = activeMatch.pathParts;
        String part = pathParts.get(depth);
        if (this.pathParam.getType() == PathParam.Type.PATTERNED) {
            Map<String, String> params = this.pathParam.matchesPatterned(part);
            if (params != null && !params.isEmpty()) {
                activeMatch.params.putAll(params);
                maybeMatchFurther(activeMatch);
            }
        } else {
            if (this.pathParam.matchesSimple(part)) {
                maybeMatchFurther(activeMatch);
            }
        }
    }
}
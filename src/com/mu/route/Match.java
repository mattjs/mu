package com.mu.route;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.mu.route.Route.Fragment;

import java.util.HashMap;

public class Match {
    PartialMatch partial = new PartialMatch(new Fragment(""));
    
    private static class PartialMatch {
        public List<Fragment> fragments;
        public List<Route> matches = new ArrayList<>();
        // Children
        public Map<String, PartialMatch> determinte = new HashMap<>();
        public Map<String, PartialMatch> parameters = new HashMap<>();
        
        public PartialMatch(Fragment fragment) {
             fragments = new ArrayList<>();
             fragments.add(fragment);
        }
        
        private Fragment getLastFragment() {
            return this.fragments.get(this.fragments.size() - 1);
        }
        
        public PartialMatch(List<Fragment> fragments) {
            this.fragments = fragments;
        }
        
        public void addRoute(Route route) {
            List<Fragment> remaining = getRemaining(route);
            if (!remaining.isEmpty()) {
                PartialMatch partial;
                Fragment fragment = remaining.get(0);
                if (fragment.isParam()) {
                    if (parameters.containsKey(fragment.getValue())) {
                        partial = parameters.get(fragment.getValue());
                    } else {
                        partial = new PartialMatch(getChildFragments(route));
                        parameters.put(fragment.getValue(), partial);
                    }
                } else {
                    if (determinte.containsKey(fragment.getValue())) {
                        partial = determinte.get(fragment.getValue());
                    } else {
                        partial = new PartialMatch(getChildFragments(route));
                        determinte.put(fragment.getValue(), partial);
                    }
                }
                partial.addRoute(route);
            } else {
                this.matches.add(route);
            }
        }
        
        private List<Fragment> getRemaining(Route route) {
            return route.getFragments()
                .subList(this.fragments.size(), route.getFragments().size());
        }
        
        private List<Fragment> getChildFragments(Route route) {
            return route.getFragments().subList(0, this.fragments.size() + 1);
        }
        
        public List<Route> findRoutes(String pathName) {
            return this.findRoutes(Fragment.getPathNameParts(pathName));
        }
        
        private List<Route> findRoutes(List<String> pathNameParts) {
            if (pathNameParts.size() == this.fragments.size()) {
                return this.matches;
            } else {
                int index = this.fragments.size();
                String key = pathNameParts.get(index);
                if (this.determinte.containsKey(key)){
                    return this.determinte.get(key).findRoutes(pathNameParts);
                } else {
                    List<Route> routes = new ArrayList<>();
                    for (PartialMatch partial : this.parameters.values()) {
                        if (partial.getLastFragment().matches(key)) {
                            routes.addAll(partial.findRoutes(pathNameParts));
                        }
                    }
                    return routes;
                }
            }
        }
    }
    
    public void addRoute(Route route) {
        partial.addRoute(route);
    }
    
    public List<Route> findRoutes(String pathName) {
        return partial.findRoutes(pathName);
    }
}
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
		public Map<String, PartialMatch> childrenByFirstFragment = new HashMap<>();
		
		public PartialMatch(Fragment fragment) {
			 fragments = new ArrayList<>();
			 fragments.add(fragment);
		}
		
		public PartialMatch(List<Fragment> fragments) {
			this.fragments = fragments;
		}
		
		public void addRoute(Route route) {
			System.out.println("Add route : " + route.getFragments());
			List<Fragment> remaining = route.getFragments()
				.subList(fragments.size(), route.getFragments().size());
			if (!remaining.isEmpty()) {
				PartialMatch partial;
				String value = remaining.get(0).getValue();
				if (childrenByFirstFragment.containsKey(value)) {
					partial = childrenByFirstFragment.get(value);
				} else {
					partial = new PartialMatch(route.getFragments()
						.subList(0, fragments.size() + 1));
					childrenByFirstFragment.put(value, partial);
				}
				partial.addRoute(route);
			} else {
				matches.add(route);
			}
		}
		
		public List<Route> findRoutes(String pathName) {
			return findRoutes(Fragment.from(pathName));
		}
		
		private List<Route> findRoutes(List<Fragment> generated) {
			if (generated.size() == fragments.size()) {
				if (matches(generated)) {
					return matches;
				}
			} else {
				int index = fragments.size();
				String key = generated.get(index).getValue();
				if (childrenByFirstFragment.containsKey(key)){
					return childrenByFirstFragment.get(key).findRoutes(generated);
				}
			}
			return null;
		}
		
		private boolean matches(List<Fragment> generated) {
			for (int i = 0; i < fragments.size(); i++) {
				if (!fragments.get(i).matches(generated.get(i).getValue())) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Fragments : '" + fragments + "'\nMatching Routes : " + matches);
			childrenByFirstFragment.values().stream().forEach(pm -> {
				builder.append(pm.toString());
			});
			return builder.toString();
		}
	}
	
	public void addRoute(Route route) {
		partial.addRoute(route);
	}
	
	public List<Route> findRoutes(String pathName) {
		return partial.findRoutes(pathName);
	}
}
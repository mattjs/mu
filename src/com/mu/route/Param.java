package com.mu.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Param {
	public enum Type {
		SIMPLE,
		PATTERNED
	}
	
	private final Type type;
	private final String value;
	private final Pattern pattern;
	private final List<String> paramNames;
	
	public static Param from(String simple) {
		return new Param(
			Type.SIMPLE,
			simple,
			null, /* pattern */
			null /* paramNames */);
	}
	
	public static Param from(
		String value,
		Pattern pattern,
		List<String> paramNames) {
		return new Param(
			Type.PATTERNED,
			value,
			pattern, 
			paramNames);
	}
	
	private Param(
		Type type,
		String value,
		Pattern pattern,
		List<String> paramNames) {
		this.type = type;
		this.value = value;
		this.pattern = pattern;
		this.paramNames = paramNames;
	}
	
	public boolean matchesSimple(String value) {
		return this.value.equals(value);
	}
	
	public Map<String, String> matchesPatterned(String value) {
		Matcher m = this.pattern.matcher(value);
        if (m.matches()) {
        	Map<String, String> params = new HashMap<>();
        	for (String name : this.paramNames) {
        		params.put(name, m.group(name));
        	}
        	return params;
        }
        return null;
	}
 
	
	public String getValue() {
		return value;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isPatterened() {
		return Type.PATTERNED.equals(this.type);
	}
}
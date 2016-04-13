package com.mu.route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TreeFactory {
    public static final String PARAM_OPEN_CHAR = "{";
    public static final String PARAM_CLOSE_CHAR = "}";
    // TODO Extend these
    public static final String URL_ALLOWED_CHARS = "[A-Za-z0-9]";
    // \\-\\.\\_\\~\\:\\/\\?\\#\\[\\]\\@\\!\\$\\&\\'\\(\\)\\*\\+\s\\,\\;\\=]";
    public static final Pattern NAMED_GROUP_PATTERN =
        Pattern.compile("\\(\\?\\<(" + URL_ALLOWED_CHARS + "+?)\\>.+?\\)");
    
    public static class RouteException extends Exception {
        public RouteException(String msg) {
            super(msg);
        }
    }
    
    public static Tree build(List<Route> routes) throws RouteException {
        Map<String, PathParam> paramsByValue = new HashMap<>(); 
        Tree tree = new Tree();
        for (Route route : routes) {
            List<PathParam> params = new ArrayList<>();
            for (String part : route.getPathParts()) {
                if (paramsByValue.containsKey(part)) {
                    params.add(paramsByValue.get(part));
                } else {
                    PathParam p = fromPathNamePart(part);
                    params.add(p);
                    paramsByValue.put(p.getValue(), p);
                }
            }
            tree.add(route, params);
        }
        return tree;
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
    
    public static PathParam fromPathNamePart(String value) throws RouteException {
        PathParam param;
        if (value.startsWith(PARAM_OPEN_CHAR)) {
            if (value.endsWith(PARAM_CLOSE_CHAR)) {
                String inner = value.substring(1, value.length() - 1);
                String regex = null;
                List<String> paramNames = null;
                if (!inner.startsWith("(")) {
                    paramNames = new ArrayList<>();
                    paramNames.add(inner);
                    regex = "(?<" + inner + ">" + URL_ALLOWED_CHARS + "+)";
                } else if (inner.endsWith(")")) {
                    paramNames = getNameGroups(inner);
                    regex = inner;
                } else {
                    throw new RouteException("Regex should be named groups only (?<{paramName}>{regex})");
                }
                try {
                    Pattern pattern = Pattern.compile(regex);
                    param = PathParam.from(value, pattern, paramNames);
                } catch (PatternSyntaxException e) {
                    throw new RouteException("Regex invalid");
                }
            } else {
                throw new RouteException("Route invalid");
            }
        } else {
            param = PathParam.from(value);
        }
        return param;
    }
    
    private static List<String> getNameGroups(String name) {
        List<String> groups = new ArrayList<>();
        Matcher matcher = NAMED_GROUP_PATTERN.matcher(name);
        while (matcher.find()) {
            groups.add(matcher.group(1));
        }
        return groups;
    }
}
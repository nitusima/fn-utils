package com.africapoa.fn.ds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;


final class PathEvaluator {
    private static final PathEvaluator INSTANCE = new PathEvaluator();
    private PathEvaluator() {}
    public static PathEvaluator getInstance() {
        return INSTANCE;
    }

    public List<String> evaluatePath(String jsonPath) {
        List<String> paths = new ArrayList<>();
        Matcher parts=PathType.PATH_POSSIBILITIES.getPattern().matcher(jsonPath.replaceAll("^[$.]+",""));
        while (parts.find()) {
            for (int i = 1, len = parts.groupCount(); i <= len; i++) {
                String x = parts.group(i);
                if (x == null || x.isEmpty()) continue;
                paths.add(x);
            }
        }
        return paths;
    }

    public PathType getMatching(String jsonPath){
       PathType type= FnList.from(Arrays.asList(PathType.REGULAR_PATH, PathType.GLOBED_PATH, PathType.PATH_EXPRESSION, PathType.WILDCARD, PathType.ARRAY))
                .filter(it->it.getPattern().matcher(jsonPath).matches())
                .first();
       return type!=null?type:PathType.NONE;
    }
}


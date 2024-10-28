package com.africapoa.fn.ds;

import java.util.regex.Pattern;

public enum PathType {
    INTEGER(Pattern.compile("^\\d+$")),
    REGULAR_PATH(Pattern.compile("(?:\\w+|\"[^\"]+\")(?:\\.(?:\\w+|\"[^\"]+\"))*")),
    ARRAY(Pattern.compile("\\[(?:(\\??\\(.+\\))|(-?\\d*:?-?\\d*(?:,-?\\d*:?-?\\d*)*)|(\\*)|(([`\"']).+?\\5))\\]")),
    GLOBED_PATH(Pattern.compile("(?=.*\\*)(?=.*\\w)[^.\\[\\]()?'\"]*")),
    WILDCARD(Pattern.compile("\\.{2,3}(?:" + "(?:\\w+|\"[^\"]+\")(?:\\.(?:\\w+|\"[^\"]+\"))*" + ")?")),
    PATH_EXPRESSION(Pattern.compile(".*\\[?\\??\\(.*\\)]?")),
    PATH_POSSIBILITIES(Pattern.compile("(" + "(?=.*\\*)(?=.*\\w)[^.\\[\\]()?'\"]*" + ")?(" + "(?:\\w+|\"[^\"]+\")(?:\\.(?:\\w+|\"[^\"]+\"))*" + ")?(" + "\\.{2,3}(?:" + "(?:\\w+|\"[^\"]+\")(?:\\.(?:\\w+|\"[^\"]+\"))*" + ")?" + ")?(\\[[^]]+])?")),
    JSON_VARIABLE(Pattern.compile("@\\.(\\w+)")),
    VALUED_TRUE(Pattern.compile("(?i)yes|ndio|ndiyo|true")),
    VALUED_FALSE(Pattern.compile("(?i)hapana|no|false")),
    NONE(Pattern.compile("null"));

    private final Pattern pattern;
    PathType(Pattern pattern) { this.pattern = pattern; }
    public Pattern getPattern() { return pattern; }
}

package com.africapoa.fn.ds;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyValue<K,V> {
    public K key;
    public V value;
    private static final Pattern PATTERN_KEY_VALUE=Pattern.compile("^\\W*([a-z]\\w+)\\W+(\\w.*)");

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }
    // Pattern pattern = Pattern.compile("^\\W*(?<key>[a-z]\\w+)\\W+(?<value>\\w.*)");//api >=26;

    public static KeyValue<String,String> create(String input){
        Matcher m=PATTERN_KEY_VALUE.matcher(input != null ? input : "");
        return m.find()
                ?new KeyValue<>(m.group(1),m.group(2))
                :new KeyValue<>("","");
    }
}

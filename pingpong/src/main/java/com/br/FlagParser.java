package com.br;

import java.util.HashMap;
import java.util.Map;

public class FlagParser {
    private final Map<String, String> flags = new HashMap<>();

    public FlagParser(String[] args) {
        for(String arg: args) {
            if(arg.startsWith("--")) {
                String[] part = arg.substring(2).split("=",2);

                if(part.length == 2) {
                    flags.put(part[0], part[1]);
                } else {
                    flags.put(part[0], "true");
                }
            }
        }
    }

    public String get(String key) {
        return flags.get(key);
    }

    public boolean has(String key) {
        return flags.containsKey(key);
    }

}

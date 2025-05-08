package com.github.xnam.evaluator;

import com.github.xnam.object.BuiltIn;

import java.util.HashMap;
import java.util.Map;

public class BuiltInMap {
    public Map<String, BuiltIn> store = new HashMap();

    public BuiltInMap() {
        initMap();
    }

    private void initMap() {
        store.put("len", new BuiltIn(args -> {
           if (args.length != 1) return new com.github.xnam.object.Error(String.format("Inappropriate amount of arguments, expected = %d, got = %d", 1, args.length));
           if (args[0] instanceof com.github.xnam.object.String) {
               com.github.xnam.object.String string = (com.github.xnam.object.String) args[0];
               return new com.github.xnam.object.Integer(string.getValue().length());
           } else {
               return new com.github.xnam.object.Error(String.format("Type %s is not supported by len()", args[0].getType()));
           }
        }));
    }
}

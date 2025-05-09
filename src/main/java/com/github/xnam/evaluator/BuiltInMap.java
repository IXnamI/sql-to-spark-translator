package com.github.xnam.evaluator;

import com.github.xnam.object.Array;
import com.github.xnam.object.BuiltIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: Convert into Window functions, including: rank, lag, last_value, count, first_value, min, dense_rank, ntile, sum, row_number, lead, avg, max, percent_rank, cume_dist, nth_value
public class BuiltInMap {
    public Map<String, BuiltIn> store = new HashMap();
    public BuiltInMap() {
        initMap();
    }

    private void initMap() {
        //String Built-ins
        store.put("len", new BuiltIn(args -> {
           if (args.length != 1) return new com.github.xnam.object.Error(String.format("Inappropriate amount of arguments, expected = %d, got = %d", 1, args.length));
           if (args[0] instanceof com.github.xnam.object.String) {
               com.github.xnam.object.String string = (com.github.xnam.object.String) args[0];
               return new com.github.xnam.object.Integer(string.getValue().length());
           } else {
               return new com.github.xnam.object.Error(String.format("Type %s is not supported by len()", args[0].getType()));
           }
        }));
        //Array Built-ins
        store.put("first", new BuiltIn(args -> {
            if (args.length != 1) return new com.github.xnam.object.Error(String.format("Inappropriate amount of arguments, expected = %d, got = %d", 1, args.length));
            if (args[0] instanceof Array) {
                Array array = (Array) args[0];
                return array.getElements().get(0);
            } else {
                return new com.github.xnam.object.Error(String.format("Tye %s is not supported by first()", args[0].getType()));
            }
        }));
        store.put("last", new BuiltIn(args -> {
            if (args.length != 1) return new com.github.xnam.object.Error(String.format("Inappropriate amount of arguments, expected = %d, got = %d", 1, args.length));
            if (args[0] instanceof Array) {
                Array array = (Array) args[0];
                return array.getElements().get(array.getElements().size()-1);
            } else {
                return new com.github.xnam.object.Error(String.format("Tye %s is not supported by first()", args[0].getType()));
            }
        }));
    }
}

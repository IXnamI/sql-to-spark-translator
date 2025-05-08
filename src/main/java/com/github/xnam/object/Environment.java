package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Environment {
    HashMap<java.lang.String, Object> store;
    Environment outer;

    public Environment(Environment outer) {
        this.store = new HashMap<>();
        this.outer = outer;
    }

    public Environment() {
        this(null);
    }

    public Object get(java.lang.String name){
        Object envObj = store.getOrDefault(name, null);
        if (envObj == null) {
            if (outer == null) return null;
            return outer.get(name);
        }
        return envObj;
    }

    public Object set(java.lang.String name, Object value){
        this.store.put(name, value);
        return value;
    }
}

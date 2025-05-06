package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Environment {
    HashMap<String, Object> store;

    public Environment() {
        this.store = new HashMap<>();
    }

    public Object get(String name){
        return this.store.getOrDefault(name, null);
    }

    public Object set(String name, Object value){
        this.store.put(name, value);
        return value;
    }
}

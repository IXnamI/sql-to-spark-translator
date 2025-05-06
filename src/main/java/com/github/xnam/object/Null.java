package com.github.xnam.object;

public class Null implements Object {
    public String getType() { return ObjectType.NULL_OBJ; }
    public String inspect() { return "null"; }
}

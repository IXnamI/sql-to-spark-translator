package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuiltIn implements Object {
    BuiltInFunction func;

    public BuiltIn(BuiltInFunction func) {
        this.func = func;
    }

    public java.lang.String getType() { return ObjectType.BUILTIN_OBJ; }
    public java.lang.String inspect() { return "builtin function"; }
}

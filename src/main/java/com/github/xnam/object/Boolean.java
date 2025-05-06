package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Boolean implements Object {
    java.lang.Boolean value;

    public Boolean(java.lang.Boolean value) {
        this.value = value;
    }

    public java.lang.String getType() { return ObjectType.BOOLEAN_OBJ; }
    public java.lang.String inspect() {
        return java.lang.String.format("%b", value);
    }
}

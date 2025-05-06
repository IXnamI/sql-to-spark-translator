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

    public String getType() { return ObjectType.BOOLEAN_OBJ; }
    public String inspect() {
        return String.format("%b", value);
    }
}

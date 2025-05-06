package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class String implements Object{
    java.lang.String value;

    public String(java.lang.String value) {
        this.value = value;
    }

    public java.lang.String getType() { return ObjectType.STRING_OBJ; }
    public java.lang.String inspect() { return value; }
}

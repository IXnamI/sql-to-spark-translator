package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Integer implements Object{
    java.lang.Integer value;

    public Integer(int value) {
        this.value = value;
    }

    public java.lang.String getType() { return ObjectType.INTEGER_OBJ; }
    public java.lang.String inspect() {
        return java.lang.String.format("%d", value);
    }
}

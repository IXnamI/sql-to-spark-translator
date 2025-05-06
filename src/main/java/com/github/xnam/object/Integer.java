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

    public String getType() { return ObjectType.INTEGER_OBJ; }
    public String inspect() {
        return String.format("%d", value);
    }
}

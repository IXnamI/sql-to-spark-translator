package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnValue implements Object {
    Object value;

    public ReturnValue(Object returnVal) {
        this.value = returnVal;
    }

    public java.lang.String getType() { return ObjectType.RETURN_VALUE_OBJ; }
    public java.lang.String inspect() { return value.inspect(); }
}

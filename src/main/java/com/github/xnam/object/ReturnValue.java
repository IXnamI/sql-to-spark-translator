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

    public String getType() { return ObjectType.RETURN_VALUE_OBJ; }
    public String inspect() { return value.inspect(); }
}

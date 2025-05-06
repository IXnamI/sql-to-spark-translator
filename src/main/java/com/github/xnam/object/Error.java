package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error implements Object {
    java.lang.String message;

    public Error(java.lang.String msg) {
        this.message = msg;
    }

    public java.lang.String getType() { return ObjectType.ERROR_OBJ; }
    public java.lang.String inspect() { return message; }
}

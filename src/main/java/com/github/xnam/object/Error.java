package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error implements Object {
    String message;

    public Error(String msg) {
        this.message = msg;
    }

    public String getType() { return ObjectType.ERROR_OBJ; }
    public String inspect() { return message; }
}

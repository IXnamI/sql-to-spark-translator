package com.github.xnam.object;

import lombok.Getter;
import lombok.Setter;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Array implements Object {
    List<Object> elements;

    public Array(List<Object> elements) {
        this.elements = elements;
    }

    public java.lang.String getType() { return ObjectType.ARRAY_OBJ; }
    public java.lang.String inspect() {
        StringBuilder output = new StringBuilder();
        List<java.lang.String> stringElems = new ArrayList<>();
        for (Object elem : elements) {
            stringElems.add(elem.inspect());
        }
        output.append("[");
        output.append(String.join(", ", stringElems));
        output.append("]");

        return output.toString();
    }
}

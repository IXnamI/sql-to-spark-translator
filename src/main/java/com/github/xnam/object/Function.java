package com.github.xnam.object;

import com.github.xnam.ast.BlockStatement;
import com.github.xnam.ast.Identifier;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Function implements Object {
    List<Identifier> params;
    BlockStatement body;
    Environment env;

    public Function(Environment env) {
        this.env = env;
    }

    public java.lang.String getType() { return ObjectType.FUNCTION_OBJ; }
    public java.lang.String inspect() {
        StringBuilder output = new StringBuilder();
        List<java.lang.String> paramsAsString = new ArrayList<>();
        for (Identifier ident : params) {
            paramsAsString.add(ident.toString());
        }
        output.append("fn");
        output.append("(");
        output.append(java.lang.String.join(", ", paramsAsString));
        output.append(") {\n");
        output.append(body.toString());
        output.append("\n}");
        return output.toString();
    }
}

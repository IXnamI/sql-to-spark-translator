package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CallExpression implements Expression{
    Token token;
    Expression function;
    List<Expression> arguments;

    public CallExpression(Token token) {
        this.token = token;
        this.arguments = new ArrayList<>();
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        List<String> stringArgs = new ArrayList<>();
        for (Expression arg : arguments) {
            stringArgs.add(arg.toString());
        }
        output.append(function.toString());
        output.append("(");
        output.append(String.join(", ", stringArgs));
        output.append(")");
        return output.toString();
    }
}

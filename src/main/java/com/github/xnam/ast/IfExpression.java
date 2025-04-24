package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IfExpression implements Expression {
    Token token;
    Expression condition;
    BlockStatement consequence;
    BlockStatement alternative;

    public IfExpression(Token token) {
        this.token = token;
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("if ");
        output.append(condition.toString());
        output.append(" ");
        output.append(consequence.toString());
        if (alternative != null) {
            output.append("else ");
            output.append(alternative.toString());
        }
        return output.toString();
    }
}

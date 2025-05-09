package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexExpression implements Expression{
    Token token;
    Expression left;
    Expression index;

    public IndexExpression(Token token, Expression left) {
        this.token = token;
        this.left = left;
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("(");
        output.append(left.toString());
        output.append("[");
        output.append(index.toString());
        output.append("])");
        return output.toString();
    }
}

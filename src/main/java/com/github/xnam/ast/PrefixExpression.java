package com.github.xnam.ast;


import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrefixExpression implements  Expression{
    Token token;
    String operator;
    Expression rightExpression;

    public PrefixExpression(Token token) {
        this(token, null);
    }

    public PrefixExpression(Token token, Expression rightExpression) {
        this.token = token;
        this.operator = token.getLiteral();
        this.rightExpression = rightExpression;
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("(");
        output.append(operator);
        output.append(rightExpression.toString());
        output.append(")");
        return output.toString();
    }
}

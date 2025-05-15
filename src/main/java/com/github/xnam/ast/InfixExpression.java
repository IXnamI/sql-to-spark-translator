package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfixExpression implements Expression{
    Token token;
    Expression leftExpression;
    String operator;
    Expression rightExpression;

    public InfixExpression(Token token) {
        this(token, null, null);
    }

    public InfixExpression(Token token, Expression left, Expression right) {
        this.token = token;
        this.leftExpression = left;
        this.operator = token.getLiteral();
        this.rightExpression = right;
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("(");
        output.append(leftExpression.toString());
        output.append(" ").append(operator).append(" ");
        output.append(rightExpression.toString());
        output.append(")");
        return output.toString();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

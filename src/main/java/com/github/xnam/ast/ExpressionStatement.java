package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpressionStatement implements Statement{
    Token token;
    Expression expression;

    public ExpressionStatement(Token token){
       this(null, token);
    }

    public ExpressionStatement(Expression expr, Token token) {
        this.expression = expr;
        this.token = token;
    }
    public String tokenLiteral() { return token.getLiteral(); }
    public void statementNode() {}
    public String toString() {
        if(expression != null) return expression.toString();
        return "";
    }
}

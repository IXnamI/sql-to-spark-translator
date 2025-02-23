package com.github.xnam.ast;

import com.github.xnam.token.Token;

public class Identifier implements Expression {
    Token token;
    String value;

    public String tokenLiteral() {
        return token.getLiteral();
    }

    public void expressionNode() {}
}

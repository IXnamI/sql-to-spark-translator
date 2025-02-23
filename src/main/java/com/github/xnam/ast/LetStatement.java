package com.github.xnam.ast;

import com.github.xnam.token.Token;

public class LetStatement implements Statement{
    Token token;
    Identifier name;
    Expression value;

    public String tokenLiteral() {
        return token.getLiteral();
    }
    public void statementNode() {}
}

package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Boolean implements Expression{
    Token token;
    java.lang.Boolean value;

    public Boolean(Token token) {
        this.token = token;
        this.value = java.lang.Boolean.parseBoolean(token.getLiteral());
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() { return tokenLiteral(); }
}

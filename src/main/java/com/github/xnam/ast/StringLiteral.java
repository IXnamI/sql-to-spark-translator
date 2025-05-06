package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringLiteral implements Expression{
    Token token;
    String value;

    public StringLiteral(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        return value;
    }
}

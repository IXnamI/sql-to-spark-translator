package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WhenThenClause implements Clause{
    Token token;
    Expression whenExpr;
    Expression thenExpr;

    public WhenThenClause(Token token) {
        this.token = token;
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    //TODO: Figure out how to print this
    public String toString() {
        StringBuilder output = new StringBuilder();
        return output.toString();
    }
}

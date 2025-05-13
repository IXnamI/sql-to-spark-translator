package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WhereClause implements Clause{
    Token token;
    Expression where;

    public WhereClause(Token token) {
        this.token = token;
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(token.getLiteral());
        output.append(" ");
        output.append(where.toString());
        return output.toString();
    }
}
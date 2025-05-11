package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitOffsetClause implements Clause{
    Token token;
    Integer value;

    public LimitOffsetClause(Token token, Integer value) {
        this.token = token;
        this.value = value;
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(token.getLiteral());
        output.append(" ");
        output.append(value);
        return output.toString();
    }
}

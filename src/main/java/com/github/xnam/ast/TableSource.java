package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableSource implements Expression{
    Token token;
    String tableName;
    String alias;

    public TableSource(Token token) {
        this.token = token;
    }

    public void expressionNode() { }
    public String tokenLiteral() { return token.getLiteral(); };
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(tableName);
        if (alias != null) {
            output.append(" AS ");
            output.append(alias);
        }
        return output.toString();
    }
}

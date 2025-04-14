package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReturnStatement implements Statement {
    Token token;
    Expression returnValue;

    public ReturnStatement(Token token) {
        this.token = token;
    }

    public void statementNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(tokenLiteral()).append(" ");
//        if (returnValue != null) output.append(returnValue.toString());
        output.append(";");
        return output.toString();
    }
}

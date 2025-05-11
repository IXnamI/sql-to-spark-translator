package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CaseExpression implements Expression {
    Token token;
    List<WhenThenClause> whenThens;
    Expression elseExpr;

    public CaseExpression(Token token) {
        this.token = token;
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    //TODO: Figure out how to print this
    public String toString() {
        StringBuilder output = new StringBuilder();
        return output.toString();
    }
}

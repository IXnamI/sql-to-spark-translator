package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import com.github.xnam.token.TokenType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CaseExpression implements Expression {
    Token token;
    List<Clause> whenThens;
    Expression elseExpr;

    public CaseExpression(Token token) {
        this.token = token;
        this.whenThens = new ArrayList<>();
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(tokenLiteral());
        for (Clause clause : whenThens) {
            output.append(" ").append(clause.toString());
        }
        if (elseExpr != null) {
            output.append(" ELSE ").append(elseExpr.toString());
        }
        output.append(" END");
        return output.toString();
    }
    public boolean hasElse() {
        return elseExpr != null;
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

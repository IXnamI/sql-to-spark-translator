package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderByItem implements Clause {
    Token token;
    Expression orderByExpression;
    boolean asc;

    public OrderByItem(Token token) {
        this.token = token;
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); };
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(orderByExpression.toString());
        String direction = asc ? "ASC" : "DESC";
        output.append(" ").append(direction);
        return output.toString();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

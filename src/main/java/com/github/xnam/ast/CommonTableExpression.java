package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommonTableExpression implements Clause{
    Token token;
    List<Expression> columnAliases;
    Statement subquery;

    public CommonTableExpression(Token token) {
        this.token = token;
        this.columnAliases = new ArrayList<>();
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(token.getLiteral());
        List<String> stringColumnAliases = new ArrayList<>();
        for (Expression item : columnAliases) {
            stringColumnAliases.add(item.toString());
        }
        if (!columnAliases.isEmpty()) {
            output.append(" (").append(String.join(", ", stringColumnAliases)).append(")");
        }
        output.append(" AS (").append(subquery.toString()).append(")");
        return output.toString();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

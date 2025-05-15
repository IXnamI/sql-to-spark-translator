package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinClause implements Clause {
    Token token;
    String joinType;
    Expression table;
    Expression onCondition;

    public JoinClause(Token token) {
        this.token = token;
        joinType = JoinType.INNER;
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); };
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(joinType).append(" ");
        output.append(tokenLiteral()).append(" ");
        output.append(table.toString());
        output.append(" ON ");
        output.append(onCondition.toString());
        return output.toString();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

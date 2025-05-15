package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnReference implements Expression {
    Token token;
    Expression tableName;
    Expression columnName;

    public ColumnReference(Token token) {
        this.token = token;
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); };
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (tableName != null) {
            output.append(tableName);
            output.append(".");
        }
        output.append(columnName.toString());
        return output.toString();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

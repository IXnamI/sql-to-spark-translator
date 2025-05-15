package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableSource implements Expression{
    Token token;
    Expression databaseName;
    Expression schemaName;
    Node table;
    Expression alias;

    public TableSource(Token token) {
        this.token = token;
    }

    public void expressionNode() { }
    public String tokenLiteral() { return token.getLiteral(); };
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(table);
        if (alias != null) {
            output.append(" AS ");
            output.append(alias);
        }
        return output.toString();
    }
    public void shiftSchemaNameToDatabaseName(Expression newSchemaName) {
        this.databaseName = this.schemaName;
        this.schemaName = newSchemaName;
    }
    public void shiftTableNameToSchemaName(Expression newTableName) {
        this.schemaName = (Expression) this.table;
        this.table = newTableName;
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

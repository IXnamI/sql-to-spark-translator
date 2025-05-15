package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SelectStatement implements Statement {
    Token token;
    List<SelectItem> selectItems;
    TableSource from;
    WhereClause where;
    List<JoinClause> joins;
    GroupByClause groupBy;
    HavingClause having;
    OrderByClause orderBy;
    LimitClause limit;
    OffsetClause offset;

    public SelectStatement(Token token) {
        this.token = token;
        selectItems = new ArrayList<>();
        joins = new ArrayList<>();
    }

    public void statementNode() {}
    public String tokenLiteral() { return token.getLiteral(); };
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(tokenLiteral()).append(" ");
        List<String> stringSelectItems = new ArrayList<>();
        for (SelectItem item : selectItems) {
            stringSelectItems.add(item.toString());
        }
        output.append(String.join(", ", stringSelectItems));
        output.append(" FROM ").append(from.toString());
        if (!joins.isEmpty()) {
            List<String> stringJoins = new ArrayList<>();
            for (JoinClause item : joins) {
                stringJoins.add(item.toString());
            }
            output.append(" ").append(String.join(" ", stringJoins));
        }
        if (where != null) output.append(" ").append(where.toString());
        if (groupBy != null) {
            output.append(" ").append(groupBy.toString());
            if (having != null) output.append(" ").append(having.toString());
        }
        if (orderBy != null) output.append(" ").append(orderBy.toString());
        if (limit != null) output.append(" ").append(limit.toString());
        if (offset != null) output.append(" ").append(offset.toString());
        return output.toString();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

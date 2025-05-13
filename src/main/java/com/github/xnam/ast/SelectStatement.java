package com.github.xnam.ast;

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
        output.append(tokenLiteral());
        List<String> stringSelectItems = new ArrayList<>();
        for (SelectItem item : selectItems) {
            stringSelectItems.add(item.toString());
        }
        output.append(String.join(", ", stringSelectItems));
        output.append(from.toString());
        if (where != null) output.append(where.toString());
        if (!joins.isEmpty()) {
            List<String> stringJoins = new ArrayList<>();
            for (JoinClause item : joins) {
                stringJoins.add(item.toString());
            }
            output.append(String.join(", ", stringJoins));
        }
        if (groupBy != null) output.append(groupBy.toString()).append(having.toString());
        if (limit != null) output.append(limit.toString());
        if (offset != null) output.append(offset.toString());
        return output.toString();
    }
}

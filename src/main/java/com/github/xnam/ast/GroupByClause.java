package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GroupByClause implements Clause{
    Token token;
    List<Expression> groupByItems;

    public GroupByClause(Token token) {
        this.token = token;
        this.groupByItems = new ArrayList<>();
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        List<String> stringGroupBy = new ArrayList<>();
        for (Expression item : groupByItems) {
            stringGroupBy.add(item.toString());
        }
        output.append(token.getLiteral());
        output.append(" ");
        output.append(String.join(", ", stringGroupBy));
        return output.toString();
    }
}

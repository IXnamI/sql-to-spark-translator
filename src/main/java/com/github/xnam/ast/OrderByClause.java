package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderByClause implements Clause{
    Token token;
    List<OrderByItem> orderByItems;

    public OrderByClause(Token token) {
        this.token = token;
        this.orderByItems = new ArrayList<>();
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        List<String> stringOrderBy = new ArrayList<>();
        for (OrderByItem item : orderByItems) {
            stringOrderBy.add(item.toString());
        }
        output.append(token.getLiteral());
        output.append(" ");
        output.append(String.join(", ", stringOrderBy));
        return output.toString();
    }
}

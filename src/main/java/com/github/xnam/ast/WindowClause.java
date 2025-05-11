package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WindowClause implements Clause{
    Token token;
    List<Expression> partitionBy;
    OrderByClause orderBy;
    FrameClause frame;

    public WindowClause(Token token) {
        this.token = token;
    }

    public void clauseNode() { }
    public String tokenLiteral() { return token.getLiteral(); };
    public String toString() {
        StringBuilder output = new StringBuilder();
        List<String> stringPartitionBy = new ArrayList<>();
        for (Expression expr : partitionBy) {
            stringPartitionBy.add(expr.toString());
        }
        output.append("(");
        output.append("PARTITION BY ");
        output.append(String.join(", ", stringPartitionBy));
        output.append(orderBy.toString());
        output.append(frame.toString());
        output.append(")");
        return output.toString();
    }
}

package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WithStatement implements Statement {
    Token token;
    List<CommonTableExpression> cteList;
    Statement mainQuery;

    public WithStatement(Token token) {
        this.token = token;
        this.cteList = new ArrayList<>();
    }

    public void statementNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(tokenLiteral()).append(" ");
        List<String> stringCteList = new ArrayList<>();
        for (CommonTableExpression cte : cteList) {
            stringCteList.add(cte.toString());
        }
        output.append(String.join(", ", stringCteList)).append(" ");
        if (mainQuery != null) {
            output.append(mainQuery.toString());
        }
        return output.toString();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

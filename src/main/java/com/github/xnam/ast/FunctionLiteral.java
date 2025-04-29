package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FunctionLiteral implements Expression {
    Token token;
    List<Identifier> params;
    BlockStatement body;

    public FunctionLiteral(Token token) {
        this.token = token;
        params = new ArrayList<>();
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        List<String> paramList = new ArrayList<>();
        for (Identifier param : params) paramList.add(param.toString());
        output.append(tokenLiteral());
        output.append("(");
        output.append(String.join(", ", paramList));
        output.append(") ");
        output.append(body.toString());
        return output.toString();
    }
}

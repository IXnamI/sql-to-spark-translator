package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrameClause implements Clause {
    Token token;
    String mode;
    String start;
    String end;

    public FrameClause(Token token) {
        this.token = token;
    }

    public void clauseNode() { }
    public String tokenLiteral() { return token.getLiteral(); };
    //TODO: Need to figure out how to print this correctly
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(mode);
        output.append(start);
        output.append(end);
        return output.toString();
    }
}

package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrameBound implements Clause {
    Token token;
    String boundType;
    Integer numRows;

    public FrameBound(Token token) {
        this.token = token;
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (!boundType.equals(FrameBoundType.N_FOLLOWING) && !boundType.equals(FrameBoundType.N_PRECEDING)) output.append(boundType);
        else {
            output.append(numRows).append(" ");
            output.append(boundType.equals(FrameBoundType.N_PRECEDING) ? "PRECEDING" : "FOLLOWING");
        }
        return output.toString();
    }
    public boolean hasConcreteRowsNum() { return boundType.equals(FrameBoundType.N_PRECEDING) || boundType.equals(FrameBoundType.N_FOLLOWING); }
}


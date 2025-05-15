package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitClause implements Clause{
    Token token;
    Expression value;

    public LimitClause(Token token) {
        this.token = token;
    }

    public void clauseNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(token.getLiteral());
        output.append(" ");
        output.append(value.toString());
        return output.toString();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

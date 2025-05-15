package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Identifier implements Expression {
    Token token;
    String value;

    public Identifier(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    public String tokenLiteral() {
        return token.getLiteral();
    }
    public void expressionNode() {}
    public String toString() { return value; }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

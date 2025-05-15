package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegerLiteral implements Expression{
    Token token;
    Integer value;

    public IntegerLiteral(Token token, Integer value) {
       this.token = token;
       this.value = value;
    }

    public IntegerLiteral(Token token) {
        this(token, null);
    }
    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        return token.getLiteral();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpressionStatement implements Statement{
    Token token;
    Expression expression;
    Node parsedNode;

    public ExpressionStatement(Token token){
       this(null, token);
    }

    public ExpressionStatement(Expression expr, Token token) {
        this.expression = expr;
        this.token = token;
    }
    public String tokenLiteral() { return token.getLiteral(); }
    public void statementNode() {}
    public String toString() {
        if(expression != null) return expression.toString();
        if (parsedNode != null) return parsedNode.toString();
        return "";
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

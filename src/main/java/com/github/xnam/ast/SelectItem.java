package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;
import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectItem implements Expression {
    Token token;
    Expression selectExpression;
    Expression alias;

    public SelectItem(Token token) {
        this.token = token;
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("(");
        output.append(selectExpression.toString());
        if (alias != null) {
            output.append(" as ");
            output.append(alias);
        }
        output.append(")");
        return output.toString();
    }
    public <R> R accept(CodegenVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

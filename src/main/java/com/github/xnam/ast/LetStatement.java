package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LetStatement implements Statement{
    private Token token;
    private Identifier name;
    private Expression value;

    public LetStatement(Token token) {
       this.token = token;
    }

    public String tokenLiteral() {
        return token.getLiteral();
    }
    public void statementNode() {}

    @Override
    public boolean equals(Object anotherObject) {
        if (this == anotherObject) return true;
        if (anotherObject == null || getClass() != anotherObject.getClass()) return false;
        LetStatement anotherLetStatement = (LetStatement) anotherObject;
        if (!tokenLiteral().equals(anotherLetStatement.tokenLiteral())) return false;
        if (getName().getValue().equals(anotherLetStatement.getName().getValue())) return false;
        return getName().tokenLiteral().equals(anotherLetStatement.getName().tokenLiteral());
    }

    @Override
    public String toString() {
        return "LetStatement {Token: "+ token + ", Name: " + name + "}";
    }
}

package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BlockStatement implements Statement{
    Token token;
    List<Statement> statements;

    public BlockStatement(Token token) {
       this.token = token;
       this.statements = new ArrayList<>();
    }

    public void statementNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Statement statement : statements) {
           output.append(statement);
        }
        return output.toString();
    }
}

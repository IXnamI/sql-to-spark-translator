package com.github.xnam.ast;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Program implements Node{
    public List<Statement> statements;

    public Program() {
        statements = new ArrayList<>();
    }

    public String tokenLiteral() {
        return statements.isEmpty() ? "" : statements.get(0).tokenLiteral();
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Statement s : statements) {
            output.append(s.toString());
        }
        return output.toString();
    }

}

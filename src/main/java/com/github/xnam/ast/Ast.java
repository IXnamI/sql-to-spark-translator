package com.github.xnam.ast;

interface Node {
    public String tokenLiteral();
}

interface Expression extends Node{
    public void expressionNode();
}

interface Statement extends Node {
    public void statementNode();
}

public class Ast {
}

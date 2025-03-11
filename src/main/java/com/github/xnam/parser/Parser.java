package com.github.xnam.parser;

import com.github.xnam.ast.*;
import com.github.xnam.token.Token;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    Lexer lexer;
    Token curToken;
    Token peekToken;
    List<String> errors;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        nextToken();
        nextToken();
    }

    private void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public Program parseProgram() {
        Program program = new Program();
        while (!curToken.getType().equals(TokenType.EOF)) {
           Statement stmt = parseStatement();
           if (stmt != null) program.getStatements().add(stmt);
           nextToken();
        }
        return program;
    }

    public Statement parseStatement() {
        switch (curToken.getType()) {
            case TokenType.LET:
                return parseLetStatement();
            case TokenType.RETURN:
                return parseReturnStatement();
            case TokenType.IF:
                return null;
            case TokenType.ELSE:
                return null;
            default:
                return null;
        }
    }

    private ReturnStatement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement(curToken);
        nextToken();
        stmt.setReturnValue(parseExpression());
        return stmt;
    }

    private LetStatement parseLetStatement() {
        LetStatement stmt = new LetStatement(curToken);
        if (!expectPeek(TokenType.IDENT)) return null;
        stmt.setName(new Identifier(curToken, curToken.getLiteral()));
        if (!expectPeek(TokenType.ASSIGN)) return null;
        while (!curTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return stmt;
    }

    private Expression parseExpression() {
        return null;
    }

    private boolean expectPeek(String tokType) {
        if (peekTokenIs(tokType)) {
            nextToken();
            return true;
        }
        peekError(tokType);
        return false;
    }

    private boolean curTokenIs(String tokType) {
        return curToken.getType().equals(tokType);
    }

    private boolean peekTokenIs(String tokType) {
        return peekToken.getType().equals(tokType);
    }

    public List<String> Errors() {
        return errors;
    }

    public void peekError(String tokType) {
        String msg = String.format("Expected next token to be %s, got %s instead", tokType, peekToken.getType());
        errors.add(msg);
    }
}

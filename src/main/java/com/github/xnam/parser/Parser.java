package com.github.xnam.parser;

import com.github.xnam.ast.*;
import com.github.xnam.token.Token;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Parser {
    Lexer lexer;
    Token curToken;
    Token peekToken;
    List<String> errors;
    Map<String, Function<Expression, Expression>> infixParseFns;
    Map<String, Supplier<Expression>> prefixParseFns;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        infixParseFns = new HashMap<>();
        prefixParseFns = new HashMap<>();
        registerPrefix(TokenType.IDENT, this::parseIdentifier);
        registerPrefix(TokenType.INT, this::parseIntegerLiteral);
        nextToken();
        nextToken();
    }

    private void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public void registerPrefix(String tokType, Supplier<Expression> prefixParseFn) {
        prefixParseFns.putIfAbsent(tokType, prefixParseFn);
    }

    public void registerInfix(String tokType, Function<Expression, Expression> infixParseFn) {
        infixParseFns.putIfAbsent(tokType, infixParseFn);
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
                return parseExpressionStatement();
        }
    }

    private ReturnStatement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement(curToken);
        nextToken();
        stmt.setReturnValue(parseExpression(Precedence.LOWEST));
        while (!curTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
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

    private ExpressionStatement parseExpressionStatement() {
        ExpressionStatement stmt = new ExpressionStatement(curToken);
        stmt.setExpression(parseExpression(Precedence.LOWEST));

        if (peekTokenIs(TokenType.SEMICOLON)) nextToken();
        return stmt;
    }
    private Expression parseExpression(int precedence) {
        Supplier<Expression> prefixParseFn = prefixParseFns.getOrDefault(curToken.getType(), null);
        if (prefixParseFn == null) return null;
        Expression leftExp = prefixParseFn.get();
        return leftExp;
    }

    private Expression parseIdentifier() {
        return new Identifier(curToken, curToken.getLiteral());
    }

    private Expression parseIntegerLiteral() {
        IntegerLiteral literal = new IntegerLiteral(curToken);
        try {
            Integer valueInInt = Integer.parseInt(curToken.getLiteral());
            literal.setValue(valueInInt);
        } catch (Exception e) {
            String msg = String.format("Could not parse %s as integer", curToken.getLiteral());
            errors.add(msg);
            return null;
        }
        assert literal.getValue() != null : "This value cannot be null";
        return literal;
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

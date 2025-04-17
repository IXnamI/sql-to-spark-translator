package com.github.xnam.parser;

import com.github.xnam.ast.*;
import com.github.xnam.token.Token;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.token.TokenType;
import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Parser {
    Lexer lexer;
    Token curToken;
    Token peekToken;
    List<String> errors;
    Map<String, Function<Expression, Expression>> infixParseFns;
    Map<String, Supplier<Expression>> prefixParseFns;
    public static final Map<String, Integer> precedences = createPrecedenceMap();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        infixParseFns = new HashMap<>();
        prefixParseFns = new HashMap<>();
        registerPrefix(TokenType.IDENT, this::parseIdentifier);
        registerPrefix(TokenType.INT, this::parseIntegerLiteral);
        registerPrefix(TokenType.BANG, this::parsePrefixExpression);
        registerPrefix(TokenType.MINUS, this::parsePrefixExpression);
        registerInfix(TokenType.PLUS, this::parseInfixExpression);
        registerInfix(TokenType.MINUS, this::parseInfixExpression);
        registerInfix(TokenType.SLASH, this::parseInfixExpression);
        registerInfix(TokenType.ASTERISK, this::parseInfixExpression);
        registerInfix(TokenType.EQ, this::parseInfixExpression);
        registerInfix(TokenType.NOT_EQ, this::parseInfixExpression);
        registerInfix(TokenType.LT, this::parseInfixExpression);
        registerInfix(TokenType.GT, this::parseInfixExpression);
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
        if (prefixParseFn == null) {
            noPrefixParseFunctionError(curToken.getType());
            return null;
        }
        Expression leftExp = prefixParseFn.get();
        while(!peekTokenIs(TokenType.SEMICOLON) && (precedence < peekPrecedence())) {
            Function<Expression, Expression> infixParseFn = infixParseFns.getOrDefault(peekToken.getType(), null);
            if (infixParseFn == null) {
                return leftExp;
            }
            nextToken();

            leftExp = infixParseFn.apply(leftExp);
        }
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

    public Expression parsePrefixExpression() {
        PrefixExpression prefExpr = new PrefixExpression(curToken);
        nextToken();
        prefExpr.setRightExpression(parseExpression(Precedence.PREFIX));
        return prefExpr;
    }

    public Expression parseInfixExpression(Expression leftExpression) {
        InfixExpression expr = new InfixExpression(curToken);
        expr.setLeftExpression(leftExpression);
        int precedence = curPrecedence();
        nextToken();
        expr.setRightExpression(parseExpression(precedence));
        return expr;
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

    private int peekPrecedence() {
        return precedences.getOrDefault(peekToken.getType(), Precedence.LOWEST);
    }

    private int curPrecedence() {
        return precedences.getOrDefault(curToken.getType(), Precedence.LOWEST);
    }

    public List<String> Errors() {
        return errors;
    }

    public void peekError(String tokType) {
        String msg = String.format("Expected next token to be %s, got %s instead", tokType, peekToken.getType());
        errors.add(msg);
    }

    public void noPrefixParseFunctionError(String tokType) {
        String msg = String.format("No prefix parse function for %s was found", tokType);
        errors.add(msg);
    }

    private static Map<String, Integer> createPrecedenceMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put(TokenType.EQ, Precedence.EQUALS);
        map.put(TokenType.NOT_EQ, Precedence.EQUALS);
        map.put(TokenType.LT, Precedence.LESSGREATER);
        map.put(TokenType.GT, Precedence.LESSGREATER);
        map.put(TokenType.PLUS, Precedence.SUM);
        map.put(TokenType.MINUS, Precedence.SUM);
        map.put(TokenType.SLASH, Precedence.PRODUCT);
        map.put(TokenType.ASTERISK, Precedence.PRODUCT);
        return Collections.unmodifiableMap(map);
    }
}

package com.github.xnam.parser;

import com.github.xnam.ast.*;
import com.github.xnam.ast.Boolean;
import com.github.xnam.token.Token;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.token.TokenType;
import com.github.xnam.utils.LoggingUtils;
import com.sun.javafx.util.Logging;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Parser {
    Lexer lexer;
    Token curToken;
    Token peekToken;
    List<String> errors;
    List<String> debug;
    Map<String, Function<Expression, Expression>> infixParseFns;
    Map<String, Supplier<Expression>> prefixParseFns;
    public static final Map<String, Integer> precedences = createPrecedenceMap();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        this.debug = new ArrayList<>();
        infixParseFns = new HashMap<>();
        prefixParseFns = new HashMap<>();
        registerPrefix(TokenType.IDENT, this::parseIdentifier);
        registerPrefix(TokenType.INT, this::parseIntegerLiteral);
        registerPrefix(TokenType.MINUS, this::parsePrefixExpression);
        registerPrefix(TokenType.LPAREN, this::parseGroupedExpression);
        registerPrefix(TokenType.STRING, this::parseStringLiteral);
        registerPrefix(TokenType.LBRACKET, this::parseArrayLiteral);
        registerInfix(TokenType.PLUS, this::parseInfixExpression);
        registerInfix(TokenType.MINUS, this::parseInfixExpression);
        registerInfix(TokenType.SLASH, this::parseInfixExpression);
        registerInfix(TokenType.ASTERISK, this::parseInfixExpression);
        registerInfix(TokenType.EQ, this::parseInfixExpression);
        registerInfix(TokenType.NOT_EQ, this::parseInfixExpression);
        registerInfix(TokenType.LT, this::parseInfixExpression);
        registerInfix(TokenType.GT, this::parseInfixExpression);
        registerInfix(TokenType.LPAREN, this::parseCallExpression);
        registerInfix(TokenType.LBRACKET, this::parseIndexExpression);
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
            default:
                return parseExpressionStatement();
        }
    }

    private ReturnStatement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement(curToken);
        nextToken();
        stmt.setReturnValue(parseExpression(Precedence.LOWEST));
        if (!expectPeek(TokenType.SEMICOLON)) return null;
        return stmt;
    }

    private LetStatement parseLetStatement() {
        LetStatement stmt = new LetStatement(curToken);
        if (!expectPeek(TokenType.IDENT)) return null;
        stmt.setName(new Identifier(curToken, curToken.getLiteral()));
        if (!expectPeek(TokenType.ASSIGN)) return null;
        nextToken();
        stmt.setValue(parseExpression(Precedence.LOWEST));
        if (!expectPeek(TokenType.SEMICOLON)) return null;
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

    private Expression parseBoolean() {
        return new Boolean(curToken);
    }

    private Expression parseIfExpression() {
        IfExpression ifExpr = new IfExpression(curToken);
        if (!expectPeek(TokenType.LPAREN)) return null;
        ifExpr.setCondition(parseExpression(Precedence.LOWEST));
        ifExpr.setConsequence(parseBlockStatement());
        if (peekTokenIs(TokenType.ELSE)) {
            nextToken();
            ifExpr.setAlternative(parseBlockStatement());
        }
        return ifExpr;
    }

    private Expression parseFunctionLiteral() {
        FunctionLiteral function = new FunctionLiteral(curToken);
        if (!expectPeek(TokenType.LPAREN)) return null;
        function.getParams().addAll(parseFunctionParameters());
        function.setBody(parseBlockStatement());
        return function;
    }

    private List<Identifier> parseFunctionParameters() {
        List<Identifier> identList = new ArrayList<>();
        if (peekTokenIs(TokenType.RPAREN)) {
            nextToken();
            return identList;
        }
        while(!curTokenIs(TokenType.RPAREN)) {
            nextToken();
            Identifier ident = (Identifier) parseIdentifier();
            identList.add(ident);
            assert peekTokenIs(TokenType.COMMA) || peekTokenIs(TokenType.RPAREN);
            nextToken();
        }
        return identList;
    }

    private StringLiteral parseStringLiteral() {
        return new StringLiteral(curToken, curToken.getLiteral());
    }

    private BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement(curToken);
        nextToken();
        while (!curTokenIs(TokenType.RBRACKET) && !curTokenIs(TokenType.EOF)) {
            Statement stmt = parseStatement();
            assert stmt != null : "Failed to parse statement";
            block.getStatements().add(stmt);
            nextToken();
        }
        return block;
    }

    private Expression parseCallExpression(Expression func) {
        CallExpression callExp = new CallExpression(curToken);
        callExp.setFunction(func);
        if (peekTokenIs(TokenType.RPAREN)) {
            return callExp;
        }
        callExp.setArguments(parseCallArguments());
        return callExp;
    }

    private List<Expression> parseCallArguments() {
        List<Expression> argsList = new ArrayList<>();
        nextToken();
        argsList.add(parseExpression(Precedence.LOWEST));
        nextToken();
        while (curTokenIs(TokenType.COMMA)) {
            nextToken();
            argsList.add(parseExpression(Precedence.LOWEST));
            nextToken();
        }
        assert curTokenIs(TokenType.RPAREN) : "Invalid arguments to call expression";
        return argsList;
    }

    private Expression parsePrefixExpression() {
        PrefixExpression prefExpr = new PrefixExpression(curToken);
        nextToken();
        prefExpr.setRightExpression(parseExpression(Precedence.PREFIX));
        return prefExpr;
    }

    private Expression parseInfixExpression(Expression leftExpression) {
        InfixExpression expr = new InfixExpression(curToken);
        expr.setLeftExpression(leftExpression);
        int precedence = curPrecedence();
        nextToken();
        expr.setRightExpression(parseExpression(precedence));
        return expr;
    }

    private Expression parseGroupedExpression() {
        nextToken();
        Expression expr = parseExpression(Precedence.LOWEST);
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        return expr;
    }

    private Expression parseArrayLiteral() {
        ArrayLiteral array = new ArrayLiteral(curToken);
        if (peekTokenIs(TokenType.RBRACKET)) {
            nextToken();
            return array;
        }
        while (!curTokenIs(TokenType.RBRACKET) && !curTokenIs(TokenType.EOF)) {
            nextToken();
            Expression elem = parseExpression(Precedence.LOWEST);
            array.getElements().add(elem);
            nextToken();
        }
        return array;
    }

    private Expression parseIndexExpression(Expression left) {
        IndexExpression idxExpr = new IndexExpression(curToken, left);
        nextToken();
        idxExpr.setIndex(parseExpression(Precedence.LOWEST));
        if (!expectPeek(TokenType.RBRACKET)) return null;
        return idxExpr;
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

    public List<String> Debug() {
        return debug;
    }

    private void addDebugStatement(String msg) {
        debug.add(logWithLocation(msg));
    }

    public void peekError(String tokType) {
        String msg = String.format("Expected next token to be %s, got %s instead", tokType, peekToken.getType());
        errors.add(msg);
    }

    public void noPrefixParseFunctionError(String tokType) {
        String msg = String.format("No prefix parse function for %s was found", tokType);
        errors.add(msg);
    }

    public static String logWithLocation(String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // Index 2 usually points to the caller of this method
        StackTraceElement element = stackTrace[2];
        String fileName = element.getFileName();
        int lineNumber = element.getLineNumber();

        return String.format("[%s:%d] %s", fileName, lineNumber, message);
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
        map.put(TokenType.LPAREN, Precedence.CALL);
        map.put(TokenType.LBRACKET, Precedence.INDEX);
        return Collections.unmodifiableMap(map);
    }
}

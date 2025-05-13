package com.github.xnam.parser;

import com.github.xnam.ast.*;
import com.github.xnam.ast.Boolean;
import com.github.xnam.token.Token;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.token.TokenType;
import com.github.xnam.utils.LoggingUtils;
import com.sun.javafx.util.Logging;
import com.sun.org.apache.xpath.internal.operations.Or;

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
    public static final List<String> clauseStart = Arrays.asList(TokenType.WHERE, TokenType.GROUP, TokenType.HAVING, TokenType.ORDER, TokenType.LIMIT, TokenType.OFFSET);

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        this.debug = new ArrayList<>();
        infixParseFns = new HashMap<>();
        prefixParseFns = new HashMap<>();
        registerPrefix(TokenType.ASTERISK, this::parseColumnName);
        registerPrefix(TokenType.IDENT, this::parseColumnName);
        registerPrefix(TokenType.INT, this::parseIntegerLiteral);
        registerPrefix(TokenType.MINUS, this::parsePrefixExpression);
        registerPrefix(TokenType.NOT, this::parsePrefixExpression);
        registerPrefix(TokenType.LPAREN, this::parseGroupedExpression);
        registerPrefix(TokenType.STRING, this::parseStringLiteral);
        registerPrefix(TokenType.TRUE, this::parseBoolean);
        registerPrefix(TokenType.FALSE, this::parseBoolean);
        registerInfix(TokenType.PLUS, this::parseInfixExpression);
        registerInfix(TokenType.MINUS, this::parseInfixExpression);
        registerInfix(TokenType.SLASH, this::parseInfixExpression);
        registerInfix(TokenType.ASTERISK, this::parseInfixExpression);
        registerInfix(TokenType.EQ, this::parseInfixExpression);
        registerInfix(TokenType.NOT_EQ, this::parseInfixExpression);
        registerInfix(TokenType.LT, this::parseInfixExpression);
        registerInfix(TokenType.GT, this::parseInfixExpression);
        registerInfix(TokenType.LPAREN, this::parseFunctionCall);
        registerInfix(TokenType.DOT, this::parseColumnWithTableReference);
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
            case TokenType.SELECT:
                return parseSelectStatement();
            default:
                //Not really a thing in SQL, only for unit testing parsing expression and clauses
                return parseTestStatement();
        }
    }

    private SelectStatement parseSelectStatement() {
        SelectStatement selectStatement = new SelectStatement(curToken);
        while (isClauseStart(curToken)) {
            Clause clause = parseClause();
        }
        return selectStatement;
    }

    private Statement parseTestStatement() {
        ExpressionStatement expressionStatement = new ExpressionStatement(curToken);
        if (isClauseStart(curToken)) expressionStatement.setParsedNode(parseClause());
        else expressionStatement.setParsedNode(parseExpression(Precedence.LOWEST));
        return expressionStatement;
    }

    private Expression parseExpression(int precedence) {
        Supplier<Expression> prefixParseFn = prefixParseFns.getOrDefault(curToken.getType(), null);
        if (prefixParseFn == null) {
            noPrefixParseFunctionError(curToken.getType());
            return null;
        }
        Expression leftExp = prefixParseFn.get();
        while((!curTokenIs(TokenType.EOF)) && (precedence < peekPrecedence())) {
            Function<Expression, Expression> infixParseFn = infixParseFns.getOrDefault(peekToken.getType(), null);
            if (infixParseFn == null) {
                return leftExp;
            }
            nextToken();

            leftExp = infixParseFn.apply(leftExp);
        }
        return leftExp;
    }

    private Clause parseClause() {
        switch (curToken.getType()) {
            case TokenType.WHERE:
                return parseWhereClause();
            case TokenType.GROUP:
                return parseGroupByClause();
            case TokenType.HAVING:
                return parseHavingClause();
            case TokenType.ORDER:
                return parseOrderByClause();
            case TokenType.LIMIT:
                return parseLimitClause();
            case TokenType.OFFSET:
                return parseOffsetClause();
            default:
                return null;
        }
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

    private Expression parseFunctionCall(Expression func) {
        FunctionCall functionCall = new FunctionCall(curToken);
        functionCall.setFunction(func);
        if (peekTokenIs(TokenType.RPAREN)) {
            return functionCall;
        }
        functionCall.setArguments(parseCallArguments());
        return functionCall;
    }

    private List<Expression> parseGroupByElements() {
        List<Expression> columns = new ArrayList<>();
        nextToken();
        columns.add(parseExpression(Precedence.LOWEST));
        nextToken();
        while (curTokenIs(TokenType.COMMA)) {
            nextToken();
            columns.add(parseExpression(Precedence.LOWEST));
            nextToken();
        }
        return columns;
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

    private Expression parseColumnName() {
        ColumnReference aCol = new ColumnReference(curToken);
        aCol.setColumnName(parseIdentifier());
        return aCol;
    }

    private Expression parseColumnWithTableReference(Expression left) {
        ColumnReference col = new ColumnReference(curToken);
        if (!(left instanceof ColumnReference)) return null;
        ColumnReference tableName = (ColumnReference) left;
        col.setTableName(tableName.getColumnName());
        col.setColumnName(parseIdentifier());
        return col;
    }

    private Clause parseWhereClause() {
        WhereClause whereClause = new WhereClause(curToken);
        nextToken();
        whereClause.setWhere(parseExpression(Precedence.LOWEST));
        return whereClause;
    }

    private Clause parseGroupByClause() {
        GroupByClause groupByClause = new GroupByClause(curToken);
        nextToken();
        groupByClause.setGroupByItems(parseGroupByElements());
        return groupByClause;
    }

    private Clause parseHavingClause() {
        HavingClause havingClause = new HavingClause(curToken);
        nextToken();
        havingClause.setHave(parseExpression(Precedence.LOWEST));
        return havingClause;
    }

    private Clause parseOrderByClause() {
        OrderByClause orderByClause = new OrderByClause(curToken);
        nextToken();
        orderByClause.setOrderByItems(parseOrderByElements());
        return orderByClause;

    }

    private List<OrderByItem> parseOrderByElements() {
        List<OrderByItem> orderByItems = new ArrayList<>();
        nextToken();
        orderByItems.add(parseOrderByItem());
        nextToken();
        while (curTokenIs(TokenType.COMMA)) {
            nextToken();
            orderByItems.add(parseOrderByItem());
            nextToken();
        }
        return orderByItems;
    }

    private OrderByItem parseOrderByItem() {
        OrderByItem orderByItem = new OrderByItem(curToken);
        orderByItem.setOrderByExpression(parseExpression(Precedence.LOWEST));
        if (peekTokenIs(TokenType.DESC) || peekTokenIs(TokenType.ASC)) {
            nextToken();
            orderByItem.setAsc(curToken.getType().equals(TokenType.ASC));
        }
        return orderByItem;
    }

    private Clause parseLimitClause() {
        LimitClause limitClause = new LimitClause(curToken);
        nextToken();
        limitClause.setValue(parseExpression(Precedence.LOWEST));
        return limitClause;
    }

    private Clause parseOffsetClause() {
        OffsetClause offsetClause = new OffsetClause(curToken);
        nextToken();
        offsetClause.setValue(parseExpression(Precedence.LOWEST));
        return offsetClause;
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

    private boolean isClauseStart(Token token) {
        return clauseStart.contains(token.getType());
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

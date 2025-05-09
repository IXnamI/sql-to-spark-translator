package com.github.xnam.token;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public final class TokenType {
    private static TokenType selfInstance;

    public static final String ILLEGAL = "ILLEGAL";
    public static final String EOF = "EOF";

    // Identifiers + Literals
    public static final String IDENT = "IDENT";
    public static final String INT = "INT";
    public static final String STRING = "STRING";
    public static final String FLOAT = "FLOAT";

    // Operators
    public static final String ASSIGN = "=";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String ASTERISK = "*";
    public static final String SLASH = "/";
    public static final String MODULO = "%";
    public static final String LT = "<";
    public static final String GT = ">";
    public static final String LTE = "<=";
    public static final String GTE = ">=";
    public static final String EQ = "==";
    public static final String NOT_EQ = "!=";

    // Delimiters
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String DOT = ".";
    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final String LBRACKET = "[";
    public static final String RBRACKET = "]";

    // SQL Keywords
    public static final String SELECT = "SELECT";
    public static final String FROM = "FROM";
    public static final String WHERE = "WHERE";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";
    public static final String AS = "AS";
    public static final String JOIN = "JOIN";
    public static final String ON = "ON";
    public static final String INNER = "INNER";
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String FULL = "FULL";
    public static final String OUTER = "OUTER";
    public static final String GROUP = "GROUP";
    public static final String BY = "BY";
    public static final String ORDER = "ORDER";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    public static final String LIMIT = "LIMIT";
    public static final String OFFSET = "OFFSET";
    public static final String INSERT = "INSERT";
    public static final String INTO = "INTO";
    public static final String VALUES = "VALUES";
    public static final String UPDATE = "UPDATE";
    public static final String SET = "SET";
    public static final String DELETE = "DELETE";
    public static final String IS = "IS";
    public static final String NULL = "NULL";
    public static final String IN = "IN";
    public static final String DISTINCT = "DISTINCT";
    public static final String HAVING = "HAVING";
    public static final String CASE = "CASE";
    public static final String WHEN = "WHEN";
    public static final String THEN = "THEN";
    public static final String ELSE = "ELSE";
    public static final String END = "END";
    public static final String OVER = "OVER";
    public static final String PARTITION = "PARTITION";
    public static final String ROWS = "ROWS";
    public static final String RANGE = "RANGE";
    public static final String UNBOUNDED = "UNBOUNDED";
    public static final String PRECEDING = "PRECEDING";
    public static final String FOLLOWING = "FOLLOWING";
    public static final String CURRENT = "CURRENT";
    public static final String ROW = "ROW";

    private Map<String, String> keywordsMapping;

    private TokenType() {
        keywordsMapping = new HashMap<>();
        initKeywordsMapping();
    }

    private static TokenType getInstance() {
        if (selfInstance == null) {
            selfInstance = new TokenType();
        }
        return selfInstance;
    }

    private void initKeywordsMapping() {
        keywordsMapping.put("select", SELECT);
        keywordsMapping.put("from", FROM);
        keywordsMapping.put("where", WHERE);
        keywordsMapping.put("and", AND);
        keywordsMapping.put("or", OR);
        keywordsMapping.put("not", NOT);
        keywordsMapping.put("as", AS);
        keywordsMapping.put("join", JOIN);
        keywordsMapping.put("on", ON);
        keywordsMapping.put("inner", INNER);
        keywordsMapping.put("left", LEFT);
        keywordsMapping.put("right", RIGHT);
        keywordsMapping.put("full", FULL);
        keywordsMapping.put("outer", OUTER);
        keywordsMapping.put("group", GROUP);
        keywordsMapping.put("by", BY);
        keywordsMapping.put("order", ORDER);
        keywordsMapping.put("asc", ASC);
        keywordsMapping.put("desc", DESC);
        keywordsMapping.put("limit", LIMIT);
        keywordsMapping.put("offset", OFFSET);
        keywordsMapping.put("insert", INSERT);
        keywordsMapping.put("into", INTO);
        keywordsMapping.put("values", VALUES);
        keywordsMapping.put("update", UPDATE);
        keywordsMapping.put("set", SET);
        keywordsMapping.put("delete", DELETE);
        keywordsMapping.put("is", IS);
        keywordsMapping.put("null", NULL);
        keywordsMapping.put("in", IN);
        keywordsMapping.put("distinct", DISTINCT);
        keywordsMapping.put("having", HAVING);
        keywordsMapping.put("case", CASE);
        keywordsMapping.put("when", WHEN);
        keywordsMapping.put("then", THEN);
        keywordsMapping.put("else", ELSE);
        keywordsMapping.put("end", END);
        keywordsMapping.put("over", OVER);
        keywordsMapping.put("partition", PARTITION);
        keywordsMapping.put("rows", ROWS);
        keywordsMapping.put("range", RANGE);
        keywordsMapping.put("unbounded", UNBOUNDED);
        keywordsMapping.put("preceding", PRECEDING);
        keywordsMapping.put("following", FOLLOWING);
        keywordsMapping.put("current", CURRENT);
        keywordsMapping.put("row", ROW);
    }

    public static String searchIdent(String ident) {
        return getInstance().keywordsMapping.getOrDefault(ident.toLowerCase(), IDENT);
    }
}


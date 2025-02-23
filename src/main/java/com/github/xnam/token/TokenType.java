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

    // Identifiers + Operators
    public static final String IDENT = "IDENT"; 
    public static final String INT = "INT";    
    public static final String ASSIGN = "=";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String BANG = "!";
    public static final String ASTERISK = "*";
    public static final String SLASH = "/";
    public static final String LT = "<";
    public static final String GT = ">";
    public static final String EQ = "==";
    public static final String NOT_EQ = "!=";

    // Delimiters
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final String LBRACE = "{";
    public static final String RBRACE = "}";

    // Keywords
    public static final String FUNCTION = "FUNCTION";
    public static final String LET = "LET";
    public static final String RETURN = "RETURN";
    public static final String IF = "IF";
    public static final String ELSE = "ELSE";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";

    private Map<String, String> keywordsMapping;
    private TokenType() {
        keywordsMapping = new HashMap<>();
        initKeywordsMapping();
    }

    private static TokenType getInstance() {
        if (selfInstance == null) {
            return new TokenType();
        }
        return selfInstance;
    }

    private void initKeywordsMapping() {
        keywordsMapping.put("fn", FUNCTION);
        keywordsMapping.put("let", LET);
        keywordsMapping.put("true", TRUE);
        keywordsMapping.put("false", FALSE);
        keywordsMapping.put("if", IF);
        keywordsMapping.put("else", ELSE);
        keywordsMapping.put("return", RETURN);
    }

    public static String searchIdent(String ident){
        return getInstance().keywordsMapping.getOrDefault(ident, IDENT);
    }
}

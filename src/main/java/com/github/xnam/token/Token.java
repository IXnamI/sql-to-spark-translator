package com.github.xnam.token;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Token {
    public String type;
    public String literal;

    private Token(String type, String literal) {
        this.type = type;
        this.literal = literal;
    }

    public static Token newToken(String type, String literal) {
        return new Token(type, literal);
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (this == anotherObject) return true;
        if (anotherObject == null || getClass() != anotherObject.getClass()) return false;
        if (!type.equals(((Token) anotherObject).getType())) return false;
        return Objects.equals(literal, ((Token) anotherObject).getLiteral());
    }

    @Override
    public String toString() {
        return "Token {type= " + type + ", literal= " + literal+"}";
    }
}


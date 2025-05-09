package Lexer;


import com.github.xnam.lexer.Lexer;
import com.github.xnam.token.Token;
import com.github.xnam.token.TokenType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LexerTest {
    @Test
    public void testNextToken() {
        String input = "let five = 5;\n" +
                "let ten = 10;\n" +
                "let add = fn(x, y) {\n" +
                "x + y;\n" +
                "};\n" +
                "let result = add(five, ten);\n" +
                "!-/*5;\n" +
                "5 < 10 > 5;\n" +
                "if (5 < 10) {\n" +
                "return true;\n" +
                "} else {\n" +
                "return false;\n" +
                "};\n" +
                "10 == 10;\n" +
                "10 != 9;\n" +
                "let a = \"Hello World\";\n" +
                "[1, 2];";
        Lexer lexer = new Lexer(input);
        List<Token> expectedOutput = new ArrayList<>();
        expectedOutput.add(Token.newToken(TokenType.LET, "let"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "five"));
        expectedOutput.add(Token.newToken(TokenType.ASSIGN, "="));
        expectedOutput.add(Token.newToken(TokenType.INT, "5"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.LET, "let"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "ten"));
        expectedOutput.add(Token.newToken(TokenType.ASSIGN, "="));
        expectedOutput.add(Token.newToken(TokenType.INT, "10"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.LET, "let"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "add"));
        expectedOutput.add(Token.newToken(TokenType.ASSIGN, "="));
        expectedOutput.add(Token.newToken(TokenType.FUNCTION, "fn"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "x"));
        expectedOutput.add(Token.newToken(TokenType.COMMA, ","));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "y"));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.LBRACE, "{"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "x"));
        expectedOutput.add(Token.newToken(TokenType.PLUS, "+"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "y"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));
        expectedOutput.add(Token.newToken(TokenType.RBRACE, "}"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.LET, "let"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "result"));
        expectedOutput.add(Token.newToken(TokenType.ASSIGN, "="));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "add"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "five"));
        expectedOutput.add(Token.newToken(TokenType.COMMA, ","));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "ten"));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.BANG, "!"));
        expectedOutput.add(Token.newToken(TokenType.MINUS, "-"));
        expectedOutput.add(Token.newToken(TokenType.SLASH, "/"));
        expectedOutput.add(Token.newToken(TokenType.ASTERISK, "*"));
        expectedOutput.add(Token.newToken(TokenType.INT, "5"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.INT, "5"));
        expectedOutput.add(Token.newToken(TokenType.LT, "<"));
        expectedOutput.add(Token.newToken(TokenType.INT, "10"));
        expectedOutput.add(Token.newToken(TokenType.GT, ">"));
        expectedOutput.add(Token.newToken(TokenType.INT, "5"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.IF, "if"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.INT, "5"));
        expectedOutput.add(Token.newToken(TokenType.LT, "<"));
        expectedOutput.add(Token.newToken(TokenType.INT, "10"));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.LBRACE, "{"));

        expectedOutput.add(Token.newToken(TokenType.RETURN, "return"));
        expectedOutput.add(Token.newToken(TokenType.TRUE, "true"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.RBRACE, "}"));
        expectedOutput.add(Token.newToken(TokenType.ELSE, "else"));
        expectedOutput.add(Token.newToken(TokenType.LBRACE, "{"));

        expectedOutput.add(Token.newToken(TokenType.RETURN, "return"));
        expectedOutput.add(Token.newToken(TokenType.FALSE, "false"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.RBRACE, "}"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.INT, "10"));
        expectedOutput.add(Token.newToken(TokenType.EQ, "=="));
        expectedOutput.add(Token.newToken(TokenType.INT, "10"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.INT, "10"));
        expectedOutput.add(Token.newToken(TokenType.NOT_EQ, "!="));
        expectedOutput.add(Token.newToken(TokenType.INT, "9"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.LET, "let"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "a"));
        expectedOutput.add(Token.newToken(TokenType.ASSIGN, "="));
        expectedOutput.add(Token.newToken(TokenType.STRING, "Hello World"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.LBRACKET, "["));
        expectedOutput.add(Token.newToken(TokenType.INT, "1"));
        expectedOutput.add(Token.newToken(TokenType.COMMA, ","));
        expectedOutput.add(Token.newToken(TokenType.INT, "2"));
        expectedOutput.add(Token.newToken(TokenType.RBRACKET, "]"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.EOF, ""));

        for (int i = 0; i < expectedOutput.size(); ++i) {
            Token tok = lexer.nextToken();
            Token expectedTok = expectedOutput.get(i);
            assertEquals("Test " + i + " failed", expectedTok, tok);
        }
    }
}

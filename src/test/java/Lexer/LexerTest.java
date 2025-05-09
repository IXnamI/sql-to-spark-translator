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
        String input = "SELECT name, age FROM users;\n" +
                "SELECT customer_id, COUNT(*) AS order_count FROM orders GROUP BY customer_id HAVING COUNT(*) >= 5.25;\n" +
                "SELECT RANK() OVER (PARTITION BY region ORDER BY total DESC) AS rnk\n" +
                "SELECT DISTINCT customer_id, COUNT(*) AS order_count, RANK() OVER (PARTITION BY region ORDER BY total DESC) AS rnk FROM orders LEFT JOIN users ON orders.user_id = users.id WHERE status != 'cancelled' AND amount >= 100 GROUP BY customer_id HAVING COUNT(*) > 5 ORDER BY order_count DESC LIMIT 10 OFFSET 5;";

        Lexer lexer = new Lexer(input);
        List<Token> expectedOutput = new ArrayList<>();

        expectedOutput.add(Token.newToken(TokenType.SELECT, "select"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "name"));
        expectedOutput.add(Token.newToken(TokenType.COMMA, ","));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "age"));
        expectedOutput.add(Token.newToken(TokenType.FROM, "from"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "users"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.SELECT, "select"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "customer_id"));
        expectedOutput.add(Token.newToken(TokenType.COMMA, ","));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "count"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.ASTERISK, "*"));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.AS, "as"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "order_count"));
        expectedOutput.add(Token.newToken(TokenType.FROM, "from"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "orders"));
        expectedOutput.add(Token.newToken(TokenType.GROUP, "group"));
        expectedOutput.add(Token.newToken(TokenType.BY, "by"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "customer_id"));
        expectedOutput.add(Token.newToken(TokenType.HAVING, "having"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "count"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.ASTERISK, "*"));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.GTE, ">="));
        expectedOutput.add(Token.newToken(TokenType.FLOAT, "5.25"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.SELECT, "select"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "rank"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.OVER, "over"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.PARTITION, "partition"));
        expectedOutput.add(Token.newToken(TokenType.BY, "by"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "region"));
        expectedOutput.add(Token.newToken(TokenType.ORDER, "order"));
        expectedOutput.add(Token.newToken(TokenType.BY, "by"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "total"));
        expectedOutput.add(Token.newToken(TokenType.DESC, "desc"));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.AS, "as"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "rnk"));

        expectedOutput.add(Token.newToken(TokenType.SELECT, "select"));
        expectedOutput.add(Token.newToken(TokenType.DISTINCT, "distinct"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "customer_id"));
        expectedOutput.add(Token.newToken(TokenType.COMMA, ","));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "count"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.ASTERISK, "*"));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.AS, "as"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "order_count"));
        expectedOutput.add(Token.newToken(TokenType.COMMA, ","));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "rank"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.OVER, "over"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.PARTITION, "partition"));
        expectedOutput.add(Token.newToken(TokenType.BY, "by"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "region"));
        expectedOutput.add(Token.newToken(TokenType.ORDER, "order"));
        expectedOutput.add(Token.newToken(TokenType.BY, "by"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "total"));
        expectedOutput.add(Token.newToken(TokenType.DESC, "desc"));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.AS, "as"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "rnk"));
        expectedOutput.add(Token.newToken(TokenType.FROM, "from"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "orders"));
        expectedOutput.add(Token.newToken(TokenType.LEFT, "left"));
        expectedOutput.add(Token.newToken(TokenType.JOIN, "join"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "users"));
        expectedOutput.add(Token.newToken(TokenType.ON, "on"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "orders"));
        expectedOutput.add(Token.newToken(TokenType.DOT, "."));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "user_id"));
        expectedOutput.add(Token.newToken(TokenType.ASSIGN, "="));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "users"));
        expectedOutput.add(Token.newToken(TokenType.DOT, "."));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "id"));
        expectedOutput.add(Token.newToken(TokenType.WHERE, "where"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "status"));
        expectedOutput.add(Token.newToken(TokenType.NOT_EQ, "!="));
        expectedOutput.add(Token.newToken(TokenType.STRING, "cancelled"));
        expectedOutput.add(Token.newToken(TokenType.AND, "and"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "amount"));
        expectedOutput.add(Token.newToken(TokenType.GTE, ">="));
        expectedOutput.add(Token.newToken(TokenType.INT, "100"));
        expectedOutput.add(Token.newToken(TokenType.GROUP, "group"));
        expectedOutput.add(Token.newToken(TokenType.BY, "by"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "customer_id"));
        expectedOutput.add(Token.newToken(TokenType.HAVING, "having"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "count"));
        expectedOutput.add(Token.newToken(TokenType.LPAREN, "("));
        expectedOutput.add(Token.newToken(TokenType.ASTERISK, "*"));
        expectedOutput.add(Token.newToken(TokenType.RPAREN, ")"));
        expectedOutput.add(Token.newToken(TokenType.GT, ">"));
        expectedOutput.add(Token.newToken(TokenType.INT, "5"));
        expectedOutput.add(Token.newToken(TokenType.ORDER, "order"));
        expectedOutput.add(Token.newToken(TokenType.BY, "by"));
        expectedOutput.add(Token.newToken(TokenType.IDENT, "order_count"));
        expectedOutput.add(Token.newToken(TokenType.DESC, "desc"));
        expectedOutput.add(Token.newToken(TokenType.LIMIT, "limit"));
        expectedOutput.add(Token.newToken(TokenType.INT, "10"));
        expectedOutput.add(Token.newToken(TokenType.OFFSET, "offset"));
        expectedOutput.add(Token.newToken(TokenType.INT, "5"));
        expectedOutput.add(Token.newToken(TokenType.SEMICOLON, ";"));

        expectedOutput.add(Token.newToken(TokenType.EOF, ""));

        for (int i = 0; i < expectedOutput.size(); ++i) {
            Token tok = lexer.nextToken();
            Token expectedTok = expectedOutput.get(i);
            assertEquals("Test " + i + " failed", expectedTok, tok);
        }
    }
}

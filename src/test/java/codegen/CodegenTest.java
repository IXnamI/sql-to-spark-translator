package codegen;

import com.github.xnam.ast.Program;
import com.github.xnam.codegen.SparkScalaCodeGenerator;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.parser.Parser;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CodegenTest {

    @Test
    public void testWithStatement() {
        String input = "WITH active_users AS (\n" +
                "    SELECT id, name, city\n" +
                "    FROM users\n" +
                "    WHERE active = true\n" +
                "),\n" +
                "user_orders AS (\n" +
                "    SELECT u.id AS user_id, o.id AS order_id, o.amount\n" +
                "    FROM active_users u\n" +
                "    JOIN orders o ON u.id = o.user_id\n" +
                "),\n" +
                "order_summary AS (\n" +
                "    SELECT user_id, COUNT(order_id) AS order_count, SUM(amount) AS total_spent\n" +
                "    FROM user_orders\n" +
                "    GROUP BY user_id\n" +
                "),\n" +
                "top_spenders AS (\n" +
                "    SELECT user_id\n" +
                "    FROM order_summary\n" +
                "    WHERE total_spent > 1000\n" +
                ")\n" +
                "SELECT u.name, s.total_spent\n" +
                "FROM active_users u\n" +
                "JOIN order_summary s ON u.id = s.user_id\n" +
                "WHERE u.id = 1999\n" +
                "ORDER BY s.total_spent DESC\n" +
                "LIMIT 10";
        String generatedCode = generateCode(input);
        System.out.println(generatedCode);
    }

    @Test
    public void testSelectStatement() {
        String input = "SELECT \n" +
                "    u.name, \n" +
                "    COUNT(o.id) OVER (PARTITION BY u.name ORDER BY o.created_at) AS order_count \n" +
                "FROM users u\n" +
                "JOIN orders o ON u.id = o.user_id\n" +
                "WHERE u.active = true\n" +
                "GROUP BY u.name\n" +
                "HAVING COUNT(o.id) > 5\n" +
                "ORDER BY order_count DESC\n" +
                "LIMIT 10";
        String generatedCode = generateCode(input);
        System.out.println(generatedCode);
    }

    private String generateCode(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkDebugStatements(parser);
        checkParserErrors(parser);

        SparkScalaCodeGenerator codegen = new SparkScalaCodeGenerator();
        return codegen.generateCode(program);
    }

    private void checkParserErrors(Parser p) {
        List<String> errors = p.Errors();
        if (errors.isEmpty()) return;
        System.out.println("Parser has " + errors.size() + " errors");
        for (String err : errors) {
            System.out.println("Parser error: " + err);
        }
        fail();
    }

    private void checkDebugStatements(Parser p) {
        List<String> debug = p.Debug();
        if (debug.isEmpty()) return;
        System.out.println("Parser has " + debug.size() + " debug statements");
        for (String d : debug) {
            System.out.println("Debug(): " + d);
        }
    }
}

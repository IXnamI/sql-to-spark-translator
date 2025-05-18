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
        String input = "SELECT u.name, COUNT(o.id) AS order_count " +
                "FROM users u " +
                "JOIN orders o ON u.id = o.user_id " +
                "WHERE u.active = true " +
                "GROUP BY u.name " +
                "HAVING COUNT(o.id) > 5 " +
                "ORDER BY order_count DESC " +
                "LIMIT 10 ";
        String generatedCode = generateCode(input);
        System.out.println(generatedCode);
    }
    @Test
    public void testCaseExpressionGeneration() {
        String input = "CASE WHEN x > 5 THEN 'A' WHEN y = 1 THEN 'B' ELSE 'C' END";
        String generatedCode = generateCode(input);
        String expectedCode = ".when(col(\"x\") > 5, \"A\").when(col(\"y\") === 1, \"B\").otherwise(\"C\")";

        assertEquals(expectedCode, generatedCode);
    }

    @Test
    public void testOrderByClauseGeneration() {
        String input = "ORDER BY salary DESC, id ASC";
        String generatedCode = generateCode(input);
        String expectedCode = ".orderBy(col(\"salary\").desc, col(\"id\").asc)";

        assertEquals(expectedCode, generatedCode);
    }

    @Test
    public void testGroupByClauseGeneration() {
        String input = "GROUP BY department, region";
        String generatedCode = generateCode(input);
        String expectedCode = ".groupBy(col(\"department\"), col(\"region\"))";

        assertEquals(expectedCode, generatedCode);
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

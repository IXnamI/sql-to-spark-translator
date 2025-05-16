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
    public void testCaseExpressionGeneration() {
        String input = "CASE WHEN x > 5 THEN 'A' WHEN y = 1 THEN 'B' ELSE 'C' END";
        String generatedCode = generateCode(input);
        String expectedCode = ".when(col(\"x\") > 5, \"A\").when(col(\"y\") === 1, \"B\").otherwise(\"C\")";

        assertEquals(expectedCode, generatedCode);
    }

    private String generateCode(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkDebugStatements(parser);
        checkParserErrors(parser);

        SparkScalaCodeGenerator codegen = new SparkScalaCodeGenerator();
        return program.accept(codegen);
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

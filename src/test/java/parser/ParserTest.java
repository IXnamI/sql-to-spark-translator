package parser;

import com.github.xnam.ast.*;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.parser.Parser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ParserTest {
    @Test
    public void testLetStatements() {
        String input = "let x = 5;\n" +
                "let y = 10;\n" +
                "let foobar = 838383;";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);
        assert(program != null);
        assertEquals(program.statements.size(), 3);

        List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add("x");
        expectedOutput.add("y");
        expectedOutput.add("foobar");

        for (int i = 0; i < expectedOutput.size(); ++i) {
            testLetStatement(program.statements.get(i), expectedOutput.get(i));
        }
    }

    @Test
    public void testReturnStatements() {
        String input = "return 5;\n" +
                "return 10;\n" +
                "return 838383;";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);
        assert(program != null);
        assertEquals(program.statements.size(), 3);

        List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add("5");
        expectedOutput.add("10");
        expectedOutput.add("838383");

        for (int i = 0; i < expectedOutput.size(); ++i) {
            testReturnStatement(program.statements.get(i), expectedOutput.get(i));
        }
    }

    @Test
    public void testIdentifierExpression() {
        String input = "foobar;";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        assert(program != null);
        assertEquals(program.getStatements().size(), 1);
        assert(program.getStatements().get(0).getClass() == ExpressionStatement.class);
        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        assert(stmt.getExpression().getClass() == Identifier.class);
        Identifier ident = (Identifier) stmt.getExpression();
        assertEquals(ident.getValue(), "foobar");
        assertEquals(ident.tokenLiteral(), "foobar");
    }

    @Test
    public void testIntegerLiteralExpression() {
        String input = "5;";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);

        assert(program != null);
        assertEquals(program.getStatements().size(), 1);
        assert(program.getStatements().get(0).getClass() == ExpressionStatement.class);
        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        assert(stmt.getExpression().getClass() == IntegerLiteral.class);
        IntegerLiteral literal =  (IntegerLiteral) stmt.getExpression();
        assertEquals(literal.getValue(), (Integer) 5);
        assertEquals(literal.tokenLiteral(), "5");
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

    private void testLetStatement(Statement s, String name) {
        assertEquals("let", s.tokenLiteral(), "TokenLiteral not 'let'");
        assertEquals(LetStatement.class, s.getClass(), "Statement not of type LetStatement");
        assertEquals(name, ((LetStatement) s).getName().getValue(), "LetStatement.Name.Value is not as expected");
        assertEquals(name, ((LetStatement) s).getName().tokenLiteral(), "LetStatement.Name.TokenLiteral is not as expected");
    }

    private void testReturnStatement(Statement s, String returnValue) {
        assertEquals("return", s.tokenLiteral(), "TokenLiteral not 'return'");
        assertEquals(ReturnStatement.class, s.getClass(), "Statement not of type ReturnStatement");
    }
}

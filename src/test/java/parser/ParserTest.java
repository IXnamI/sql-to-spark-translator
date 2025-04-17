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

    @Test
    public void testParsingPrefixExpressions() {
        PrefixTest[] prefixTests =  new PrefixTest[2];
        prefixTests[0] = new PrefixTest("!5;", "!", 5);
        prefixTests[1] = new PrefixTest("-15;", "-", 15);

        for (PrefixTest currentTest : prefixTests) {
            Lexer lexer = new Lexer(currentTest.input);
            Parser parser = new Parser(lexer);

            Program program = parser.parseProgram();
            checkParserErrors(parser);
            assert (program != null);
            assertEquals(program.getStatements().size(), 1);
            assert (program.getStatements().get(0).getClass() == ExpressionStatement.class);
            ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
            assert (stmt.getExpression().getClass() == PrefixExpression.class);
            PrefixExpression prefExpr = (PrefixExpression) stmt.getExpression();
            assertEquals(currentTest.operator, prefExpr.getOperator());
            testIntegerLiteral(currentTest.integerValue, prefExpr.getRightExpression());
        }
    }

    @Test
    public void testParsingInfixExpressions() {
        InfixTest[] infixTests = new InfixTest[8];
        infixTests[0] = new InfixTest("5+5;", 5, "+", 5);
        infixTests[1] = new InfixTest("5-5;", 5, "-", 5);
        infixTests[2] = new InfixTest("5*5;", 5, "*", 5);
        infixTests[3] = new InfixTest("5/5;", 5, "/", 5);
        infixTests[4] = new InfixTest("5>5;", 5, ">", 5);
        infixTests[5] = new InfixTest("5<5;", 5, "<", 5);
        infixTests[6] = new InfixTest("5==5;", 5, "==",5);
        infixTests[7] = new InfixTest("5!=5;", 5, "!=", 5);

        for (InfixTest currentTest : infixTests) {
            Lexer lexer = new Lexer(currentTest.input);
            Parser parser = new Parser(lexer);

            Program program = parser.parseProgram();
            checkParserErrors(parser);
            assert (program != null);
            assertEquals(program.getStatements().size(), 1);
            assert (program.getStatements().get(0).getClass() == ExpressionStatement.class);
            ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
            assert (stmt.getExpression().getClass() == InfixExpression.class);
            InfixExpression infixExpr = (InfixExpression) stmt.getExpression();
            testIntegerLiteral(currentTest.leftValue, infixExpr.getLeftExpression());
            assertEquals (currentTest.operator, infixExpr.getOperator());
            testIntegerLiteral(currentTest.rightValue, infixExpr.getRightExpression());
        }
    }

    @Test
    public void testOperatorPrecedenceParsing() {
        String[][] tests = {
                {"-a * b", "((-a) * b)"},
                {"!-a", "(!(-a))"},
                {"a + b + c", "((a + b) + c)"},
                {"a + b - c", "((a + b) - c)"},
                {"a * b * c", "((a * b) * c)"},
                {"a * b / c", "((a * b) / c)"},
                {"a + b / c", "(a + (b / c))"},
                {"a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)"},
                {"3 + 4; -5 * 5", "(3 + 4)((-5) * 5)"},
                {"5 > 4 == 3 < 4", "((5 > 4) == (3 < 4))"},
                {"5 < 4 != 3 > 4", "((5 < 4) != (3 > 4))"},
                {"3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"},
        };

        for (String[] test : tests) {
            String input = test[0];
            String expected = test[1];

            Lexer lexer = new Lexer(input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            assert (program != null);
            String actual = program.toString();
            assertEquals (expected, actual);
        }
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

    private void testIntegerLiteral(Integer expectedIntValue, Expression givenExpr) {
        assert(givenExpr.getClass() == IntegerLiteral.class);
        IntegerLiteral literal = (IntegerLiteral) givenExpr;
        assertEquals(literal.getValue(), expectedIntValue);
        assertEquals(literal.tokenLiteral(), String.valueOf(expectedIntValue));
    }
}

class PrefixTest {
    final String input;
    final String operator;
    final Integer integerValue;
    PrefixTest(String input, String operator, Integer integerValue) {
        this.input = input;
        this.operator = operator;
        this.integerValue = integerValue;
    }

    @Override
    public String toString() {
        return  operator + " " + integerValue;
    }
}


class InfixTest {
    final String input;
    final Integer leftValue;
    final String operator;
    final Integer rightValue;

    InfixTest (String input, Integer leftValue, String operator, Integer rightValue) {
        this.input = input;
        this.operator = operator;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    @Override
    public String toString() {
        return leftValue + " " + operator + " " + rightValue;
    }
}

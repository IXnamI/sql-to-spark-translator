package parser;

import com.github.xnam.ast.*;
import com.github.xnam.ast.Boolean;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.parser.Parser;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
        PrefixTest[] prefixTests =  new PrefixTest[4];
        prefixTests[0] = new PrefixTest("!5;", "!", 5);
        prefixTests[1] = new PrefixTest("-15;", "-", 15);
        prefixTests[2] = new PrefixTest("!true", "!", true);
        prefixTests[3] = new PrefixTest("!false", "!", false);

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
            testLiteralExpression(prefExpr.getRightExpression(), currentTest.value);
        }
    }

    @Test
    public void testParsingInfixExpressions() {
        InfixTest[] infixTests = new InfixTest[11];
        infixTests[0] = new InfixTest("5+5;", 5, "+", 5);
        infixTests[1] = new InfixTest("5-5;", 5, "-", 5);
        infixTests[2] = new InfixTest("5*5;", 5, "*", 5);
        infixTests[3] = new InfixTest("5/5;", 5, "/", 5);
        infixTests[4] = new InfixTest("5>5;", 5, ">", 5);
        infixTests[5] = new InfixTest("5<5;", 5, "<", 5);
        infixTests[6] = new InfixTest("5==5;", 5, "==",5);
        infixTests[7] = new InfixTest("5!=5;", 5, "!=", 5);
        infixTests[8] = new InfixTest("true == true;", true, "==", true);
        infixTests[9] = new InfixTest("true != false;", true, "!=", false);
        infixTests[10] = new InfixTest("false == false;", false, "==", false);

        for (InfixTest currentTest : infixTests) {
            Lexer lexer = new Lexer(currentTest.input);
            Parser parser = new Parser(lexer);

            Program program = parser.parseProgram();
            checkParserErrors(parser);
            assert (program != null);
            assertEquals(program.getStatements().size(), 1);
            assert (program.getStatements().get(0).getClass() == ExpressionStatement.class);
            ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
            testInfixExpression(stmt.getExpression(), currentTest.leftValue, currentTest.operator, currentTest.rightValue);
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
                {"true", "true"},
                {"false", "false"},
                {"3 > 5 == false", "((3 > 5) == false)"},
                {"3 < 5 == true", "((3 < 5) == true)"},
                {"1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"},
                {"(5 + 5) * 2", "((5 + 5) * 2)"},
                {"2 / (5 + 5)", "(2 / (5 + 5))"},
                {"-(5 + 5)", "(-(5 + 5))"},
                {"!(true == true)", "(!(true == true))"}
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

    @Test
    public void testParsingBoolean() {
        String input = "true;";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);

        assert(program != null);
        assertEquals(program.getStatements().size(), 1);
        assert(program.getStatements().get(0).getClass() == ExpressionStatement.class);
        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        testLiteralExpression(stmt.getExpression(), true);
    }

    @Test
    public void testParsingIfExpression() {
        String input = "if (x < y) { x }";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkDebugStatements(parser);
        checkParserErrors(parser);

        assert(program != null);
        assertEquals(program.getStatements().size(), 1);
        assert (program.getStatements().get(0).getClass() == ExpressionStatement.class);
        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        assert (stmt.getExpression().getClass() == IfExpression.class);
        IfExpression ifExpr = (IfExpression) stmt.getExpression();
        testInfixExpression(ifExpr.getCondition(), "x", "<", "y");
        assert (ifExpr.getConsequence().getStatements().size() == 1);
        assert (ifExpr.getConsequence().getStatements().get(0).getClass() == ExpressionStatement.class);
        ExpressionStatement consequence = (ExpressionStatement) ifExpr.getConsequence().getStatements().get(0);
        testIdentifier("x", consequence.getExpression());
        if (ifExpr.getAlternative() != null) fail("Input does not have an alternative, but got alternative");
    }

    @Test
    public void testParsingIfElseExpression() {
        String input = "if (x < y) { x } else { y }";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);

        assert(program != null);
        assertEquals(program.getStatements().size(), 1);
        assert (program.getStatements().get(0).getClass() == ExpressionStatement.class);
        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        assert (stmt.getExpression().getClass() == IfExpression.class);
        IfExpression ifExpr = (IfExpression) stmt.getExpression();
        testInfixExpression(ifExpr.getCondition(), "x", "<", "y");
        assert (ifExpr.getConsequence().getStatements().size() == 1);
        assert (ifExpr.getConsequence().getStatements().get(0).getClass() == ExpressionStatement.class);
        ExpressionStatement consequence = (ExpressionStatement) ifExpr.getConsequence().getStatements().get(0);
        testIdentifier("x", consequence.getExpression());
        assert (ifExpr.getAlternative() != null);
        assert (ifExpr.getAlternative().getStatements().size() == 1);
        assert (ifExpr.getAlternative().getStatements().get(0).getClass() == ExpressionStatement.class);
        ExpressionStatement alternative = (ExpressionStatement) ifExpr.getAlternative().getStatements().get(0);
        testIdentifier("y", alternative.getExpression());
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

    private void testIdentifier(String expected, Expression expr) {
        assert(expr.getClass() == Identifier.class);
        Identifier ident = (Identifier) expr;
        assertEquals(expected, ident.getValue());
        assertEquals(expected, ident.tokenLiteral());
    }

    private void testBooleanLiteral(java.lang.Boolean expected, Expression expr) {
        assert(expr.getClass() == Boolean.class);
        Boolean bool = (Boolean) expr;
        assertEquals(expected, bool.getValue());
        assertEquals(expected.toString(), bool.tokenLiteral());
    }

    private void testLiteralExpression(Expression expr, Object expected) {
        if (expected instanceof Integer) {
            testIntegerLiteral((Integer) expected, expr);
        } else if (expected instanceof String) {
            testIdentifier((String) expected, expr);
        } else if (expected instanceof java.lang.Boolean) {
            testBooleanLiteral((java.lang.Boolean) expected, expr);
        } else {
            fail("Expression type is not handled");
        }
    }

    private void testInfixExpression(Expression expr, Object left, String operator, Object right) {
        assert(expr.getClass() == InfixExpression.class);
        InfixExpression infix = (InfixExpression) expr;
        testLiteralExpression(infix.getLeftExpression(), left);
        assertEquals(operator, infix.getOperator());
        testLiteralExpression(infix.getRightExpression(), right);
    }
}

class PrefixTest {
    final String input;
    final String operator;
    final Object value;
    PrefixTest(String input, String operator, Object value) {
        this.input = input;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String toString() {
        return  operator + " " + value;
    }
}


class InfixTest {
    final String input;
    final Object leftValue;
    final String operator;
    final Object rightValue;

    InfixTest (String input, Object leftValue, String operator, Object rightValue) {
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

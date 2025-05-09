package evaluator;

import com.github.xnam.ast.Program;
import com.github.xnam.evaluator.Evaluator;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.object.Array;
import com.github.xnam.object.Environment;
import com.github.xnam.object.Function;
import com.github.xnam.object.ObjectType;
import com.github.xnam.parser.Parser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvaluatorTest {
/*
    @Test
    public void testEvalIntegerExpression() {
        List<LiteralExpressionTest> tests = new ArrayList<>();
        tests.add(new LiteralExpressionTest("5", 5));
        tests.add(new LiteralExpressionTest("10", 10));
        tests.add(new LiteralExpressionTest("-5", -5));
        tests.add(new LiteralExpressionTest("-10", -10));
        tests.add(new LiteralExpressionTest("5 + 5 + 5 + 5 - 10", 10));
        tests.add(new LiteralExpressionTest("2 * 2 * 2 * 2 * 2", 32));
        tests.add(new LiteralExpressionTest("-50 + 100 + -50", 0));
        tests.add(new LiteralExpressionTest("5 * 2 + 10", 20));
        tests.add(new LiteralExpressionTest("5 + 2 * 10", 25));
        tests.add(new LiteralExpressionTest("20 + 2 * -10", 0));
        tests.add(new LiteralExpressionTest("50 / 2 * 2 + 10", 60));
        tests.add(new LiteralExpressionTest("2 * (5 + 10)", 30));
        tests.add(new LiteralExpressionTest("3 * 3 * 3 + 10", 37));
        tests.add(new LiteralExpressionTest("3 * (3 * 3) + 10", 37));
        tests.add(new LiteralExpressionTest("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50));

        for (LiteralExpressionTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            checkDebugStatements();
            testIntegerObject(evaluated, currentTest.expected);
        }
    }

    @Test
    public void testEvalBooleanExpression() {
        List<LiteralExpressionTest> tests = new ArrayList<>();
        tests.add(new LiteralExpressionTest("true", true));
        tests.add(new LiteralExpressionTest("false", false));
        tests.add(new LiteralExpressionTest("1 < 2", true));
        tests.add(new LiteralExpressionTest("1 > 2", false));
        tests.add(new LiteralExpressionTest("1 < 1", false));
        tests.add(new LiteralExpressionTest("1 > 1", false));
        tests.add(new LiteralExpressionTest("1 == 1", true));
        tests.add(new LiteralExpressionTest("1 != 1", false));
        tests.add(new LiteralExpressionTest("1 == 2", false));
        tests.add(new LiteralExpressionTest("1 != 2", true));
        tests.add(new LiteralExpressionTest("true == true", true));
        tests.add(new LiteralExpressionTest("false == false", true));
        tests.add(new LiteralExpressionTest("true == false", false));
        tests.add(new LiteralExpressionTest("true != false", true));
        tests.add(new LiteralExpressionTest("false != true", true));
        for (LiteralExpressionTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            checkDebugStatements();
            testBooleanObject(evaluated, currentTest.expected);
        }
    }

    @Test
    public void testBangOperator() {
        List<LiteralExpressionTest> tests = new ArrayList<>();
        tests.add(new LiteralExpressionTest("!true", false));
        tests.add(new LiteralExpressionTest("!false", true));
        tests.add(new LiteralExpressionTest("!5", false));
        tests.add(new LiteralExpressionTest("!!true", true));
        tests.add(new LiteralExpressionTest("!!false", false));
        tests.add(new LiteralExpressionTest("!!5", true));
        for (LiteralExpressionTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            checkDebugStatements();
            testBooleanObject(evaluated, currentTest.expected);
        }
    }

    @Test
    public void testIfElseExpressions() {
        List<LiteralExpressionTest> tests = new ArrayList<>();
        tests.add(new LiteralExpressionTest("if (true) { 10 }", 10));
        tests.add(new LiteralExpressionTest("if (false) { 10 }", null));
        tests.add(new LiteralExpressionTest("if (1) { 10 }", 10));
        tests.add(new LiteralExpressionTest("if (1 < 2) { 10 }", 10));
        tests.add(new LiteralExpressionTest("if (1 > 2) { 10 }", null));
        tests.add(new LiteralExpressionTest("if (1 > 2) { 10 } else { 20 }", 20));
        tests.add(new LiteralExpressionTest("if (1 < 2) { 10 } else { 20 }", 10));

        for (LiteralExpressionTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            if (currentTest.expected instanceof Integer) {
                testIntegerObject(evaluated, currentTest.expected);
            } else {
                testNullObject(evaluated);
            }
        }
    }

    @Test
    public void testReturnStatements() {
        List<LiteralExpressionTest> tests = new ArrayList<>();
        tests.add(new LiteralExpressionTest("return 10;", 10));
        tests.add(new LiteralExpressionTest("return 10; 9;", 10));
        tests.add(new LiteralExpressionTest("return 2 * 5; 9;", 10));
        tests.add(new LiteralExpressionTest("9; return 2 * 5; 9;", 10));
        tests.add(new LiteralExpressionTest(
                "if (10 > 1) {\n" +
                        "  if (10 > 1) {\n" +
                        "    return 10;\n" +
                        "  }\n" +
                        "  return 1;\n" +
                        "}",
                10
        ));
        for (LiteralExpressionTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            checkDebugStatements();
            testIntegerObject(evaluated, currentTest.expected);
        }
    }

    @Test
    public void testErrorHandling() {
        List<ErrorHandlingTest> tests = new ArrayList<>();
        tests.add(new ErrorHandlingTest("5 + true;", "type mismatch: INTEGER + BOOLEAN"));
        tests.add(new ErrorHandlingTest("5 + true; 5;", "type mismatch: INTEGER + BOOLEAN"));
        tests.add(new ErrorHandlingTest("-true", "unknown operator: -BOOLEAN"));
        tests.add(new ErrorHandlingTest("true + false;", "unknown operator: BOOLEAN + BOOLEAN"));
        tests.add(new ErrorHandlingTest("5; true + false; 5", "unknown operator: BOOLEAN + BOOLEAN"));
        tests.add(new ErrorHandlingTest("if (10 > 1) { true + false; }", "unknown operator: BOOLEAN + BOOLEAN"));
        tests.add(new ErrorHandlingTest(
                "if (10 > 1) {\n" +
                        "  if (10 > 1) {\n" +
                        "    return true + false;\n" +
                        "  }\n" +
                        "  return 1;\n" +
                        "}",
                "unknown operator: BOOLEAN + BOOLEAN"
        ));
        tests.add(new ErrorHandlingTest("foobar", "identifier not found: foobar"));
        for (ErrorHandlingTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            assert evaluated.getType().equals(ObjectType.ERROR_OBJ);
            com.github.xnam.object.Error err = (com.github.xnam.object.Error) evaluated;
            assertEquals(currentTest.expectedErrorMessage, err.getMessage());
        }
    }

    @Test
    public void testLetStatements() {
        List<LiteralExpressionTest> tests = new ArrayList<>();
        tests.add(new LiteralExpressionTest("let a = 5; a;", 5));
        tests.add(new LiteralExpressionTest("let a = 5 * 5; a;", 25));
        tests.add(new LiteralExpressionTest("let a = 5; let b = a; b;", 5));
        tests.add(new LiteralExpressionTest("let a = 5; let b = a; let c = a + b + 5; c;", 15));
        for (LiteralExpressionTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            checkDebugStatements();
            testIntegerObject(evaluated, currentTest.expected);
        }
    }

    @Test
    public void testFunctionObject() {
        String input = "fn(x) { x + 2; };";
        Object evaluated = testEval(input);
        assert evaluated instanceof Function;
        Function func = (Function) evaluated;
        assertEquals(1, func.getParams().size());
        assertEquals("x", func.getParams().get(0).toString());

        String expectedBody = "(x + 2)";
        assertEquals(expectedBody, func.getBody().toString());
    }

    @Test
    public void testFunctionApplication() {
        List<LiteralExpressionTest> tests = new ArrayList<>();
        tests.add(new LiteralExpressionTest("let identity = fn(x) { x; }; identity(5);", 5));
        tests.add(new LiteralExpressionTest("let identity = fn(x) { return x; }; identity(5);", 5));
        tests.add(new LiteralExpressionTest("let double = fn(x) { x * 2; }; double(5);", 10));
        tests.add(new LiteralExpressionTest("let add = fn(x, y) { x + y; }; add(5, 5);", 10));
        tests.add(new LiteralExpressionTest("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20));
        for (LiteralExpressionTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            checkDebugStatements();
            testIntegerObject(evaluated, currentTest.expected);
        }
    }

    @Test
    public void testClosures() {
        String input = "let newAdder = fn(x) {\n" +
                            "fn(y) { x + y };\n" +
                        "};\n" +
                        "let addTwo = newAdder(2);\n" +
                        "addTwo(2);";
        testIntegerObject(testEval(input), 4);
    }

    @Test
    public void testStringLiteral() {
        String input = "\"Hello World\"";

        Object evaluated = testEval(input);
        assert evaluated instanceof com.github.xnam.object.String;
        com.github.xnam.object.String string = (com.github.xnam.object.String) evaluated;
        assertEquals("Hello World", string.getValue());
    }

    @Test
    public void testBuiltInFunctions() {
        List<LiteralExpressionTest> tests = new ArrayList<>();
        tests.add(new LiteralExpressionTest("len(\"\")", 0));
        tests.add(new LiteralExpressionTest("len(\"four\")", 4));
        tests.add(new LiteralExpressionTest("len(\"hello world\")", 11));
        tests.add(new LiteralExpressionTest("len(1)", "Type INTEGER is not supported by len()"));
        tests.add(new LiteralExpressionTest("len(\"one\", \"two\")", "Inappropriate amount of arguments, expected = 1, got = 2"));
        for (LiteralExpressionTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            checkDebugStatements();
            if (currentTest.expected instanceof Integer) testIntegerObject(evaluated, currentTest.expected);
            else if (currentTest.expected instanceof String) {
                assert evaluated instanceof com.github.xnam.object.Error;
                com.github.xnam.object.Error errorObj = (com.github.xnam.object.Error) evaluated;
                assertEquals(currentTest.expected, errorObj.getMessage());
            }
        }
    }

    @Test
    public void testArrayLiterals() {
        String input = "[1, 2 * 2, 3 + 3]";

        Object evaluated = testEval(input);
        assert evaluated instanceof Array;
        Array array = (Array) evaluated;
        assertEquals(3, array.getElements().size());
        testIntegerObject(array.getElements().get(0), 1);
        testIntegerObject(array.getElements().get(1), 4);
        testIntegerObject(array.getElements().get(2), 6);
    }

    @Test
    public void testArrayIndexExpressions() {
        List<LiteralExpressionTest> tests = new ArrayList<>();
        tests.add(new LiteralExpressionTest("[1, 2, 3][0]", 1));
        tests.add(new LiteralExpressionTest("[1, 2, 3][1]", 2));
        tests.add(new LiteralExpressionTest("[1, 2, 3][2]", 3));
        tests.add(new LiteralExpressionTest("let i = 0; [1][i];", 1));
        tests.add(new LiteralExpressionTest("[1, 2, 3][1 + 1];", 3));
        tests.add(new LiteralExpressionTest("let myArray = [1, 2, 3]; myArray[2];", 3));
        tests.add(new LiteralExpressionTest("let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];", 6));
        tests.add(new LiteralExpressionTest("let myArray = [1, 2, 3]; let i = myArray[0]; myArray[i];", 2));
        tests.add(new LiteralExpressionTest("[1, 2, 3][3]", null));
        tests.add(new LiteralExpressionTest("[1, 2, 3][-1]", null));
        for (LiteralExpressionTest currentTest : tests) {
            com.github.xnam.object.Object evaluated = testEval(currentTest.input);
            checkDebugStatements();
            if (currentTest.expected instanceof Integer) testIntegerObject(evaluated, currentTest.expected);
            else testNullObject(evaluated);
        }
    }
*/

    private com.github.xnam.object.Object testEval(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Environment env = new Environment();
        Evaluator.clearDebug();
        return Evaluator.eval(program, env);
    }

    private void testIntegerObject(com.github.xnam.object.Object evaluatedObject, Object expectedValue) {
        assert (evaluatedObject.getClass() == com.github.xnam.object.Integer.class);
        com.github.xnam.object.Integer result = (com.github.xnam.object.Integer) evaluatedObject;
        assertEquals(expectedValue, result.getValue());
    }

    private void testBooleanObject(com.github.xnam.object.Object evaluatedObject, Object expectedValue) {
        assert (evaluatedObject.getClass() == com.github.xnam.object.Boolean.class);
        com.github.xnam.object.Boolean result = (com.github.xnam.object.Boolean) evaluatedObject;
        assertEquals(expectedValue, result.getValue());
    }

    private void testNullObject(com.github.xnam.object.Object evaluatedObject) {
        assert evaluatedObject == Evaluator.NULL;
    }

    private void checkDebugStatements() {
        List<String> debug = Evaluator.debug;
        if (debug.isEmpty()) return;
        System.out.println("Evaluator has " + debug.size() + " debug statements");
        for (String d : debug) {
            System.out.println("Debug(): " + d);
        }
    }
}

class LiteralExpressionTest {
    String input;
    Object expected;

    public LiteralExpressionTest(String input, Object expected) {
        this.input = input;
        this.expected = expected;
    }
}

class ErrorHandlingTest {
    String input;
    String expectedErrorMessage;

    public ErrorHandlingTest(String input, String expectedErrorMessage) {
        this.input = input;
        this.expectedErrorMessage = expectedErrorMessage;
    }
}
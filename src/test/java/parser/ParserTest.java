package parser;

import com.github.xnam.ast.*;
import com.github.xnam.ast.Boolean;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.parser.Parser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ParserTest {

    @Test
    public void testWhereClause() {
        String input = "WHERE age > 30";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);
        checkDebugStatements(parser);

        assert (program != null);
        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assert (stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assert (exprStmt.getParsedNode() instanceof WhereClause);
        WhereClause whereClause = (WhereClause) exprStmt.getParsedNode();
        assert (whereClause.getWhere() instanceof InfixExpression);
        InfixExpression where = (InfixExpression) whereClause.getWhere();
        testInfixExpression(where, "age", ">", 30);
    }

    @Test
    public void testHavingClause() {
        String input = "HAVING COUNT(*) > 5";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);
        checkDebugStatements(parser);

        assert (program != null);
        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assert (stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assert (exprStmt.getParsedNode() instanceof HavingClause);
        HavingClause havingClause = (HavingClause) exprStmt.getParsedNode();
        assert (havingClause.getHave() instanceof InfixExpression);
        InfixExpression having = (InfixExpression) havingClause.getHave();
        assert (having.getLeftExpression() instanceof FunctionCall);
        assert (having.getRightExpression() instanceof IntegerLiteral);
        FunctionCall funcCall = (FunctionCall) having.getLeftExpression();
        IntegerLiteral integerLiteral = (IntegerLiteral) having.getRightExpression();
        assertEquals(">", having.getOperator());
        testLiteralExpression(integerLiteral, 5);
        testLiteralExpression(funcCall.getFunction(), "COUNT");
        assertEquals(funcCall.getArguments().size(), 1);
        testLiteralExpression(funcCall.getArguments().get(0), "*");
    }

    @Test
    public void testGroupByClause() {
        String input = "GROUP BY department, region";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);
        checkDebugStatements(parser);

        assert (program != null);
        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assert (stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assert (exprStmt.getParsedNode() instanceof GroupByClause);
        GroupByClause groupBy = (GroupByClause) exprStmt.getParsedNode();
        assertEquals(2, groupBy.getGroupByItems().size());

        ColumnReference col1 = (ColumnReference) groupBy.getGroupByItems().get(0);
        ColumnReference col2 = (ColumnReference) groupBy.getGroupByItems().get(1);

        testColumnReference("department", col1);
        testColumnReference("region", col2);
    }

    @Test
    public void testOrderByClause() {
        String input = "ORDER BY created_at DESC, name ASC";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);
        checkDebugStatements(parser);

        ExpressionStatement exprStmt = (ExpressionStatement) program.getStatements().get(0);
        assert(exprStmt.getParsedNode() instanceof OrderByClause);

        OrderByClause orderBy = (OrderByClause) exprStmt.getParsedNode();
        assertEquals(2, orderBy.getOrderByItems().size());

        OrderByItem item1 = orderBy.getOrderByItems().get(0);
        OrderByItem item2 = orderBy.getOrderByItems().get(1);

        assert(item1.getOrderByExpression() instanceof ColumnReference);
        assert(!item1.isAsc());
        testIdentifier("created_at", ((ColumnReference) item1.getOrderByExpression()).getColumnName());

        assert(item2.getOrderByExpression() instanceof ColumnReference);
        assert(item2.isAsc());
        testIdentifier("name", ((ColumnReference) item2.getOrderByExpression()).getColumnName());
    }

    @Test
    public void testLimitClause() {
        String input = "LIMIT 10";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);
        checkDebugStatements(parser);

        ExpressionStatement exprStmt = (ExpressionStatement) program.getStatements().get(0);
        assert(exprStmt.getParsedNode() instanceof LimitClause);

        LimitClause limitClause = (LimitClause) exprStmt.getParsedNode();
        testLiteralExpression(limitClause.getValue(), 10);
    }

    @Test
    public void testOffsetClause() {
        String input = "OFFSET 5";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkParserErrors(parser);
        checkDebugStatements(parser);

        ExpressionStatement exprStmt = (ExpressionStatement) program.getStatements().get(0);
        assert(exprStmt.getParsedNode() instanceof OffsetClause);

        OffsetClause offsetClause = (OffsetClause) exprStmt.getParsedNode();
        testLiteralExpression(offsetClause.getValue(), 5);
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

    private void testIntegerLiteral(Integer expectedIntValue, Expression givenExpr) {
        assert(givenExpr.getClass() == IntegerLiteral.class);
        IntegerLiteral literal = (IntegerLiteral) givenExpr;
        assertEquals(literal.getValue(), expectedIntValue);
        assertEquals(literal.tokenLiteral(), String.valueOf(expectedIntValue));
    }

    private void testColumnReference(String expected, Expression expr) {
        assert(expr instanceof ColumnReference);
        ColumnReference aCol = (ColumnReference) expr;
        assert(aCol.getColumnName() instanceof Identifier);
        testIdentifier(expected, aCol.getColumnName());
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
            testColumnReference((String) expected, expr);
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

class FunctionParamsTest {
    final String input;
    final List<String> expectedParams;

    FunctionParamsTest(String input, List<String> expectedParams) {
        this.input = input;
        this.expectedParams = expectedParams;
    }
}

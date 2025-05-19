package parser;

import com.github.xnam.ast.*;
import com.github.xnam.ast.Boolean;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.parser.Parser;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ParserTest {

    @Test
    public void testWithStatement() {
        String input = "WITH cte1 AS (SELECT id FROM table1), " +
                "cte2 AS (SELECT id FROM cte1) " +
                "SELECT * FROM cte2";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkDebugStatements(parser);
        checkParserErrors(parser);

        System.out.println(program.toString());
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        assert (stmt instanceof WithStatement);
        WithStatement withStmt = (WithStatement) stmt;
        assertEquals("WITH", withStmt.getToken().getLiteral());
        assertEquals(2, withStmt.getCteList().size());

        CommonTableExpression cte1 = withStmt.getCteList().get(0);
        assertEquals("cte1", cte1.getToken().getLiteral());
        assertEquals (0, cte1.getColumnAliases().size());
        assert (cte1.getSubquery() instanceof SelectStatement);

        SelectStatement cte1Select = (SelectStatement) cte1.getSubquery();
        assertEquals(1, cte1Select.getSelectItems().size());
        SelectItem cte1SelectItem = cte1Select.getSelectItems().get(0);
        assert (cte1SelectItem.getSelectExpression() instanceof ColumnReference);
        testColumnReference(null, "id", cte1SelectItem.getSelectExpression());
        assert (cte1Select.getFrom() != null);
        Expression fromTable = (Expression) cte1Select.getFrom().getTable();
        testIdentifier("table1", fromTable);
        assert (cte1Select.getFrom().getAlias() == null);

        CommonTableExpression cte2 = withStmt.getCteList().get(1);
        assertEquals("cte2", cte2.getToken().getLiteral());
        assertEquals (0, cte2.getColumnAliases().size());
        assert (cte2.getSubquery() instanceof SelectStatement);

        SelectStatement cte2Select = (SelectStatement) cte2.getSubquery();
        assertEquals(1, cte2Select.getSelectItems().size());
        SelectItem cte2SelectItem = cte2Select.getSelectItems().get(0);
        assert (cte2SelectItem.getSelectExpression() instanceof ColumnReference);
        testColumnReference(null, "id", cte2SelectItem.getSelectExpression());
        assert (cte2Select.getFrom() != null);
        Expression fromTable2 = (Expression) cte2Select.getFrom().getTable();
        testIdentifier("cte1", fromTable2);
        assert (cte2Select.getFrom().getAlias() == null);

        assert (withStmt.getMainQuery() instanceof SelectStatement);
        SelectStatement mainSelect = (SelectStatement) withStmt.getMainQuery();
        assertEquals(1, mainSelect.getSelectItems().size());
        SelectItem mainSelectItem = mainSelect.getSelectItems().get(0);
        assert (mainSelectItem.getSelectExpression() instanceof ColumnReference);
        testColumnReference(null, "*", mainSelectItem.getSelectExpression());
        // FROM cte_example in main query
        assert (mainSelect.getFrom() != null);
        Expression mainFromTable = (Expression) mainSelect.getFrom().getTable();
        testIdentifier("cte2", mainFromTable);
        assert (mainSelect.getFrom().getAlias() == null);
    }

    @Test
    public void testFullSelectStatement() {
        String input = "SELECT u.name, COUNT(o.id) AS order_count " +
                "FROM users u " +
                "JOIN orders o ON u.id = o.user_id " +
                "WHERE u.active = true " +
                "GROUP BY u.name " +
                "HAVING COUNT(o.id) > 5 " +
                "ORDER BY order_count DESC " +
                "LIMIT 10 " +
                "OFFSET 5";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Program program = parser.parseProgram();
        checkDebugStatements(parser);
        checkParserErrors(parser);

        System.out.println(program.toString());
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        assert (stmt instanceof SelectStatement);

        SelectStatement select = (SelectStatement) stmt;

        assertEquals(2, select.getSelectItems().size());
        SelectItem item1 = select.getSelectItems().get(0);
        assert (item1.getSelectExpression() instanceof ColumnReference);
        ColumnReference col1 = (ColumnReference) item1.getSelectExpression();
        testColumnReference("u", "name", col1);
        assert (item1.getAlias() == null);

        SelectItem item2 = select.getSelectItems().get(1);
        assert (item2.getSelectExpression() instanceof FunctionCall);
        FunctionCall countFunc = (FunctionCall) item2.getSelectExpression();
        testIdentifier("COUNT", countFunc.getFunction());
        assertEquals(1, countFunc.getArguments().size());
        ColumnReference argCol = (ColumnReference) countFunc.getArguments().get(0);
        testColumnReference("o", "id", argCol);
        testIdentifier("order_count", item2.getAlias());

        assert (select.getFrom() != null);
        testIdentifier("users", (Expression) select.getFrom().getTable());
        testIdentifier("u", select.getFrom().getAlias());

        assertEquals(1, select.getJoins().size());
        JoinClause join = select.getJoins().get(0);
        assertEquals(JoinType.INNER, join.getJoinType());
        assert (join.getTable() instanceof TableSource);
        TableSource joinTable = (TableSource) join.getTable();
        testIdentifier("orders", (Expression) joinTable.getTable());
        testIdentifier("o", joinTable.getAlias());

        InfixExpression joinCondition = (InfixExpression) join.getOnCondition();
        assertEquals("=", joinCondition.getOperator());
        ColumnReference left = (ColumnReference) joinCondition.getLeftExpression();
        ColumnReference right = (ColumnReference) joinCondition.getRightExpression();
        testColumnReference("u", "id", left);
        testColumnReference("o", "user_id", right);

        WhereClause where = select.getWhere();
        assert (where.getWhere() instanceof InfixExpression);
        InfixExpression whereCond = (InfixExpression) where.getWhere();
        assertEquals("=", whereCond.getOperator());
        ColumnReference whereLeft = (ColumnReference) whereCond.getLeftExpression();
        Boolean whereRight = (Boolean) whereCond.getRightExpression();
        testColumnReference("u", "active", whereLeft);
        assertEquals("true", whereRight.getValue().toString());

        GroupByClause groupBy = select.getGroupBy();
        assertEquals(1, groupBy.getGroupByItems().size());
        ColumnReference groupCol = (ColumnReference) groupBy.getGroupByItems().get(0);
        testColumnReference("u", "name", groupCol);

        HavingClause having = select.getHaving();
        assert (having.getHave() instanceof InfixExpression);
        InfixExpression havingCond = (InfixExpression) having.getHave();
        assertEquals(">", havingCond.getOperator());
        testIdentifier("COUNT", countFunc.getFunction());
        assertEquals(1, countFunc.getArguments().size());
        ColumnReference argCol2 = (ColumnReference) countFunc.getArguments().get(0);
        testColumnReference("o", "id", argCol2);
        IntegerLiteral havingRight = (IntegerLiteral) havingCond.getRightExpression();
        assertEquals("5", havingRight.getValue().toString());

        OrderByClause orderBy = select.getOrderBy();
        assertEquals(1, orderBy.getOrderByItems().size());
        OrderByItem orderItem = orderBy.getOrderByItems().get(0);
        ColumnReference orderCol = (ColumnReference) orderItem.getOrderByExpression();
        testColumnReference(null, "order_count", orderCol);
        assert (!orderItem.isAsc());

        LimitClause limit = select.getLimit();
        testLiteralExpression(limit.getValue(), 10);

        OffsetClause offset = select.getOffset();
        testLiteralExpression(offset.getValue(), 5);
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

    private void testColumnReference(String expectedTable, String expectedCol, Expression expr) {
        assert(expr instanceof ColumnReference);
        ColumnReference aCol = (ColumnReference) expr;
        assert(aCol.getColumnName() instanceof Identifier);
        if (expectedTable != null) testIdentifier(expectedTable, aCol.getTableName());
        testIdentifier(expectedCol, aCol.getColumnName());
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
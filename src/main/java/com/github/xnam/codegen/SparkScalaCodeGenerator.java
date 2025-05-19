package com.github.xnam.codegen;

import com.github.xnam.ast.*;
import com.github.xnam.ast.Boolean;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.xnam.ast.FrameBoundType.*;

public class SparkScalaCodeGenerator implements CodegenVisitor<String> {
    public final ArrayList<String> windowSpecMapping = new ArrayList<>();
    public final HashMap<String, String> knownTables = new HashMap<>();
    public final ArrayList<String> emittedStatements = new ArrayList<>();
    public final ArrayList<String> unknownTableStatements = new ArrayList<>();
    public final List<String> aggregateFunctions = Arrays.asList(
            "APPROX_COUNT_DISTINCT", "AVG", "GROUPING", "COUNT", "MAX", "MIN",
            "STDEV", "SUM", "VAR_POP", "VAR_SAMP", "VARIANCE", "APPROX_PERCENTILE",
            "CORR", "COVAR_POP", "COVAR_SAMP", "FIRST", "LAST", "MEDIAN",
            "PERCENTILE_CONT", "PERCENTILE_DISC", "REGR_AVGX", "REGR_AVGY",
            "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX",
            "REGR_SXY", "REGR_SYY", "STDDEV_POP", "STDDEV_SAMP", "SUM_DISTINCT"
    );

    public String generateCode(Node program) {
        SparkCodeFormatter codeFormatter = new SparkCodeFormatter();
        String queryCode = program.accept(this);

        StringBuilder finalCode = new StringBuilder();
        for (String decl : unknownTableStatements) {
            finalCode.append(decl).append("\n");
        }
        for (int i = 0; i < windowSpecMapping.size(); i++) {
            finalCode.append("val windowSpec").append(i+1).append(" = ").append(windowSpecMapping.get(i)).append("\n");
        }
        for (String decl : emittedStatements) {
            finalCode.append(decl).append("\n");
        }
        finalCode.append("\nval result = ").append(queryCode);

        return codeFormatter.format(finalCode.toString());
    }

    public String visit(Boolean booleanLiteral) {
        return booleanLiteral.tokenLiteral();
    }

    public String visit(CaseExpression caseExpression) {
        StringBuilder output = new StringBuilder();
        for (Clause clause : caseExpression.getWhenThens()) {
            output.append(clause.accept(this));
        }
        if (caseExpression.hasElse()) {
            output.append(".otherwise(");
            output.append(caseExpression.getElseExpr().accept(this));
            output.append(")");
        }
        return output.toString();
    }

    public String visit(WhenThenClause whenThenClause) {
        StringBuilder output = new StringBuilder();
        output.append(".when(");
        output.append(whenThenClause.getWhenExpr().accept(this));
        output.append(", ");
        output.append(whenThenClause.getThenExpr().accept(this));
        output.append(")");
        return output.toString();
    }

    public String visit(ColumnReference columnReference) {
        if (columnReference.getTableName() != null) {
            return "col(\"" + columnReference.getTableName().accept(this) + "." + columnReference.getColumnName().accept(this) + "\")";
        } else {
            return "col(\"" + columnReference.getColumnName().accept(this) + "\")";
        }
    }

    public String visit(CommonTableExpression commonTableExpression) {
        StringBuilder code = new StringBuilder();

        String cteName = commonTableExpression.getToken().getLiteral();
        String subqueryCode = commonTableExpression.getSubquery().accept(this);

        code.append("val ").append(cteName).append(" = ").append(subqueryCode);
        knownTables.put(cteName, cteName);

        emittedStatements.add(code.toString());

        return cteName;
    }

    public String visit(ExpressionStatement expressionStatement) {
        return expressionStatement.getParsedNode().accept(this);
    }

    public String visit(FrameBound frameBound) {
        switch (frameBound.getBoundType()) {
            case UNBOUNDED_PRECEDING:
                return "Window.unboundedPreceding";
            case UNBOUNDED_FOLLOWING:
                return "Window.unboundedFollowing";
            case CURRENT_ROW:
                return "Window.currentRow";
            case N_PRECEDING:
                return "-" + frameBound.getNumRows();
            case N_FOLLOWING:
                return String.valueOf(frameBound.getNumRows());
            default:
                throw new UnsupportedOperationException("Unknown frame bound type: " + frameBound.getBoundType());
        }
    }

    public String visit(FrameClause frameClause) {
        String start = frameClause.getStart().accept(this);
        String end = frameClause.getEnd().accept(this);

        if (frameClause.isRange()) {
            return ".rangeBetween(" + start + ", " + end + ")";
        } else {
            return ".rowsBetween(" + start + ", " + end + ")";
        }
    }

    public String visit(FunctionCall functionCall) {
        StringBuilder code = new StringBuilder();

        String functionName = ((Identifier) functionCall.getFunction()).getValue().toLowerCase(); // normalize to lower-case

        code.append(functionName).append("(");

        List<String> args = functionCall.getArguments().stream()
                .map(arg -> arg.accept(this))
                .collect(Collectors.toList());

        code.append(String.join(", ", args)).append(")");

        if (functionCall.getWindowClause() != null) {
            code.append("over.(").append(functionCall.getWindowClause().accept(this)).append(")");
        }

        return code.toString();
    }

    public String visit(GroupByClause groupByClause) {
        if (groupByClause.getGroupByItems().isEmpty()) {
            return "";
        }

        String groupCols = groupByClause.getGroupByItems().stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", "));

        return ".groupBy(" + groupCols + ")";
    }

    public String visit(HavingClause havingClause) {
        return ".filter(" + havingClause.getHave().accept(this) + ")";
    }

    public String visit(Identifier identifier) {
        return identifier.getValue();
    }

    public String visit(InfixExpression infixExpression) {
        String operator = infixExpression.getOperator();
        String sparkOperator;

        switch (operator) {
            case "=":
                sparkOperator = "===";
                break;
            case "<>":
                sparkOperator = "=!=";
                break;
            case "AND":
                sparkOperator = "&&";
                break;
            case "OR":
                sparkOperator = "||";
                break;
            default:
                sparkOperator = operator;
                break;
        }

        return infixExpression.getLeftExpression().accept(this) + " " + sparkOperator + " " + infixExpression.getRightExpression().accept(this);
    }

    public String visit(IntegerLiteral integerLiteral) {
        return String.valueOf(integerLiteral.getValue());
    }

    public String visit(JoinClause joinClause) {
        StringBuilder code = new StringBuilder();

        String joinType = joinClause.getJoinType().toLowerCase();
        String rightTableAlias = joinClause.getTable().accept(this);
        String onCondition = joinClause.getOnCondition().accept(this);

        // Emit .join(right, condition, type)
        code.append(".join(")
                .append(rightTableAlias)
                .append(", ")
                .append(onCondition)
                .append(", ")
                .append("\"").append(joinType).append("\"")
                .append(")");

        return code.toString();
    }

    public String visit(LimitClause limitClause) {
        return ".limit(" + limitClause.getValue().accept(this) + ")";
    }

    public String visit(OffsetClause offsetClause) {
        return ".offset(" + offsetClause.getValue().accept(this) + ")";
    }

    public String visit(OrderByClause orderByClause) {
        if (orderByClause.getOrderByItems().isEmpty()) {
            return "";
        }

        String items = orderByClause.getOrderByItems().stream()
                .map(item -> item.accept(this))
                .collect(Collectors.joining(", "));

        return ".orderBy(" + items + ")";
    }

    public String visit(OrderByItem orderByItem) {
        String expr = orderByItem.getOrderByExpression().accept(this);
        if (orderByItem.isAsc()) {
            return expr + ".asc";
        } else {
            return expr + ".desc";
        }
    }

    public String visit(PrefixExpression prefixExpression) {
        String right = prefixExpression.getRightExpression().accept(this);

        switch (prefixExpression.getOperator()) {
            case "NOT":
                return "!" + right;
            case "-":
                return "-" + right;
            case "+":
                return right;
            default:
                throw new UnsupportedOperationException("Unknown prefix operator: " + prefixExpression.getOperator());
        }
    }

    public String visit(Program program) {
        StringBuilder output = new StringBuilder();
        for (Statement stmt : program.getStatements()) {
            output.append(stmt.accept(this));
        }
        return output.toString();
    }

    public String visit(SelectItem selectItem) {
        StringBuilder output = new StringBuilder();

        String expressionCode = selectItem.getSelectExpression().accept(this);
        output.append(expressionCode);

        if (selectItem.getAlias() != null) {
            output.append(".alias(\"").append(selectItem.getAlias().accept(this)).append("\")");
        }

        return output.toString();
    }

    public String visit(SelectStatement select) {
        StringBuilder code = new StringBuilder();

        // FROM
        String fromAlias = select.getFrom().accept(this);
        code.append(fromAlias);

        if (select.getWhere() != null) {
            code.append(select.getWhere().accept(this));
        }

        for (JoinClause join : select.getJoins()) {
            code.append(join.accept(this));
        }

        boolean hasGroupBy = select.getGroupBy() != null && !select.getGroupBy().getGroupByItems().isEmpty();
        if (hasGroupBy) {
            code.append(select.getGroupBy().accept(this));
        }

        Set<String> aggregatedAliases = new HashSet<>();
        List<String> aggExprs = new ArrayList<>();

        for (SelectItem item : select.getSelectItems()) {
            if (isAggregate(item.getSelectExpression())) {
                aggExprs.add(item.accept(this));
                if (item.getAlias() != null) {
                    aggregatedAliases.add(((Identifier) item.getAlias()).getValue());
                }
            }
        }

        if (hasGroupBy || !aggExprs.isEmpty()) {
            code.append(".agg(").append(String.join(", ", aggExprs)).append(")");
        }

        if (select.getHaving() != null) {
            code.append(select.getHaving().accept(this));
        }

        if (select.getOrderBy() != null) {
            code.append(select.getOrderBy().accept(this));
        }

        if (!select.getSelectItems().isEmpty()) {
            List<String> projectionExprs = new ArrayList<>();

            for (SelectItem item : select.getSelectItems()) {
                Identifier alias = (Identifier) item.getAlias();

                if (alias != null && aggregatedAliases.contains(alias.getValue())) {
                    projectionExprs.add("col(\"" + alias.getValue() + "\")");
                } else {
                    projectionExprs.add(item.accept(this));
                }
            }

            code.append(".select(").append(String.join(", ", projectionExprs)).append(")");
        }

        if (select.getLimit() != null) {
            code.append(select.getLimit().accept(this));
        }

        if (select.getOffset() != null) {
            code.append(select.getOffset().accept(this));
        }
        return code.toString();
    }

    public String visit(StringLiteral stringLiteral) {
        return "\""+ stringLiteral.getValue() + "\"";
    }

    public String visit(TableSource tableSource) {
        StringBuilder declaration = new StringBuilder();

        String alias;
        if (tableSource.getAlias() != null) {
            alias = tableSource.getAlias().accept(this);
        } else if (tableSource.getTable() instanceof Identifier) {
            alias = ((Identifier) tableSource.getTable()).getValue();
        } else {
            alias = "df_" + System.currentTimeMillis();
        }

        if (tableSource.getTable() instanceof SelectStatement) {
            String subqueryCode = tableSource.getTable().accept(this);
            declaration.append("val ").append(alias).append(" = ").append(subqueryCode);
            knownTables.put(alias, alias);
            emittedStatements.add(declaration.toString());
        } else {
            String baseName = (tableSource.getTable() instanceof Identifier)
                    ? ((Identifier) tableSource.getTable()).getValue()
                    : alias;

            if (!knownTables.containsKey(baseName)) {
                unknownTableStatements.add("val " + baseName + " = // User needs to define this table");
                knownTables.put(baseName, baseName);
            }

            if (!alias.equals(baseName) && !knownTables.containsKey(alias)) {
                alias = baseName + ".as(\"" + alias + "\")";
            }
        }

        return alias;
    }

    public String visit(WhereClause whereClause) {
        return ".filter(" + whereClause.getWhere().accept(this) + ")";
    }

    public String visit(WindowClause windowClause) {
        StringBuilder output = new StringBuilder();
        output.append("Window");
        if (!windowClause.getPartitionBy().isEmpty()) {
            String partitions = windowClause.getPartitionBy().stream()
                    .map(expr -> expr.accept(this))
                    .collect(Collectors.joining(", "));
            output.append(".partitionBy(").append(partitions).append(")");
        }
        if (windowClause.getOrderBy() != null) output.append(windowClause.getOrderBy().accept(this));
        if (windowClause.getFrame() != null) output.append(windowClause.getFrame().accept(this));
        String windowName = "windowSpec" + windowSpecMapping.size();
        windowSpecMapping.add(output.toString());
        return windowName;
    }

    public String visit(WithStatement withStatement) {
        StringBuilder code = new StringBuilder();
        for (CommonTableExpression cte : withStatement.getCteList()) {
            cte.accept(this);
        }
        String mainQuery = withStatement.getMainQuery().accept(this);
        code.append(mainQuery);
        return code.toString();
    }

    private boolean isAggregate(Expression expr) {
        if (expr instanceof FunctionCall) {
            String functionName =  ((Identifier) ((FunctionCall) expr).getFunction()).getValue().toLowerCase();
            return aggregateFunctions.contains(functionName);
        }
        return false;
    }

}

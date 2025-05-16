package com.github.xnam.codegen;

import com.github.xnam.ast.*;
import com.github.xnam.ast.Boolean;

public class SparkScalaCodeGenerator implements CodegenVisitor<String>{
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
        return null;
    }

    public String visit(ExpressionStatement expressionStatement) {
        return expressionStatement.getParsedNode().accept(this);
    }

    public String visit(FrameBound frameBound) {
        return null;
    }

    public String visit(FrameClause frameClause) {
        return null;
    }

    public String visit(FunctionCall functionCall) {
        return null;
    }

    public String visit(GroupByClause groupByClause) {
        return null;
    }

    public String visit(HavingClause havingClause) {
        return null;
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
                sparkOperator = operator; // +, -, *, /, etc. remain the same
                break;
        }

        return infixExpression.getLeftExpression().accept(this) + " " + sparkOperator + " " + infixExpression.getRightExpression().accept(this);
    }

    public String visit(IntegerLiteral integerLiteral) {
        return String.valueOf(integerLiteral.getValue());
    }

    public String visit(JoinClause joinClause) {
        return null;
    }

    public String visit(LimitClause limitClause) {
        return null;
    }

    public String visit(OffsetClause offsetClause) {
        return null;
    }

    public String visit(OrderByClause orderByClause) {
        return null;
    }

    public String visit(OrderByItem orderByItem) {
        return null;
    }

    public String visit(PrefixExpression prefixExpression) {
        return null;
    }

    public String visit(Program program) {
        StringBuilder output = new StringBuilder();
        for (Statement stmt : program.getStatements()) {
            output.append(stmt.accept(this));
        }
        return output.toString();
    }

    public String visit(SelectItem selectItem) {
        return null;
    }

    public String visit(SelectStatement selectStatement) {
        return null;
    }

    public String visit(StringLiteral stringLiteral) {
        return "\""+ stringLiteral.getValue() + "\"";
    }

    public String visit(TableSource tableSource) {
        return null;
    }

    public String visit(WhereClause whereClause) {
        return null;
    }

    public String visit(WindowClause windowClause) {
        return null;
    }

    public String visit(WithStatement withStatement) {
        return null;
    }
}

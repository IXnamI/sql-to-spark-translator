package com.github.xnam.codegen;

import com.github.xnam.ast.*;
import com.github.xnam.ast.Boolean;

public interface CodegenVisitor<R> {
    R visit(Boolean booleanLiteral);
    R visit(CaseExpression caseExpression);
    R visit(ColumnReference columnReference);
    R visit(CommonTableExpression commonTableExpression);
    R visit(ExpressionStatement expressionStatement);
    R visit(FrameBound frameBound);
    R visit(FrameClause frameClause);
    R visit(FunctionCall functionCall);
    R visit(GroupByClause groupByClause);
    R visit(HavingClause havingClause);
    R visit(Identifier identifier);
    R visit(InfixExpression infixExpression);
    R visit(IntegerLiteral integerLiteral);
    R visit(JoinClause joinClause);
    R visit(LimitClause limitClause);
    R visit(OffsetClause offsetClause);
    R visit(OrderByClause orderByClause);
    R visit(OrderByItem orderByItem);
    R visit(PrefixExpression prefixExpression);
    R visit(Program program);
    R visit(SelectItem selectItem);
    R visit(SelectStatement selectStatement);
    R visit(StringLiteral stringLiteral);
    R visit(TableSource tableSource);
    R visit(WhenThenClause whenThenClause);
    R visit(WhereClause whereClause);
    R visit(WindowClause windowClause);
    R visit(WithStatement withStatement);
}


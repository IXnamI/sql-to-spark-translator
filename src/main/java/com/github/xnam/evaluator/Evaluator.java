package com.github.xnam.evaluator;

import com.github.xnam.ast.*;
import com.github.xnam.ast.Boolean;
import com.github.xnam.object.*;
import com.github.xnam.object.Error;
import com.github.xnam.object.Integer;
import com.github.xnam.object.Object;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Evaluator {
    public static List<String> debug = new ArrayList<>();

    public static final com.github.xnam.object.Boolean TRUE = new com.github.xnam.object.Boolean(true);
    public static final com.github.xnam.object.Boolean FALSE = new com.github.xnam.object.Boolean(false);
    public static final Null NULL = new Null();

    public static Object eval(Node node, Environment env) {
        if (node instanceof Program) return evalProgram((Program) node, env);
        else if (node instanceof ReturnStatement) {
            Object returnVal = eval(((ReturnStatement) node).getReturnValue(), env);
            if (isError(returnVal)) return returnVal;
            return new ReturnValue(returnVal);
        } else if (node instanceof BlockStatement) return evalBlockStatement((BlockStatement) node, env);
        else if (node instanceof IfExpression) return evalIfExpression(node, env);
        else if (node instanceof LetStatement) {
            Object evaluated = eval(((LetStatement) node).getValue(), env);
            if (isError(evaluated)) return evaluated;
            env.set(((LetStatement) node).getName().getValue(), evaluated);
            return evaluated;
        } else if (node instanceof FunctionLiteral){
            FunctionLiteral func = (FunctionLiteral) node;
            Function funcObj = new Function(env);
            funcObj.setBody(func.getBody());
            funcObj.setParams(func.getParams());
            return funcObj;
        } else if (node instanceof ExpressionStatement) return eval(((ExpressionStatement) node).getExpression(), env);
        else if (node instanceof PrefixExpression) {
            PrefixExpression expr = (PrefixExpression) node;
            Object right = eval(expr.getRightExpression(), env);
            if (isError(right)) return right;
            return evalPrefixExpression(expr.getOperator(), right);
        } else if (node instanceof InfixExpression) {
            InfixExpression expr = (InfixExpression) node;
            Object left = eval(expr.getLeftExpression(), env);
            if (isError(left)) return left;
            Object right = eval(expr.getRightExpression(), env);
            if (isError(right)) return right;
            return evalInfixExpression(expr.getOperator(), left, right);
        } else if (node instanceof Identifier) return evalIdentifier((Identifier) node, env);
        else if (node instanceof Boolean) return toBooleanObject(((Boolean) node).getValue());
        else if (node instanceof IntegerLiteral) return new Integer(((IntegerLiteral) node).getValue());
        else return NULL;
    }

    private static Object evalPrefixExpression(String prefixOperator, Object rightExpression) {
        switch (prefixOperator) {
            case "!":
                return evalBangOperatorExpression(rightExpression);
            case "-":
                return evalMinusPrefixOperatorExpression(rightExpression);
            default:
                return new Error(String.format("unknown prefix operator: %s", prefixOperator));
        }
    }

    private static Object evalInfixExpression(String infixOperator, Object leftExpression, Object rightExpression) {
        if (leftExpression.getType().equals(ObjectType.INTEGER_OBJ) && rightExpression.getType().equals(ObjectType.INTEGER_OBJ)) {
            return evalIntegerInfixExpression(infixOperator, (Integer) leftExpression, (Integer) rightExpression);
        } else if (infixOperator.equals("==")){
            return toBooleanObject(leftExpression.equals(rightExpression));
        } else if (infixOperator.equals("!=")) {
            return toBooleanObject(!leftExpression.equals(rightExpression));
        } else if (!leftExpression.getType().equals(rightExpression.getType())) {
            return new Error(String.format("type mismatch: %s %s %s", leftExpression.getType(), infixOperator, rightExpression.getType()));
        } else {
            return new Error(String.format("unknown operator: %s %s %s", leftExpression.getType(), infixOperator, rightExpression.getType()));
        }
    }

    private static Object evalIntegerInfixExpression(String infixOperator, Integer leftExpression, Integer rightExpression) {
        java.lang.Integer leftVal = leftExpression.getValue();
        java.lang.Integer rightVal = rightExpression.getValue();
        switch (infixOperator) {
            case "+":
                return new Integer(leftVal + rightVal);
            case "-":
                return new Integer(leftVal - rightVal);
            case "*":
                return new Integer(leftVal * rightVal);
            case "/":
                return new Integer(leftVal / rightVal);
            case "<":
                return toBooleanObject(leftVal < rightVal);
            case ">":
                return toBooleanObject(leftVal > rightVal);
            case "==":
                return toBooleanObject(Objects.equals(leftVal, rightVal));
            case "!=":
                return toBooleanObject(!Objects.equals(leftVal, rightVal));
            default:
                return new Error(String.format("unknown operator: %s %s %s", leftExpression.getType(), infixOperator, rightExpression.getType()));
        }
    }

    private static Object evalBangOperatorExpression(Object rightExpression) {
        if ((rightExpression.equals(FALSE)) || (rightExpression.equals(NULL))) return TRUE;
        return FALSE;
    }

    private static Object evalMinusPrefixOperatorExpression(Object rightExpression) {
        if (!rightExpression.getType().equals(ObjectType.INTEGER_OBJ)) return new Error(String.format("unknown operator: -%s", rightExpression.getType()));
        Integer intObject = (Integer) rightExpression;
        return new Integer(-intObject.getValue());
    }

    private static Object evalProgram(Program program, Environment env) {
        Object result = null;
        assert !program.getStatements().isEmpty();
        for (Statement stmt : program.getStatements()) {
            result = eval(stmt, env);
            if (result instanceof Error) return result;
            if (result instanceof ReturnValue) return ((ReturnValue) result).getValue();
        }
        return result;
    }

    private static Object evalBlockStatement(BlockStatement block, Environment env) {
        Object result = null;
        assert !block.getStatements().isEmpty();
        for (Statement stmt : block.getStatements()) {
            result = eval(stmt, env);
            if (result != null ) {
                String type = result.getType();
                if (type.equals(ObjectType.RETURN_VALUE_OBJ) || type.equals(ObjectType.ERROR_OBJ)) return result;
            }
        }
        return result;
    }

    private static Object evalIfExpression(Node node, Environment env) {
        IfExpression ifExpr = (IfExpression) node;
        Object conditionResult = eval(ifExpr.getCondition(), env);
        if (isError(conditionResult)) return conditionResult;
        if (isTruthy(conditionResult)) return eval(ifExpr.getConsequence(), env);
        if (ifExpr.getAlternative() != null) return eval(ifExpr.getAlternative(), env);
        return NULL;
    }

    private static Object evalIdentifier(Identifier ident, Environment env) {
        Object evaluatedIdent = env.get(ident.getValue());
        if (evaluatedIdent == null) return new Error("identifier not found: " + ident.getValue());
        return evaluatedIdent;
    }

    private static boolean isTruthy(Object obj) {
        if (obj == TRUE) return true;
        else if (obj == FALSE || obj == NULL) return false;
        return true;
    }

    private static boolean isError(Object object) {
        if (object != null) return object.getType().equals(ObjectType.ERROR_OBJ);
        return false;
    }

    private static com.github.xnam.object.Boolean toBooleanObject(boolean boolValue) {
        if (boolValue) return TRUE;
        return FALSE;
    }

    private static void addDebugStatement(String msg) {
        debug.add(logWithLocation(msg));
    }

    public static void clearDebug() {
        debug.clear();
    }

    public static String logWithLocation(String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // Index 2 usually points to the caller of this method
        StackTraceElement element = stackTrace[2];
        String fileName = element.getFileName();
        int lineNumber = element.getLineNumber();

        return String.format("[%s:%d] %s", fileName, lineNumber, message);
    }
}

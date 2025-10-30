package ai.flexgalaxy.cql2.ast;

import java.util.HashMap;

public class Op2Type {
    private final HashMap<String, String> op2Type = new HashMap<>();
    public Op2Type() {
        op2Type.put("or", "andOrExpression");
        op2Type.put("and", "andOrExpression");
        op2Type.put("not", "notExpression");

        op2Type.put("=", "binaryComparisonPredicate");
        op2Type.put("<>", "binaryComparisonPredicate");
        op2Type.put(">", "binaryComparisonPredicate");
        op2Type.put("<", "binaryComparisonPredicate");
        op2Type.put(">=", "binaryComparisonPredicate");
        op2Type.put("<=", "binaryComparisonPredicate");

        op2Type.put("+", "arithmeticExpression");
        op2Type.put("-", "arithmeticExpression");
        op2Type.put("*", "arithmeticExpression");
        op2Type.put("/", "arithmeticExpression");
        op2Type.put("^", "arithmeticExpression");
        op2Type.put("%", "arithmeticExpression");
        op2Type.put("div", "arithmeticExpression");

        op2Type.put("like", "isLikePredicate");
        op2Type.put("between", "isBetweenPredicate");
        op2Type.put("in", "isInListPredicate");
        op2Type.put("isNull", "isNullPredicate");

        op2Type.put("casei", "characterClause");
        op2Type.put("accenti", "characterClause");

        op2Type.put("s_contains", "spatialPredicate");
        op2Type.put("s_crosses", "spatialPredicate");
        op2Type.put("s_disjoint", "spatialPredicate");
        op2Type.put("s_equals", "spatialPredicate");
        op2Type.put("s_intersects", "spatialPredicate");
        op2Type.put("s_overlaps", "spatialPredicate");
        op2Type.put("s_touches", "spatialPredicate");
        op2Type.put("s_within", "spatialPredicate");

        op2Type.put("t_after", "temporalPredicate");
        op2Type.put("t_before", "temporalPredicate");
        op2Type.put("t_contains", "temporalPredicate");
        op2Type.put("t_disjoint", "temporalPredicate");
        op2Type.put("t_during", "temporalPredicate");
        op2Type.put("t_equals", "temporalPredicate");
        op2Type.put("t_finishedBy", "temporalPredicate");
        op2Type.put("t_finishes", "temporalPredicate");
        op2Type.put("t_intersects", "temporalPredicate");
        op2Type.put("t_meets", "temporalPredicate");
        op2Type.put("t_metBy", "temporalPredicate");
        op2Type.put("t_overlappedBy", "temporalPredicate");
        op2Type.put("t_overlaps", "temporalPredicate");
        op2Type.put("t_startedBy", "temporalPredicate");
        op2Type.put("t_starts", "temporalPredicate");

        op2Type.put("a_containedBy", "arrayPredicate");
        op2Type.put("a_contains", "arrayPredicate");
        op2Type.put("a_equals", "arrayPredicate");
        op2Type.put("a_overlaps", "arrayPredicate");
    }

    public String type(String op) {
        return op2Type.getOrDefault(op, "functionRef");
    }
}

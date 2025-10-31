package ai.flexgalaxy.cql2.ast;

import java.util.HashMap;

import static ai.flexgalaxy.cql2.ast.AstNodeType.*;

public class Op2Type {
    private final HashMap<String, AstNodeType> op2Type = new HashMap<>();
    public Op2Type() {
        op2Type.put("or", AndOrExpression);
        op2Type.put("and", AndOrExpression);
        op2Type.put("not", NotExpression);

        op2Type.put("=", BinaryComparisonPredicate);
        op2Type.put("<>", BinaryComparisonPredicate);
        op2Type.put(">", BinaryComparisonPredicate);
        op2Type.put("<", BinaryComparisonPredicate);
        op2Type.put(">=", BinaryComparisonPredicate);
        op2Type.put("<=", BinaryComparisonPredicate);

        op2Type.put("+", ArithmeticExpression);
        op2Type.put("-", ArithmeticExpression);
        op2Type.put("*", ArithmeticExpression);
        op2Type.put("/", ArithmeticExpression);
        op2Type.put("^", ArithmeticExpression);
        op2Type.put("%", ArithmeticExpression);
        op2Type.put("div", ArithmeticExpression);

        op2Type.put("like", IsLikePredicate);
        op2Type.put("between", IsBetweenPredicate);
        op2Type.put("in", IsInListPredicate);
        op2Type.put("isNull", IsNullPredicate);

        op2Type.put("casei", CharacterClause);
        op2Type.put("accenti", CharacterClause);

        op2Type.put("s_contains", SpatialPredicate);
        op2Type.put("s_crosses", SpatialPredicate);
        op2Type.put("s_disjoint", SpatialPredicate);
        op2Type.put("s_equals", SpatialPredicate);
        op2Type.put("s_intersects", SpatialPredicate);
        op2Type.put("s_overlaps", SpatialPredicate);
        op2Type.put("s_touches", SpatialPredicate);
        op2Type.put("s_within", SpatialPredicate);

        op2Type.put("t_after", TemporalPredicate);
        op2Type.put("t_before", TemporalPredicate);
        op2Type.put("t_contains", TemporalPredicate);
        op2Type.put("t_disjoint", TemporalPredicate);
        op2Type.put("t_during", TemporalPredicate);
        op2Type.put("t_equals", TemporalPredicate);
        op2Type.put("t_finishedBy", TemporalPredicate);
        op2Type.put("t_finishes", TemporalPredicate);
        op2Type.put("t_intersects", TemporalPredicate);
        op2Type.put("t_meets", TemporalPredicate);
        op2Type.put("t_metBy", TemporalPredicate);
        op2Type.put("t_overlappedBy", TemporalPredicate);
        op2Type.put("t_overlaps", TemporalPredicate);
        op2Type.put("t_startedBy", TemporalPredicate);
        op2Type.put("t_starts", TemporalPredicate);

        op2Type.put("a_containedBy", ArrayPredicate);
        op2Type.put("a_contains", ArrayPredicate);
        op2Type.put("a_equals", ArrayPredicate);
        op2Type.put("a_overlaps", ArrayPredicate);
    }

    public AstNodeType type(String op) {
        return op2Type.getOrDefault(op, FunctionRef);
    }
}

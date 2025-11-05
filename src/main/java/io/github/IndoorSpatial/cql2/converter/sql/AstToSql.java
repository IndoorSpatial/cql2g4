package io.github.IndoorSpatial.cql2.converter.sql;

import io.github.IndoorSpatial.cql2.ast.AstNode;
import io.github.IndoorSpatial.cql2.ast.AstNodeType;
import static io.github.IndoorSpatial.cql2.ast.AstNodeType.*;

import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;


public class AstToSql {

    private final HashMap<AstNodeType, BiFunction<AstNode, AstNodeType, String>> typedConverters = new HashMap<>();

    final Function<AstNode, String> binaryInfixOperator = node ->
            convert(node.getArgs().getFirst()) + " " + node.getOp().toUpperCase() + " " + convert(node.getArgs().getLast());
    final Function<AstNode, String> unaryPrefixOperator = node ->
            node.getOp().toUpperCase() + " (" + convert(node.getArgs().getFirst()) + ")";
    final BiFunction<AstNode, Function<String, String>, String> function = (node, fname) -> {
        StringBuilder sb = new StringBuilder();
        sb.append(fname.apply(node.getOp()));
        sb.append("(");
        for (int i = 0; i < node.getArgs().size(); i++) {
            sb.append(convert(node.getArgs().get(i)));
            if (i < node.getArgs().size() - 1)
                sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    };

    final BiFunction<AstNode, String, String> arrayS = (node, surrounding) -> {
        StringBuilder sb = new StringBuilder();
        sb.append(surrounding.charAt(0));
        for (int i = 0; i < node.getArgs().size(); i++) {
            sb.append(convert(node.getArgs().get(i)));
            if (i < node.getArgs().size() - 1)
                sb.append(", ");
        }
        sb.append(surrounding.charAt(1));
        return sb.toString();
    };
    final Function<AstNode, String> array = (node) -> arrayS.apply(node, "()");
    @Setter
    private int srid = 4326;

    public AstToSql() {
        this(null);
    }

    public AstToSql(Function<String, String> propertyToQueryable) {
        typedConverters.put(AndOrExpression, (node, pt) ->
            String.join(" " + node.getOp().toUpperCase() + " ", node.getArgs().stream().map(this::convert).toList()));

        typedConverters.put(NotExpression, (node, pt) -> unaryPrefixOperator.apply(node));

        typedConverters.put(BinaryComparisonPredicate, (node, pt) -> binaryInfixOperator.apply(node));

        typedConverters.put(ArithmeticExpression, (node, pt) -> {
            if (node.getOp().equals("^"))
                return "POWER(" + convert(node.getArgs().getFirst()) + ", " + convert(node.getArgs().getLast()) + ")";
            else if (node.getOp().equals("div"))
                return convert(node.getArgs().getFirst()) + " / " + convert(node.getArgs().getLast());
            else
                return convert(node.getArgs().getFirst()) + " " + node.getOp().toUpperCase() + " " + convert(node.getArgs().getLast());
        });

        typedConverters.put(IsLikePredicate, (node, pt) -> binaryInfixOperator.apply(node));

        typedConverters.put(IsBetweenPredicate, (node, pt) ->
                convert(node.getArgs().getFirst()) +" BETWEEN " + convert(node.getArgs().get(1)) + " AND " + convert(node.getArgs().getLast()));

        typedConverters.put(IsInListPredicate, (node, pt) -> binaryInfixOperator.apply(node));

        typedConverters.put(IsNullPredicate, (node, pt) -> convert(node.getArgs().getFirst()) + " IS NULL");

        typedConverters.put(CharacterClause, (node, pt) -> {
            if (Objects.equals(node.getOp(), "accenti"))
                return "UNACCENT" + array.apply(node);
            if (Objects.equals(node.getOp(), "casei"))
                return "LOWER" + array.apply(node);
            return null;
        });

        typedConverters.put(SpatialPredicate, (node, pt) ->
                function.apply(node, op -> "ST" + op.substring(1).toUpperCase()));

        typedConverters.put(TemporalPredicate, (node, pt) -> {
            String lhs = convert(node.getArgs().getFirst());
            String rhs = convert(node.getArgs().getLast());
            if (node.getArgs().getFirst().getType() == IntervalInstance)
                lhs = "TSRANGE(" + lhs + ", " + lhs + ", '[]')";
            if (node.getArgs().getLast().getType() == IntervalInstance)
                rhs = "TSRANGE(" + rhs + ", " + rhs + ", '[]')";
            return switch (node.getOp()) {
                case "t_equals" -> lhs + " = " + rhs;  // point-point, point-interval

                case "t_after" -> lhs + " >> " + rhs;  // point, point-interval
                case "t_before" -> lhs + " << " + rhs;  // point, point-interval
                case "t_contains" -> lhs + " @> " + rhs;
                case "t_during" -> lhs + " <@ " + rhs;
                case "t_intersects" -> lhs + " && " + rhs;  // point, point-interval
                case "t_disjoint" -> "NOT (" + lhs + " && " + rhs + ")";  // point, point-interval

                case "t_starts" ->
                        "LOWER(" + lhs + ") = LOWER(" + rhs + ") AND UPPER(" + lhs + ") < UPPER(" + rhs + ")";
                case "t_startedBy" ->
                        "LOWER(" + lhs + ") = LOWER(" + rhs + ") AND UPPER(" + lhs + ") > UPPER(" + rhs + ")";

                case "t_finishes" ->
                        "UPPER(" + lhs + ") = UPPER(" + rhs + ") AND LOWER(" + lhs + ") < LOWER(" + rhs + ")";
                case "t_finishedBy" ->
                        "UPPER(" + lhs + ") = UPPER(" + rhs + ") AND LOWER(" + lhs + ") > LOWER(" + rhs + ")";

                case "t_meets" -> "UPPER(" + lhs + ") = LOWER(" + rhs + ")";
                case "t_metBy" -> "LOWER(" + lhs + ") = UPPER(" + rhs + ")";

                case "t_overlaps" ->
                        lhs + " && " + rhs + " AND LOWER(" + lhs + ") < LOWER(" + rhs + ") AND UPPER(" + lhs + ") < UPPER(" + rhs + ")";
                case "t_overlappedBy" ->
                        lhs + " && " + rhs + " AND LOWER(" + lhs + ") > LOWER(" + rhs + ") AND UPPER(" + lhs + ") > UPPER(" + rhs + ")";

                default -> throw new IllegalStateException("Unexpected temporal operator: " + node.getOp());
            };
        });

        typedConverters.put(ArrayPredicate, (node, pt) ->
            switch (node.getOp()) {
                case "a_overlaps" ->
                    convert(node.getArgs().getFirst()) + " && " + convert(node.getArgs().getLast());
                case "a_contains" ->
                    convert(node.getArgs().getFirst()) + " @> " + convert(node.getArgs().getLast());
                case "a_containedBy" ->
                    convert(node.getArgs().getFirst()) + " <@ " + convert(node.getArgs().getLast());
                case "a_equals" -> {
                    String left = convert(node.getArgs().getFirst());
                    String right = convert(node.getArgs().getLast());
                    yield "(" + left + " @> " + right + ") AND (" + left + " <@ " + right + ")";
                }
                default -> throw new RuntimeException("unknow array operator " + node.getOp());
            });

        typedConverters.put(FunctionRef, (node, pt) -> function.apply(node, op -> op));

        typedConverters.put(PropertyLiteral, (node, pt) -> {
            String property_name = node.getValue();
            if (propertyToQueryable == null)
                return "\"" + property_name + "\"";
            else
                return propertyToQueryable.apply(property_name);
        });

        typedConverters.put(StringLiteral, (node, pt) -> {
            String value = node.getValue();
            if (pt == IntegerLiteral && Objects.equals(value, ".."))
                return "NULL";
            else
                return "'" + value + "'";
        });

        typedConverters.put(DoubleLiteral, (node, pt) -> Double.toString(node.getValue()));

        typedConverters.put(IntegerLiteral, (node, pt) -> Integer.toString(node.getValue()));

        typedConverters.put(BooleanLiteral, (node, pt) -> {
            boolean b = node.getValue();
            return b ? "TRUE" : "FALSE";
        });

        typedConverters.put(DateLiteral, (node, pt) -> {
            String date = node.getValue();
            return "DATE '" + date + "'";
        });

        typedConverters.put(ArrayExpression, (node, pt) -> "ARRAY " + arrayS.apply(node, "[]"));

        typedConverters.put(InListOperands, (node, pt) -> array.apply(node));

        typedConverters.put(GeometryLiteral, (node, pt) -> {
            Geometry geom = node.getValue();
            return "ST_GeomFromText('" + geom.toText() + "', " + srid + ")";
        });

        typedConverters.put(IntervalInstance, (node, pt) -> "TSRANGE(" +
                convert(node.getArgs().getFirst(), node.getType()) +
                ", " +
                convert(node.getArgs().getLast(), node.getType()) +
                ", '[]')");

        typedConverters.put(TimestampLiteral, (node, pt) -> "'" + node.getValue() + "'");

        typedConverters.put(BBoxLiteral, (node, pt) -> {
            List<Double> bbox = node.getValue();
            double minx = bbox.get(0);
            double miny = bbox.get(1);
            double maxx = bbox.get(2);
            double maxy = bbox.get(3);
            return String.format("ST_GeomFromText('POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))', %d)",
                    minx, miny,
                    maxx, miny,
                    maxx, maxy,
                    minx, maxy,
                    minx, miny,
                    srid
            );
        });
    }

    public String convert(AstNode node) {
        return convert(node, null);
    }

    private String convert(AstNode node, AstNodeType parentType) {
        if (typedConverters.containsKey(node.getType()))
            return typedConverters.get(node.getType()).apply(node, parentType);
        else
            throw new RuntimeException("Unsupported node type: " + node.getType());
    }
}

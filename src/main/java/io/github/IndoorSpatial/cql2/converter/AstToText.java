package io.github.IndoorSpatial.cql2.converter;

import io.github.IndoorSpatial.cql2.ast.AstNode;
import io.github.IndoorSpatial.cql2.ast.AstNodeType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.github.IndoorSpatial.cql2.ast.AstNodeType.*;

public class AstToText {

    private final HashMap<AstNodeType, Function<AstNode, String>> typedConverters = new HashMap<>();

    final Function<AstNode, String> binaryInfixOperator = node ->
            convert(node.getArgs().getFirst()) + " " + node.getOp() + " " + convert(node.getArgs().getLast());
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

    public AstToText() {
        typedConverters.put(AndOrExpression, node ->
                String.join(" " + node.getOp().toUpperCase() + " ",
                        node.getArgs().stream().map(arg -> "(" + convert(arg) + ")").toList()));

        typedConverters.put(NotExpression, unaryPrefixOperator);

        typedConverters.put(BinaryComparisonPredicate, binaryInfixOperator);

        typedConverters.put(ArithmeticExpression, binaryInfixOperator);

        typedConverters.put(IsLikePredicate, binaryInfixOperator);

        typedConverters.put(IsBetweenPredicate, node ->
                convert(node.getArgs().getFirst()) +" BETWEEN " + convert(node.getArgs().get(1)) + " AND " + convert(node.getArgs().getLast()));

        typedConverters.put(IsInListPredicate, binaryInfixOperator);

        typedConverters.put(IsNullPredicate, node -> convert(node.getArgs().getFirst()) + " IS NULL");

        typedConverters.put(CharacterClause, node -> node.getOp().toUpperCase() + array.apply(node));

        typedConverters.put(SpatialPredicate, node -> function.apply(node, String::toUpperCase));

        typedConverters.put(TemporalPredicate, node -> function.apply(node, String::toUpperCase));

        typedConverters.put(ArrayPredicate, node -> function.apply(node, String::toUpperCase));

        typedConverters.put(FunctionRef, node -> function.apply(node, op -> op));

        typedConverters.put(PropertyLiteral, node -> "\"" + node.getValue() + "\"");

        typedConverters.put(StringLiteral, node -> "'" + node.getValue() + "'");

        typedConverters.put(DoubleLiteral, node -> Double.toString(node.getValue()));

        typedConverters.put(IntegerLiteral, node -> Integer.toString(node.getValue()));

        typedConverters.put(BooleanLiteral, node -> node.getValue() ? "TRUE" : "FALSE");

        typedConverters.put(DateLiteral, node -> "DATE('" + node.getValue() + "')");

        typedConverters.put(ArrayExpression, array);

        typedConverters.put(InListOperands, array);

        typedConverters.put(GeometryLiteral, node -> {
            Geometry geom = node.getValue();
            int outputDimension = 2;
            if (Arrays.stream(geom.getCoordinates()).toList().stream().anyMatch(coor -> coor.getZ() != 0))
                outputDimension = 3;
            WKTWriter writer = new WKTWriter(outputDimension);
            return writer.write(geom);
        });

        typedConverters.put(IntervalInstance, node ->
                "INTERVAL(" +
                convert(node.getArgs().getFirst()) +
                ", " +
                convert(node.getArgs().getLast()) +
                ")");

        typedConverters.put(TimestampLiteral, node -> "TIMESTAMP('" + node.getValue() + "')");

        typedConverters.put(BBoxLiteral, node -> {
            List<Double> bbox = node.getValue();
            return "BBOX(" + String.join(",", bbox.stream().map(String::valueOf).toList()) + ")";
        });
    }

    public String convert(AstNode node) {
        if (typedConverters.containsKey(node.getType()))
            return typedConverters.get(node.getType()).apply(node);
        else
            throw new RuntimeException("Unsupported node type: " + node.getType());
    }
}

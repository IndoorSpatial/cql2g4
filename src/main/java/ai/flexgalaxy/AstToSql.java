package ai.flexgalaxy;

import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AstToSql {

    private final HashMap<String, Function<AstNode, String>> typedConverters = new HashMap<>();

    Function<AstNode, String> binaryInfixOperator = node ->
            visit(node.getArgs().getFirst()) + " " + node.getOp().toUpperCase() + " " + visit(node.getArgs().getLast());
    Function<AstNode, String> unaryPrefixOperator = node ->
            node.getOp().toUpperCase() + " (" + visit(node.getArgs().getFirst()) + ")";
    BiFunction<AstNode, Boolean, String> function = (node, upper) -> {
        StringBuilder sb = new StringBuilder();
        if (upper)
            sb.append(node.getOp().toUpperCase());
        else
            sb.append(node.getOp());
        sb.append("(");
        for (int i = 0; i < node.getArgs().size(); i++) {
            sb.append(visit(node.getArgs().get(i)));
            if (i < node.getArgs().size() - 1)
                sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    };

    Function<AstNode, String> array = (node) -> {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < node.getArgs().size(); i++) {
            sb.append(visit(node.getArgs().get(i)));
            if (i < node.getArgs().size() - 1)
                sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    };

    public AstToSql() {
        this(SqlDialect.PostgreSQL);
    }

    public AstToSql(SqlDialect dialect) {
        typedConverters.put("andOrExpression", binaryInfixOperator);
        typedConverters.put("notExpression", unaryPrefixOperator);
        typedConverters.put("binaryComparisonPredicate", binaryInfixOperator);
        typedConverters.put("arithmeticExpression", node -> {
            if (node.getOp().equals("^"))
                return "POWER(" + visit(node.getArgs().getFirst()) + ", " + visit(node.getArgs().getLast()) + ")";
            else
                return visit(node.getArgs().getFirst()) + " " + node.getOp().toUpperCase() + " " + visit(node.getArgs().getLast());
        });
        typedConverters.put("isLikePredicate", binaryInfixOperator);
        typedConverters.put("isBetweenPredicate", node ->
                visit(node.getArgs().getFirst()) + " BETWEEN " + visit(node.getArgs().get(1)) + " AND " + visit(node.getArgs().getLast()));
        typedConverters.put("isInListPredicate", binaryInfixOperator);
        typedConverters.put("isNullPredicate", node -> visit(node.getArgs().getFirst()) + " IS NULL");
        typedConverters.put("characterClause", node -> function.apply(node, true));
        typedConverters.put("spatialPredicate", node -> function.apply(node, true));
        typedConverters.put("temporalPredicate", node -> function.apply(node, true));
        typedConverters.put("arrayPredicate", node -> function.apply(node, true));
        typedConverters.put("functionRef", node -> function.apply(node, false));
        typedConverters.put("Property", node -> {
            String property_name = (String) ((AstLiteral) node).getValue();
            return switch (dialect) {
                case SQLite -> "\"" + property_name + "\"";
                case MySQL -> "`" + property_name + "`";
                case Oracle -> "\"" + property_name + "\"";
                case PostgreSQL -> "\"" + property_name + "\"";
                case SQLServer -> "[" + property_name + "]";
            };
        });
        typedConverters.put("String", node -> "'" + ((AstLiteral) node).getValue() + "'");
        typedConverters.put("Double", node -> Double.toString((double) ((AstLiteral) node).getValue()));
        typedConverters.put("Integer", node -> Integer.toString((int) ((AstLiteral) node).getValue()));
        typedConverters.put("Boolean", node -> {
            boolean b = (boolean) ((AstLiteral) node).getValue();
            return switch (dialect) {
                case SQLite -> b ? "TRUE" : "FALSE";
                case MySQL -> b ? "TRUE" : "FALSE";
                case Oracle -> b ? "1" : "0";
                case PostgreSQL -> b ? "TRUE" : "FALSE";
                case SQLServer -> b ? "1" : "0";
            };
        });
        typedConverters.put("Date", node -> {
            String date = (String) ((AstLiteral) node).getValue();
            return switch (dialect) {
                case SQLite -> "DATE(" + date + ")";
                case MySQL -> "DATE '" + date + "'";
                case Oracle -> "DATE '" + date + "'";
                case PostgreSQL -> "DATE '" + date + "'";
                case SQLServer -> "CAST('" + date + "' AS DATE)";
            };
        });
        typedConverters.put("arrayExpression", node -> array.apply(node));
        typedConverters.put("Geometry", node -> {
            Geometry geom = (Geometry)((AstLiteral)node).getValue();
            return "ST_GeomFromText('" + geom.toText() + "')";
        });

        // TODO: we should support PostgreSQL only
        typedConverters.put("intervalInstance", node -> array.apply(node));

        typedConverters.put("Timestamp", node -> (String)((AstLiteral)node).getValue());
        typedConverters.put("BBox", node -> {
            List<Double> bbox = (List<Double>)((AstLiteral)node).getValue();
            double minx = bbox.get(0);
            double miny = bbox.get(1);
            double maxx = bbox.get(2);
            double maxy = bbox.get(3);
            return String.format("ST_GeomFromText('POLYGON(%f %f, %f %f, %f %f, %f %f, %f %f)')",
                    minx, miny,
                    maxx, miny,
                    maxx, maxy,
                    minx, maxy,
                    minx, miny
                    );
        });
    }

    public String visit(AstNode node) {
        if (typedConverters.containsKey(node.getType()))
            return typedConverters.get(node.getType()).apply(node);
        else
            throw new RuntimeException("Unsupported node type: " + node.getType());
    }
}

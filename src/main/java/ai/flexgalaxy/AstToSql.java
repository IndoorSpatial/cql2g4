package ai.flexgalaxy;

import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AstToSql {
    public static class Config {
        SqlDialect dialect = SqlDialect.PostgreSQL;
        ArrayType arrayType = ArrayType.BuiltIn;
    }

    private final HashMap<String, Function<AstNode, String>> typedConverters = new HashMap<>();

    Function<AstNode, String> binaryInfixOperator = node ->
            visit(node.getArgs().getFirst()) + " " + node.getOp().toUpperCase() + " " + visit(node.getArgs().getLast());
    Function<AstNode, String> unaryPrefixOperator = node ->
            node.getOp().toUpperCase() + " (" + visit(node.getArgs().getFirst()) + ")";
    BiFunction<AstNode, Function<String, String>, String> function = (node, fname) -> {
        StringBuilder sb = new StringBuilder();
        sb.append(fname.apply(node.getOp()));
        sb.append("(");
        for (int i = 0; i < node.getArgs().size(); i++) {
            sb.append(visit(node.getArgs().get(i)));
            if (i < node.getArgs().size() - 1)
                sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    };

    BiFunction<AstNode, String, String> arrayS = (node, surrounding) -> {
        StringBuilder sb = new StringBuilder();
        sb.append(surrounding.charAt(0));
        for (int i = 0; i < node.getArgs().size(); i++) {
            sb.append(visit(node.getArgs().get(i)));
            if (i < node.getArgs().size() - 1)
                sb.append(", ");
        }
        sb.append(surrounding.charAt(1));
        return sb.toString();
    };
    Function<AstNode, String> array = (node) -> arrayS.apply(node, "()");

    public AstToSql() {
        this(new Config());
    }

    public AstToSql(Config config) {
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
        typedConverters.put("characterClause", node -> function.apply(node, String::toUpperCase));
        typedConverters.put("spatialPredicate", node -> function.apply(node, op -> "ST" + op.substring(1).toUpperCase()));
        typedConverters.put("temporalPredicate", node -> function.apply(node, String::toUpperCase));
        typedConverters.put("arrayPredicate", node -> {
            if (config.arrayType == ArrayType.BuiltIn) {
                return visit(node.getArgs().getFirst()) + " && " + visit(node.getArgs().getLast());
            } else {
                throw new RuntimeException("Unsupported array type");
            }
        });
        typedConverters.put("functionRef", node -> function.apply(node, op -> op));
        typedConverters.put("Property", node -> {
            String property_name = (String) ((AstLiteral) node).getValue();
            return switch (config.dialect) {
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
            return switch (config.dialect) {
                case SQLite -> b ? "TRUE" : "FALSE";
                case MySQL -> b ? "TRUE" : "FALSE";
                case Oracle -> b ? "1" : "0";
                case PostgreSQL -> b ? "TRUE" : "FALSE";
                case SQLServer -> b ? "1" : "0";
            };
        });
        typedConverters.put("Date", node -> {
            String date = (String) ((AstLiteral) node).getValue();
            return switch (config.dialect) {
                case SQLite -> "DATE(" + date + ")";
                case MySQL -> "DATE '" + date + "'";
                case Oracle -> "DATE '" + date + "'";
                case PostgreSQL -> "DATE '" + date + "'";
                case SQLServer -> "CAST('" + date + "' AS DATE)";
            };
        });
        typedConverters.put("arrayExpression", node -> {
            if (config.dialect == SqlDialect.PostgreSQL) {
                if (config.arrayType == ArrayType.BuiltIn)
                    return "ARRAY " + arrayS.apply(node, "[]");
                else if (config.arrayType == ArrayType.Json)
                    throw new RuntimeException("unimplemented array type");
                else
                    throw new RuntimeException("Unsupported array type:  " + config.arrayType);
            } else {
                throw new RuntimeException("Unsupported array type in dialect: " + config.dialect);
            }
        });
        typedConverters.put("Geometry", node -> {
            Geometry geom = (Geometry) ((AstLiteral) node).getValue();
            return "ST_GeomFromText('" + geom.toText() + "')";
        });

        // TODO: we should support PostgreSQL only
        typedConverters.put("intervalInstance", node -> array.apply(node));

        typedConverters.put("Timestamp", node -> "'" + ((AstLiteral) node).getValue() + "'");
        typedConverters.put("BBox", node -> {
            List<Double> bbox = (List<Double>) ((AstLiteral) node).getValue();
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

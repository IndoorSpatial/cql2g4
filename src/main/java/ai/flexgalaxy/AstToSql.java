package ai.flexgalaxy;

import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AstToSql {
    public static class Config {
        SqlDialect dialect = SqlDialect.PostgreSQL;
        ArrayType arrayType = ArrayType.BuiltIn;
        public void set(SqlDialect dialect) { this.dialect = dialect; }
        public void set(ArrayType arrayType) { this.arrayType = arrayType; }
    }

    private final HashMap<String, BiFunction<AstNode, String, String>> typedConverters = new HashMap<>();

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
        typedConverters.put("andOrExpression", (node, pt) -> binaryInfixOperator.apply(node));
        typedConverters.put("notExpression", (node, pt) -> unaryPrefixOperator.apply(node));
        typedConverters.put("binaryComparisonPredicate", (node, pt) -> binaryInfixOperator.apply(node));
        typedConverters.put("arithmeticExpression", (node, pt) -> {
            if (node.getOp().equals("^"))
                return "POWER(" + visit(node.getArgs().getFirst()) + ", " + visit(node.getArgs().getLast()) + ")";
            else if (node.getOp().equals("div"))
                return visit(node.getArgs().getFirst()) + " / " + visit(node.getArgs().getLast());
            else
                return visit(node.getArgs().getFirst()) + " " + node.getOp().toUpperCase() + " " + visit(node.getArgs().getLast());
        });
        typedConverters.put("isLikePredicate", (node, pt) -> binaryInfixOperator.apply(node));
        typedConverters.put("isBetweenPredicate", (node, pt) ->
                visit(node.getArgs().getFirst()) + " BETWEEN " + visit(node.getArgs().get(1)) + " AND " + visit(node.getArgs().getLast()));
        typedConverters.put("isInListPredicate", (node, pt) -> binaryInfixOperator.apply(node));
        typedConverters.put("isNullPredicate", (node, pt) -> visit(node.getArgs().getFirst()) + " IS NULL");
        typedConverters.put("characterClause", (node, pt) -> {
            if (Objects.equals(node.getOp(), "accenti"))
                return "UNACCENT" + array.apply(node);
            if (Objects.equals(node.getOp(), "casei"))
                return "LOWER" + array.apply(node);
            return function.apply(node, String::toUpperCase);
        });
        typedConverters.put("spatialPredicate", (node, pt) -> function.apply(node, op -> "ST" + op.substring(1).toUpperCase()));
        typedConverters.put("temporalPredicate", (node, pt) -> {
            String lhs = visit(node.getArgs().getFirst());
            String rhs = visit(node.getArgs().getLast());
            if (!Objects.equals(node.getArgs().getFirst().getType(), "intervalInstance"))
                lhs = "TSRANGE(" + lhs + ", " + lhs + ", '[]')";
            if (!Objects.equals(node.getArgs().getLast().getType(), "intervalInstance"))
                rhs = "TSRANGE(" + rhs + ", " + rhs + ", '[]')";
            return switch (node.getOp()) {
                case "t_equals" -> lhs + " = " + rhs;  // point-point, point-interval

                case "t_after"  -> lhs + " >> " + rhs;  // point, point-interval
                case "t_before"  -> lhs + " << " + rhs;  // point, point-interval
                case "t_contains"  -> lhs + " @> " + rhs;
                case "t_during"  -> lhs + " <@ " + rhs;
                case "t_intersects"  -> lhs + " && " + rhs;  // point, point-interval
                case "t_disjoint"  -> "NOT (" + lhs + " && " + rhs + ")";  // point, point-interval

                case "t_starts" -> "LOWER(" + lhs + ") = LOWER(" + rhs + ") AND UPPER(" + lhs + ") < UPPER(" + rhs + ")";
                case "t_startedBy" -> "LOWER(" + lhs + ") = LOWER(" + rhs + ") AND UPPER(" + lhs + ") > UPPER(" + rhs + ")";

                case "t_finishes" -> "UPPER(" + lhs + ") = UPPER(" + rhs + ") AND LOWER(" + lhs + ") < LOWER(" + rhs + ")";
                case "t_finishedBy" -> "UPPER(" + lhs + ") = UPPER(" + rhs + ") AND LOWER(" + lhs + ") > LOWER(" + rhs + ")";

                case "t_meets" -> "UPPER(" + lhs + ") = LOWER(" + rhs + ")";
                case "t_metBy" -> "LOWER(" + lhs + ") = UPPER(" + rhs + ")";

                case "t_overlaps"  -> lhs + " && " + rhs + " AND LOWER(" + lhs + ") < LOWER(" + rhs + ") AND UPPER(" + lhs + ") < UPPER(" + rhs + ")";
                case "t_overlappedBy"  -> lhs + " && " + rhs + " AND LOWER(" + lhs + ") > LOWER(" + rhs + ") AND UPPER(" + lhs + ") > UPPER(" + rhs + ")";

                default -> throw new IllegalStateException("Unexpected temporal operator: " + node.getOp());
            };

        });
        typedConverters.put("arrayPredicate", (node, pt) -> {
            if (config.arrayType == ArrayType.BuiltIn) {
                if (node.getOp().equals("a_overlaps"))
                    return visit(node.getArgs().getFirst()) + " && " + visit(node.getArgs().getLast());
                if (node.getOp().equals("a_contains"))
                    return visit(node.getArgs().getFirst()) + " @> " + visit(node.getArgs().getLast());
                if (node.getOp().equals("a_containedBy"))
                    return visit(node.getArgs().getFirst()) + " <@ " + visit(node.getArgs().getLast());
                if (node.getOp().equals("a_equals")) {
                    String left = visit(node.getArgs().getFirst());
                    String right = visit(node.getArgs().getLast());
                    return "(" + left + " @> " + right + ") AND (" + left + " <@ " + right + ")";
                }
                throw new RuntimeException("unknow array operator " + node.getOp());
            } else {
                throw new RuntimeException("Unsupported array type");
            }
        });
        typedConverters.put("functionRef", (node, pt) -> function.apply(node, op -> op));
        typedConverters.put("Property", (node, pt) -> {
            String property_name = (String) ((AstLiteral) node).getValue();
            return switch (config.dialect) {
                case SQLite -> "\"" + property_name + "\"";
                case MySQL -> "`" + property_name + "`";
                case Oracle -> "\"" + property_name + "\"";
                case PostgreSQL -> "\"" + property_name + "\"";
                case SQLServer -> "[" + property_name + "]";
            };
        });
        typedConverters.put("String", (node, pt) -> {
            String value = (String)((AstLiteral) node).getValue();
            if (Objects.equals(pt, "intervalInstance") && Objects.equals(value, ".."))
                return "NULL";
            else
                return "'" + value + "'";
        });
        typedConverters.put("Double", (node, pt) -> Double.toString((double) ((AstLiteral) node).getValue()));
        typedConverters.put("Integer", (node, pt) -> Integer.toString((int) ((AstLiteral) node).getValue()));
        typedConverters.put("Boolean", (node, pt) -> {
            boolean b = (boolean) ((AstLiteral) node).getValue();
            return switch (config.dialect) {
                case SQLite -> b ? "TRUE" : "FALSE";
                case MySQL -> b ? "TRUE" : "FALSE";
                case Oracle -> b ? "1" : "0";
                case PostgreSQL -> b ? "TRUE" : "FALSE";
                case SQLServer -> b ? "1" : "0";
            };
        });
        typedConverters.put("Date", (node, pt) -> {
            String date = (String) ((AstLiteral) node).getValue();
            return switch (config.dialect) {
                case SQLite -> "DATE(" + date + ")";
                case MySQL -> "DATE '" + date + "'";
                case Oracle -> "DATE '" + date + "'";
                case PostgreSQL -> "DATE '" + date + "'";
                case SQLServer -> "CAST('" + date + "' AS DATE)";
            };
        });
        typedConverters.put("arrayExpression", (node, pt) -> {
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
        typedConverters.put("inListOperands", (node, pt) -> array.apply(node));
        typedConverters.put("Geometry", (node, pt) -> {
            Geometry geom = (Geometry) ((AstLiteral) node).getValue();
            return "ST_GeomFromText('" + geom.toText() + "')";
        });

        typedConverters.put("intervalInstance", (node, pt) -> "TSRANGE(" +
                visit(node.getArgs().getFirst(), node.getType()) +
                ", " +
                visit(node.getArgs().getLast(), node.getType()) +
                ", '[]')");

        typedConverters.put("Timestamp", (node, pt) -> "'" + ((AstLiteral) node).getValue() + "'");
        typedConverters.put("BBox", (node, pt) -> {
            List<Double> bbox = (List<Double>) ((AstLiteral) node).getValue();
            double minx = bbox.get(0);
            double miny = bbox.get(1);
            double maxx = bbox.get(2);
            double maxy = bbox.get(3);
            return String.format("ST_GeomFromText('POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))')",
                    minx, miny,
                    maxx, miny,
                    maxx, maxy,
                    minx, maxy,
                    minx, miny
            );
        });
    }

    public String visit(AstNode node) {
        return visit(node, null);
    }
    public String visit(AstNode node, String parentType) {
        if (typedConverters.containsKey(node.getType()))
            return typedConverters.get(node.getType()).apply(node, parentType);
        else
            throw new RuntimeException("Unsupported node type: " + node.getType());
    }
}

package ai.flexgalaxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.util.List;

public class AstToJsonNode {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeoJsonWriter writer = new GeoJsonWriter();

    public AstToJsonNode() {
        writer.setEncodeCRS(false);
    }

    public JsonNode visit(AstNode node) {
        if (node.getOp() != null) {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("op", node.getOp());
            ArrayNode arr = objectMapper.createArrayNode();
            node.getArgs().forEach((arg) -> arr.add(visit(arg)));
            obj.set("args", arr);
            return obj;
        }

        if (node.getType().equals("arrayExpression") || node.getType().equals("inListOperands")) {
            ArrayNode arr = objectMapper.createArrayNode();
            node.getArgs().forEach((arg) -> arr.add(visit(arg)));
            return arr;
        }

        if (node.getType().equals("intervalInstance")) {
            ArrayNode arr = objectMapper.createArrayNode();
            node.getArgs().forEach((arg) -> arr.add(visit(arg)));
            ObjectNode obj = objectMapper.createObjectNode();
            obj.set("interval", arr);
            return obj;
        }

        if (node.isLiteral()) {
            AstLiteral literal = (AstLiteral) node;
            return switch (literal.getLiteralType()) {
                case LiteralType.Property ->  {
                    ObjectNode obj = objectMapper.createObjectNode();
                    obj.put("property", (String)literal.getValue());
                    yield obj;
                }
                case LiteralType.Date -> {
                    ObjectNode obj = objectMapper.createObjectNode();
                    obj.put("date", (String)literal.getValue());
                    yield obj;
                }
                case LiteralType.Timestamp -> {
                    ObjectNode obj = objectMapper.createObjectNode();
                    obj.put("timestamp", (String)literal.getValue());
                    yield obj;
                }
                case LiteralType.Geometry -> {
                    ObjectNode obj = objectMapper.createObjectNode();
                    Geometry geom =  (Geometry) literal.getValue();
                    String geojson = writer.write(geom);
                    try {
                        yield objectMapper.readTree(geojson);
                    } catch (JsonProcessingException e) {
                        yield obj;
                    }
                }
                case LiteralType.BBox -> {
                    ArrayNode arr = objectMapper.createArrayNode();
                    List<Double> numbers = (List<Double>)literal.getValue();
                    numbers.forEach(arr::add);
                    ObjectNode obj = objectMapper.createObjectNode();
                    obj.set("bbox", arr);
                    yield obj;
                }
                default -> objectMapper.valueToTree(literal.getValue());
            };
        }

        throw new RuntimeException("unrecognized node type: " + node.getType());
    }
}

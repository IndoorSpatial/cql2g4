package ai.flexgalaxy.cql2.converter;

import ai.flexgalaxy.cql2.ast.AstNode;
import ai.flexgalaxy.cql2.ast.AstNodeType;
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

    public JsonNode convert(AstNode node) {
        if (node.getOp() != null) {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("op", node.getOp());
            ArrayNode arr = objectMapper.createArrayNode();
            node.getArgs().forEach((arg) -> arr.add(convert(arg)));
            obj.set("args", arr);
            return obj;
        }

        if (node.getType() == AstNodeType.ArrayExpression || node.getType() == AstNodeType.InListOperands) {
            ArrayNode arr = objectMapper.createArrayNode();
            node.getArgs().forEach((arg) -> arr.add(convert(arg)));
            return arr;
        }

        if (node.getType() == AstNodeType.IntervalInstance) {
            ArrayNode arr = objectMapper.createArrayNode();
            node.getArgs().forEach((arg) -> arr.add(convert(arg)));
            ObjectNode obj = objectMapper.createObjectNode();
            obj.set("interval", arr);
            return obj;
        }

        if (node.isLiteral()) {
            return switch (node.getType()) {
                case AstNodeType.PropertyLiteral ->  {
                    ObjectNode obj = objectMapper.createObjectNode();
                    obj.put("property", node.<String>getValue());
                    yield obj;
                }
                case AstNodeType.DateLiteral -> {
                    ObjectNode obj = objectMapper.createObjectNode();
                    obj.put("date", node.<String>getValue());
                    yield obj;
                }
                case AstNodeType.TimestampLiteral -> {
                    ObjectNode obj = objectMapper.createObjectNode();
                    obj.put("timestamp", node.<String>getValue());
                    yield obj;
                }
                case AstNodeType.GeometryLiteral -> {
                    ObjectNode obj = objectMapper.createObjectNode();
                    Geometry geom = node.getValue();
                    String geojson = writer.write(geom);
                    try {
                        yield objectMapper.readTree(geojson);
                    } catch (JsonProcessingException e) {
                        yield obj;
                    }
                }
                case AstNodeType.BBoxLiteral -> {
                    ArrayNode arr = objectMapper.createArrayNode();
                    List<Double> numbers = node.getValue();
                    numbers.forEach(arr::add);
                    ObjectNode obj = objectMapper.createObjectNode();
                    obj.set("bbox", arr);
                    yield obj;
                }
                default -> objectMapper.valueToTree(node.getValue());
            };
        }

        throw new RuntimeException("unrecognized node type: " + node.getType());
    }
}

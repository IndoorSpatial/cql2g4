package ai.flexgalaxy.cql2.converter;

import ai.flexgalaxy.cql2.ast.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static ai.flexgalaxy.cql2.ast.AstNodeType.*;

public class JsonNodeToAST {
    private final Op2Type op2Type = new Op2Type();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeoJsonReader reader = new GeoJsonReader();

    public AstNode convert(JsonNode node) {
        return convert(node, null);
    }

    public AstNode convert(JsonNode node, String upperOp) {
        if (node.isObject()) {
            if (node.has("op") && node.has("args") && node.get("args").isArray()) {
                List<AstNode> list = new LinkedList<>();
                node.get("args").forEach(arg -> list.add(convert(arg, node.get("op").asText())));
                return new AstNode(node.get("op").asText(), op2Type.type(node.get("op").asText()), list);
            }

            if (node.has("interval"))
                return new AstNode(null, IntervalInstance, readInterval(node));

            if (node.has("type") && node.has("coordinates"))
                return new AstNode(GeometryLiteral, readGeoJson(node));

            if (node.has("type") && node.has("geometries"))
                return new AstNode(GeometryLiteral, readGeoJson(node));

            if (node.has("bbox"))
                return new AstNode(BBoxLiteral, readBbox(node));

            if (node.has("timestamp"))
                return new AstNode(TimestampLiteral, readTimestamp(node));

            if (node.has("date"))
                return new AstNode(DateLiteral, readDate(node));

            if (node.has("property"))
                return new AstNode(PropertyLiteral, readProperty(node));
        }
        if (node.isArray()) {
            List<AstNode> list = new LinkedList<>();
            node.forEach(item -> list.add(convert(item)));
            if (Objects.equals(upperOp, "in"))
                return new AstNode(null, InListOperands, list);
            else
                return new AstNode(null, ArrayExpression, list);
        }

        if (node.isTextual())
            return new AstNode(StringLiteral, node.asText());

        if (node.isBoolean())
            return new AstNode(BooleanLiteral, node.asBoolean());

        if (node.isIntegralNumber())
            return new AstNode(IntegerLiteral, node.asInt());

        if (node.isNumber())
            return new AstNode(DoubleLiteral, node.asDouble());

        throw new RuntimeException("unsupported type: " + node.getNodeType());
    }

    private Geometry readGeoJson(JsonNode node) {
        try {
            String geojson = objectMapper.writeValueAsString(node);
            System.out.println(geojson);
            var geom = reader.read(geojson);
            System.out.println(geom.toText());
            return geom;
        } catch (JsonProcessingException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private List<Double> readBbox(JsonNode node) {
        if (node.get("bbox").isArray() && (node.get("bbox").size() == 4 || node.get("bbox").size() == 6)) {
            List<Double> list = new LinkedList<>();
            node.get("bbox").forEach(coor -> list.add(coor.asDouble()));
            return list;
        } else {
            throw new RuntimeException("bbox is not valid");
        }
    }

    private String readTimestamp(JsonNode node) {
        return node.get("timestamp").asText();
    }

    private String readDate(JsonNode node) {
        return node.get("date").asText();
    }

    private List<AstNode> readInterval(JsonNode node) {
        if (node.get("interval").isArray() && node.get("interval").size() == 2) {
            List<AstNode> list = new LinkedList<>();
            node.get("interval").forEach(value -> list.add(convert(value)));
            return list;
        } else {
            throw new RuntimeException("interval is not valid");
        }
    }

    private String readProperty(JsonNode node) {
        return node.get("property").asText();
    }
}

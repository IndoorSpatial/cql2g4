package ai.flexgalaxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class JsonNodeToAST {
    private HashMap<String, String> op2Type = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private GeoJsonReader reader = new GeoJsonReader();

    public JsonNodeToAST() {
        op2Type.put("or", "booleanExpression");
        op2Type.put("and", "booleanTerm");
        op2Type.put("not", "booleanFactor");
        op2Type.put("=", "binaryComparisonPredicate");
    }

    public List<AstNode> Args(JsonNode args) {
        if (!args.isArray())
            return null;
        List<AstNode> list = new LinkedList<>();
        for (JsonNode node : args)
            list.add(visit(node));
        return list;
    }

    public AstNode expr(String op, JsonNode args) {
        return new AstNode(op, op2Type.get(op), Args(args));
    }

    public Geometry readGeoJson(JsonNode node) {
        try {
            return reader.read(objectMapper.writeValueAsString(node));
        } catch (JsonProcessingException | ParseException e) {
            return null;
        }
    }

    public AstNode readBbox(JsonNode node) {
        return null;
    }

    public AstNode readTimestamp(JsonNode node) {
        return null;
    }

    public AstNode readDate(JsonNode node) {
        return null;
    }

    public AstNode readInterval(JsonNode node) {
        return null;
    }

    public String readProperty(JsonNode node) {
        return node.get("property").asText();
    }

    public AstNode visit(JsonNode node) {
        if (node.isObject()) {
            if (node.has("op") && node.has("args") && node.get("args").isArray())
                return expr(node.get("op").asText(), node.get("args"));
            if (node.has("type") && node.has("geometry"))
                return new AstLiteral(LiteralType.Geometry, readGeoJson(node));
            if (node.has("bbox"))
                return new AstLiteral(LiteralType.BBox, readBbox(node));
            if (node.has("timestamp"))
                return new AstLiteral(LiteralType.Timestamp, readTimestamp(node));
            if (node.has("date"))
                return new AstLiteral(LiteralType.Date, readDate(node));
            if (node.has("interval"))
                return new AstLiteral(LiteralType.Interval, readInterval(node));
            if (node.has("property"))
                return new AstLiteral(LiteralType.Property, readProperty(node));
        }
//        if (node.isArray()) {
//            // TODO:
//        }

        if (node.isTextual())
            return new AstLiteral(LiteralType.String, node.asText());

        if (node.isBoolean())
            return new AstLiteral(LiteralType.Boolean, node.asBoolean());

        if (node.isIntegralNumber())
            return new AstLiteral(LiteralType.Integer, node.asInt());

        if (node.isNumber())
            return new AstLiteral(LiteralType.Double, node.asDouble());

        return null;
    }
}

package ai.flexgalaxy.test;

import ai.flexgalaxy.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import static org.junit.jupiter.api.Assertions.*;

class JsonNodeToASTTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    public JsonNodeToASTTest() {
        objectMapper.setDefaultPropertyInclusion(
                JsonInclude.Include.NON_NULL
        );
        SimpleModule module = new SimpleModule();
        module.addSerializer(Geometry.class, new CustomGeometrySerializer());
        objectMapper.registerModule(module);
    }

    @Test
    void visit() throws JsonProcessingException {
        JsonNode json = objectMapper.readTree("{\n" +
                "  \"op\": \"t_before\",\n" +
                "  \"args\": [\n" +
                "    { \"property\": \"updated_at\" },\n" +
                "    { \"timestamp\": \"2012-08-10T05:30:00Z\" }\n" +
                "  ]\n" +
                "}\n");

        JsonNodeToAST converter =  new JsonNodeToAST();
        AstNode astNode = converter.visit(json);

        System.out.println(json);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(astNode));
    }
}
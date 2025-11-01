package ai.flexgalaxy.cql2.converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.NumericNode;

import org.locationtech.jts.geom.Geometry;

import ai.flexgalaxy.cql2.ast.AstNode;

class JsonNodeToASTTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    final JsonNodeToAST toAst = new JsonNodeToAST();
    final AstToJsonNode toJson = new AstToJsonNode();

    public JsonNodeToASTTest() {
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Geometry.class, new CustomGeometrySerializer());
        objectMapper.registerModule(module);
    }

    static Stream<String> testFiles() {
        String jsonPrefix = "schema/1.0/examples/json/";
        File dir = new File(jsonPrefix);
        File[] files = dir.listFiles((f, name) -> name.endsWith(".json"));
        assertNotNull(files);
        return Arrays.stream(files).map(f -> jsonPrefix + f.getName());
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    void testAllFiles(String filename) throws IOException {
        // read json
        String originJsonContent = Files.readString(Paths.get(filename), java.nio.charset.StandardCharsets.UTF_8);
        JsonNode originJson = objectMapper.readTree(originJsonContent);
        System.out.println(objectMapper.writeValueAsString(originJson));

        // convert to AST
        AstNode astNode = toAst.convert(originJson);
        System.out.println(astNode.ToString());

        // convert back
        JsonNode convertedJson = toJson.convert(astNode);
        System.out.println(objectMapper.writeValueAsString(convertedJson));

        assertTrue(originJson.equals((lhs, rhs) -> {
            if (lhs.equals(rhs))
                return 0;
            if ((lhs instanceof NumericNode) && (rhs instanceof NumericNode)) {
                Double ld = lhs.asDouble();
                Double rd = rhs.asDouble();
                return ld.compareTo(rd);
            }
            return 1;
        }, convertedJson));
    }
}
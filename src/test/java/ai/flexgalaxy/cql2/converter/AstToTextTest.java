package ai.flexgalaxy.cql2.converter;

import ai.flexgalaxy.cql2.Cql2Lexer;
import ai.flexgalaxy.cql2.Cql2Parser;
import ai.flexgalaxy.cql2.ast.AstNode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.NumericNode;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.locationtech.jts.geom.Geometry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AstToTextTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AstToTextTest() {
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
        // read origin json
        String originJsonContent = Files.readString(Paths.get(filename), java.nio.charset.StandardCharsets.UTF_8);
        JsonNode originJsonNode = objectMapper.readTree(originJsonContent);
        System.out.println(originJsonContent);

        // convert to ast
        JsonNodeToAST toAst = new JsonNodeToAST();
        AstNode astNode = toAst.convert(originJsonNode);
        System.out.println(astNode.ToString());

        // convert to text
        AstToText toText = new AstToText();
        String cql2Text = toText.convert(astNode);
        System.out.println(cql2Text);

        // parse
        Cql2Lexer lexer = new Cql2Lexer(CharStreams.fromString(cql2Text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Cql2Parser parser = new Cql2Parser(tokens);
        ParseTree tree = parser.booleanExpression();

        // convert to json
        ParseTreeToJsonNode toJsonNode = new ParseTreeToJsonNode(tokens);
        JsonNode convertJsonResult = toJsonNode.visit(tree);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(convertJsonResult));

        assertTrue(convertJsonResult.equals((lhs, rhs) -> {
            if (lhs.equals(rhs))
                return 0;
            if ((lhs instanceof NumericNode) && (rhs instanceof NumericNode)) {
                Double ld = lhs.asDouble();
                Double rd = rhs.asDouble();
                return ld.compareTo(rd);
            }
            return 1;
        }, originJsonNode));
    }
}
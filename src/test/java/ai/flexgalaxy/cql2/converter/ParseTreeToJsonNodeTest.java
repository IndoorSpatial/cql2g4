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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NumericNode;

import ai.flexgalaxy.Cql2g4.Cql2Lexer;
import ai.flexgalaxy.Cql2g4.Cql2Parser;

public class ParseTreeToJsonNodeTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String textPrefix = "schema/1.0/examples/text/";
    private static final String jsonPrefix = "schema/1.0/examples/json/";

    static Stream<String> testFiles() {
        File dir = new File(textPrefix);
        File[] files = dir.listFiles((f, name) -> name.endsWith(".txt"));
        assertNotNull(files);
        return Arrays.stream(files)
                .map(File::getName)
                .filter(name -> Files.exists(Paths.get(jsonPrefix + name.replace(".txt", ".json"))));
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    void testAllFiles(String filename) throws IOException {
        // read text query
        String contentText = Files.readString(Paths.get(textPrefix + filename),
                java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(contentText);

        // parse
        Cql2Lexer lexer = new Cql2Lexer(CharStreams.fromString(contentText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Cql2Parser parser = new Cql2Parser(tokens);
        ParseTree tree = parser.booleanExpression();
        System.out.println(tree.toStringTree(parser));

        // convert to json
        ParseTreeToJsonNode toJsonNode = new ParseTreeToJsonNode(tokens);
        JsonNode convertJsonResult = toJsonNode.visit(tree);
        System.out.println(objectMapper.writeValueAsString(convertJsonResult));

        // read expect json
        String contentJson = Files.readString(Paths.get(jsonPrefix + filename.replace(".txt", ".json")),
                java.nio.charset.StandardCharsets.UTF_8);
        JsonNode expectJsonNode = objectMapper.readTree(contentJson);
        System.out.println(objectMapper.writeValueAsString(expectJsonNode));

        // compare them
        assertTrue(convertJsonResult.equals((lhs, rhs) -> {
            if (lhs.equals(rhs))
                return 0;
            if ((lhs instanceof NumericNode) && (rhs instanceof NumericNode)) {
                Double ld = lhs.asDouble();
                Double rd = rhs.asDouble();
                return ld.compareTo(rd);
            }
            return 1;
        }, expectJsonNode));
    }
}

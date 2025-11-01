package ai.flexgalaxy.cql2.converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.flexgalaxy.cql2.Cql2Lexer;
import ai.flexgalaxy.cql2.Cql2Parser;
import ai.flexgalaxy.cql2.ast.AstNode;

class ParseTreeToAstTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String textPrefix = "schema/1.0/examples/text/";
    private static final String jsonPrefix = "schema/1.0/examples/json/";
    private static final String projectRoot = System.getProperty("user.dir");

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
        String contentText = Files.readString(Paths.get(textPrefix + filename), java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(contentText);

        // parse
        Cql2Lexer lexer = new Cql2Lexer(CharStreams.fromString(contentText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Cql2Parser parser = new Cql2Parser(tokens);
        ParseTree tree = parser.booleanExpression();

        // convert to AST
        ParseTreeToAst treeToAst = new ParseTreeToAst(tokens);
        AstNode astNodeFromText = treeToAst.visit(tree);
        System.out.println(astNodeFromText.ToString());

        // read expect json
        String contentJson = Files.readString(Paths.get(jsonPrefix + filename.replace(".txt", ".json")),
                java.nio.charset.StandardCharsets.UTF_8);
        JsonNode expectJsonNode = objectMapper.readTree(contentJson);

        JsonNodeToAST jsonToAst = new JsonNodeToAST();
        AstNode astNodeFromJson = jsonToAst.convert(expectJsonNode);

        assertEquals(astNodeFromJson.ToString(), astNodeFromText.ToString());
    }
}
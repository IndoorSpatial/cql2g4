package ai.flexgalaxy.cql2.converter;

import ai.flexgalaxy.Cql2g4.Cql2Lexer;
import ai.flexgalaxy.Cql2g4.Cql2Parser;
import ai.flexgalaxy.cql2.ast.AstNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NumericNode;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ParseTreeToAstTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String textPrefix = "schema/1.0/examples/text/";
    private final String jsonPrefix = "schema/1.0/examples/json/";
    private final String projectRoot = System.getProperty("user.dir");

    public void convert() {
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName().replace("_alt", "-alt");

        // assert json exist
        Path pathJson = Paths.get(projectRoot, jsonPrefix + testName + ".json");
        assertTrue(Files.exists(pathJson), "json file not found: " + pathJson);

        try {
            // read text query
            Path pathTxt = Paths.get(projectRoot, textPrefix + testName + ".txt");
            String contentText = Files.readString(pathTxt, java.nio.charset.StandardCharsets.UTF_8);
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
            String contentJson = Files.readString(pathJson, java.nio.charset.StandardCharsets.UTF_8);
            JsonNode expectJsonNode = objectMapper.readTree(contentJson);

            JsonNodeToAST jsonToAst = new JsonNodeToAST();
            AstNode astNodeFromJson = jsonToAst.convert(expectJsonNode);

            assertEquals(astNodeFromJson.ToString(), astNodeFromText.ToString());

        } catch (IOException e) {
            fail();
        }
    }

    // @formatter:off
    @Test public void clause6_01() { convert(); }
    @Test public void clause6_02a() { convert(); }
    @Test public void clause6_02b() { convert(); }
    @Test public void clause6_02c() { convert(); }
    @Test public void clause6_02d() { convert(); }
    @Test public void clause6_03() { convert(); }
    @Test public void clause7_01() { convert(); }
    @Test public void clause7_02() { convert(); }
    @Test public void clause7_03a() { convert(); }
    @Test public void clause7_03b() { convert(); }
    @Test public void clause7_04() { convert(); }
    @Test public void clause7_05() { convert(); }
    @Test public void clause7_07() { convert(); }
    @Test public void clause7_10() { convert(); }
    @Test public void clause7_12() { convert(); }
    @Test public void clause7_13() { convert(); }
    @Test public void clause7_15() { convert(); }
    @Test public void clause7_16() { convert(); }
    @Test public void clause7_17() { convert(); }
    @Test public void clause7_18() { convert(); }
    @Test public void clause7_19() { convert(); }
    @Test public void example01() { convert(); }
    @Test public void example02() { convert(); }
    @Test public void example03() { convert(); }
    @Test public void example04() { convert(); }
    @Test public void example05a() { convert(); }
    @Test public void example05b() { convert(); }
    @Test public void example06a() { convert(); }
    @Test public void example06b() { convert(); }
    @Test public void example07() { convert(); }
    @Test public void example08() { convert(); }
    @Test public void example09() { convert(); }
    @Test public void example10() { convert(); }
    @Test public void example11() { convert(); }
    @Test public void example12() { convert(); }
    @Test public void example13() { convert(); }
    @Test public void example14() { convert(); }
    @Test public void example15() { convert(); }
    @Test public void example16() { convert(); }
    @Test public void example17() { convert(); }
    @Test public void example18() { convert(); }
    @Test public void example19() { convert(); }
    @Test public void example20() { convert(); }
    @Test public void example21() { convert(); }
    @Test public void example22() { convert(); }
    @Test public void example23() { convert(); }
    @Test public void example24() { convert(); }
    @Test public void example25() { convert(); }
    @Test public void example26() { convert(); }
    @Test public void example27() { convert(); }
    @Test public void example28() { convert(); }
    @Test public void example29() { convert(); }
    @Test public void example30() { convert(); }
    @Test public void example31() { convert(); }
    @Test public void example32() { convert(); }
    @Test public void example33() { convert(); }
    @Test public void example34() { convert(); }
    @Test public void example35() { convert(); }
    //    @Test public void example36_alt01() { convert(); }  // no related json file
    @Test public void example36() { convert(); }
    @Test public void example37() { convert(); }
    //    @Test public void example38_alt01() { convert(); }  // no related json file
    @Test public void example38() { convert(); }
    @Test public void example39() { convert(); }
    //    @Test public void example40_alt01() { convert(); }  // no related json file
    @Test public void example40() { convert(); }
    @Test public void example41() { convert(); }
    //    @Test public void example42_alt01() { convert(); }  // no related json file
    @Test public void example42() { convert(); }
    //    @Test public void example43_alt01() { convert(); }  // no related json file
    @Test public void example43() { convert(); }
    //    @Test public void example44_alt01() { convert(); }  // no related json file
    @Test public void example44() { convert(); }
    @Test public void example45() { convert(); }
    //    @Test public void example46_alt01() { convert(); }  // no related json file
    @Test public void example46() { convert(); }
    @Test public void example47() { convert(); }
    @Test public void example48() { convert(); }
    //    @Test public void example49_alt01() { convert(); }  // no related json file
    @Test public void example49() { convert(); }
    @Test public void example50() { convert(); }
    @Test public void example51() { convert(); }
    @Test public void example52() { convert(); }
    @Test public void example53() { convert(); }
    //    @Test public void example54_alt01() { convert(); }
    @Test public void example54() { convert(); }
    //    @Test public void example55_alt01() { convert(); }  // no related json file
    @Test public void example55() { convert(); }
    @Test public void example56() { convert(); }
    @Test public void example57() { convert(); }
    @Test public void example58() { convert(); }
    @Test public void example59() { convert(); }
    @Test public void example60() { convert(); }
    @Test public void example61() { convert(); }
    @Test public void example62() { convert(); }
    @Test public void example63() { convert(); }
    @Test public void example64() { convert(); }
    @Test public void example65() { convert(); }
    @Test public void example66() { convert(); }
    @Test public void example67() { convert(); }
    @Test public void example68() { convert(); }
    @Test public void example69() { convert(); }
    @Test public void example70() { convert(); }
    @Test public void example71() { convert(); }
    @Test public void example72() { convert(); }
    @Test public void example73() { convert(); }
    @Test public void example74() { convert(); }
    @Test public void example75() { convert(); }
    @Test public void example76() { convert(); }
    @Test public void example77() { convert(); }
    @Test public void example78() { convert(); }
    @Test public void example79() { convert(); }
    @Test public void example80() { convert(); }
    @Test public void example81() { convert(); }
    @Test public void example82() { convert(); }
    @Test public void example83() { convert(); }
    @Test public void example84() { convert(); }
    //    @Test public void example85_alt01() { convert(); }  // no related json file
    @Test public void example85() { convert(); }
    @Test public void example86() { convert(); }
}
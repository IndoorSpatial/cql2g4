package ai.flexgalaxy.test;

import ai.flexgalaxy.Cql2g4.Cql2Lexer;
import ai.flexgalaxy.Cql2g4.Cql2Parser;
import ai.flexgalaxy.cql2.converter.ParseTreeToJsonNode;
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

public class JsonConverterVisitorTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String textPrefix = "schema/1.0/examples/text/";
    private final String jsonPrefix = "schema/1.0/examples/json/";
    private final String projectRoot = System.getProperty("user.dir");

    public JsonConverterVisitorTest() {
    }

    public void convertTest() {
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
            System.out.println(tree.toStringTree(parser));

            // convert to json
            ParseTreeToJsonNode visitor = new ParseTreeToJsonNode(tokens);
            JsonNode convertJsonResult = visitor.visit(tree);

            // read expect json
            String contentJson = Files.readString(pathJson, java.nio.charset.StandardCharsets.UTF_8);
            JsonNode expectJsonNode = objectMapper.readTree(contentJson);

            System.out.println(objectMapper.writeValueAsString(expectJsonNode));
            System.out.println(objectMapper.writeValueAsString(convertJsonResult));
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
        } catch (IOException e) {
            fail();
        }
    }

    // @formatter:off
    @Test public void clause6_01() { convertTest(); }
    @Test public void clause6_02a() { convertTest(); }
    @Test public void clause6_02b() { convertTest(); }
    @Test public void clause6_02c() { convertTest(); }
    @Test public void clause6_02d() { convertTest(); }
    @Test public void clause6_03() { convertTest(); }
    @Test public void clause7_01() { convertTest(); }
    @Test public void clause7_02() { convertTest(); }
    @Test public void clause7_03a() { convertTest(); }
    @Test public void clause7_03b() { convertTest(); }
    @Test public void clause7_04() { convertTest(); }
    @Test public void clause7_05() { convertTest(); }
    @Test public void clause7_07() { convertTest(); }
    @Test public void clause7_10() { convertTest(); }
    @Test public void clause7_12() { convertTest(); }
    @Test public void clause7_13() { convertTest(); }
    @Test public void clause7_15() { convertTest(); }
    @Test public void clause7_16() { convertTest(); }
    @Test public void clause7_17() { convertTest(); }
    @Test public void clause7_18() { convertTest(); }
    @Test public void clause7_19() { convertTest(); }
    @Test public void example01() { convertTest(); }
    @Test public void example02() { convertTest(); }
    @Test public void example03() { convertTest(); }
    @Test public void example04() { convertTest(); }
    @Test public void example05a() { convertTest(); }
    @Test public void example05b() { convertTest(); }
    @Test public void example06a() { convertTest(); }
    @Test public void example06b() { convertTest(); }
    @Test public void example07() { convertTest(); }
    @Test public void example08() { convertTest(); }
    @Test public void example09() { convertTest(); }
    @Test public void example10() { convertTest(); }
    @Test public void example11() { convertTest(); }
    @Test public void example12() { convertTest(); }
    @Test public void example13() { convertTest(); }
    @Test public void example14() { convertTest(); }
    @Test public void example15() { convertTest(); }
    @Test public void example16() { convertTest(); }
    @Test public void example17() { convertTest(); }
    @Test public void example18() { convertTest(); }
    @Test public void example19() { convertTest(); }
    @Test public void example20() { convertTest(); }
    @Test public void example21() { convertTest(); }
    @Test public void example22() { convertTest(); }
    @Test public void example23() { convertTest(); }
    @Test public void example24() { convertTest(); }
    @Test public void example25() { convertTest(); }
    @Test public void example26() { convertTest(); }
    @Test public void example27() { convertTest(); }
    @Test public void example28() { convertTest(); }
    @Test public void example29() { convertTest(); }
    @Test public void example30() { convertTest(); }
    @Test public void example31() { convertTest(); }
    @Test public void example32() { convertTest(); }
    @Test public void example33() { convertTest(); }
    @Test public void example34() { convertTest(); }
    @Test public void example35() { convertTest(); }
//    @Test public void example36_alt01() { convertTest(); }
    @Test public void example36() { convertTest(); }
    @Test public void example37() { convertTest(); }
//    @Test public void example38_alt01() { convertTest(); }
    @Test public void example38() { convertTest(); }
    @Test public void example39() { convertTest(); }
//    @Test public void example40_alt01() { convertTest(); }
    @Test public void example40() { convertTest(); }
    @Test public void example41() { convertTest(); }
//    @Test public void example42_alt01() { convertTest(); }
    @Test public void example42() { convertTest(); }
//    @Test public void example43_alt01() { convertTest(); }
    @Test public void example43() { convertTest(); }
//    @Test public void example44_alt01() { convertTest(); }
    @Test public void example44() { convertTest(); }
    @Test public void example45() { convertTest(); }
//    @Test public void example46_alt01() { convertTest(); }
    @Test public void example46() { convertTest(); }
    @Test public void example47() { convertTest(); }
    @Test public void example48() { convertTest(); }
//    @Test public void example49_alt01() { convertTest(); }
    @Test public void example49() { convertTest(); }
    @Test public void example50() { convertTest(); }
    @Test public void example51() { convertTest(); }
    @Test public void example52() { convertTest(); }
    @Test public void example53() { convertTest(); }
//    @Test public void example54_alt01() { convertTest(); }
    @Test public void example54() { convertTest(); }
//    @Test public void example55_alt01() { convertTest(); }
    @Test public void example55() { convertTest(); }
    @Test public void example56() { convertTest(); }
    @Test public void example57() { convertTest(); }
    @Test public void example58() { convertTest(); }
    @Test public void example59() { convertTest(); }
    @Test public void example60() { convertTest(); }
    @Test public void example61() { convertTest(); }
    @Test public void example62() { convertTest(); }
    @Test public void example63() { convertTest(); }
    @Test public void example64() { convertTest(); }
    @Test public void example65() { convertTest(); }
    @Test public void example66() { convertTest(); }
    @Test public void example67() { convertTest(); }
    @Test public void example68() { convertTest(); }
    @Test public void example69() { convertTest(); }
    @Test public void example70() { convertTest(); }
    @Test public void example71() { convertTest(); }
    @Test public void example72() { convertTest(); }
    @Test public void example73() { convertTest(); }
    @Test public void example74() { convertTest(); }
    @Test public void example75() { convertTest(); }
    @Test public void example76() { convertTest(); }
    @Test public void example77() { convertTest(); }
    @Test public void example78() { convertTest(); }
    @Test public void example79() { convertTest(); }
    @Test public void example80() { convertTest(); }
    @Test public void example81() { convertTest(); }
    @Test public void example82() { convertTest(); }
    @Test public void example83() { convertTest(); }
    @Test public void example84() { convertTest(); }
//    @Test public void example85_alt01() { convertTest(); }
    @Test public void example85() { convertTest(); }
    @Test public void example86() { convertTest(); }
}

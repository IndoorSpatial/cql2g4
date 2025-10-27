package ai.flexgalaxy.test;

import ai.flexgalaxy.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class JsonNodeToASTTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String jsonPrefix = "schema/1.0/examples/json/";
    private final String projectRoot = System.getProperty("user.dir");
    JsonNodeToAST converter = new JsonNodeToAST();

    public JsonNodeToASTTest() {
        objectMapper.setDefaultPropertyInclusion(
                JsonInclude.Include.NON_NULL
        );
        SimpleModule module = new SimpleModule();
        module.addSerializer(Geometry.class, new CustomGeometrySerializer());
        objectMapper.registerModule(module);
    }

    void visit() {
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        Path pathJson = Paths.get(projectRoot, jsonPrefix + testName + ".json");

        try {
            String contentJson = Files.readString(pathJson, java.nio.charset.StandardCharsets.UTF_8);
            JsonNode json = objectMapper.readTree(contentJson);
            System.out.println(objectMapper.writeValueAsString(json));

            AstNode astNode = converter.visit(json);
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(astNode));
        } catch (IOException e) {
            fail();
        }
    }

    @Test public void clause6_01() { visit(); }
    @Test public void clause6_02a() { visit(); }
    @Test public void clause6_02b() { visit(); }
    @Test public void clause6_02c() { visit(); }
    @Test public void clause6_02d() { visit(); }
    @Test public void clause6_03() { visit(); }
    @Test public void clause7_01() { visit(); }
    @Test public void clause7_02() { visit(); }
    @Test public void clause7_03a() { visit(); }
    @Test public void clause7_03b() { visit(); }
    @Test public void clause7_04() { visit(); }
    @Test public void clause7_05() { visit(); }
    @Test public void clause7_07() { visit(); }
    @Test public void clause7_10() { visit(); }
    @Test public void clause7_12() { visit(); }
    @Test public void clause7_13() { visit(); }
    @Test public void clause7_15() { visit(); }
    @Test public void clause7_16() { visit(); }
    @Test public void clause7_17() { visit(); }
    @Test public void clause7_18() { visit(); }
    @Test public void clause7_19() { visit(); }
    @Test public void example01() { visit(); }
    @Test public void example02() { visit(); }
    @Test public void example03() { visit(); }
    @Test public void example04() { visit(); }
    @Test public void example05a() { visit(); }
    @Test public void example05b() { visit(); }
    @Test public void example06a() { visit(); }
    @Test public void example06b() { visit(); }
    @Test public void example07() { visit(); }
    @Test public void example08() { visit(); }
    @Test public void example09() { visit(); }
    @Test public void example10() { visit(); }
    @Test public void example11() { visit(); }
    @Test public void example12() { visit(); }
    @Test public void example13() { visit(); }
    @Test public void example14() { visit(); }
    @Test public void example15() { visit(); }
    @Test public void example16() { visit(); }
    @Test public void example17() { visit(); }
    @Test public void example18() { visit(); }
    @Test public void example19() { visit(); }
    @Test public void example20() { visit(); }
    @Test public void example21() { visit(); }
    @Test public void example22() { visit(); }
    @Test public void example23() { visit(); }
    @Test public void example24() { visit(); }
    @Test public void example25() { visit(); }
    @Test public void example26() { visit(); }
    @Test public void example27() { visit(); }
    @Test public void example28() { visit(); }
    @Test public void example29() { visit(); }
    @Test public void example30() { visit(); }
    @Test public void example31() { visit(); }
    @Test public void example32() { visit(); }
    @Test public void example33() { visit(); }
    @Test public void example34() { visit(); }
    @Test public void example35() { visit(); }
    @Test public void example36() { visit(); }
    @Test public void example37() { visit(); }
    @Test public void example38() { visit(); }
    @Test public void example39() { visit(); }
    @Test public void example40() { visit(); }
    @Test public void example41() { visit(); }
    @Test public void example42() { visit(); }
    @Test public void example43() { visit(); }
    @Test public void example44() { visit(); }
    @Test public void example45() { visit(); }
    @Test public void example46() { visit(); }
    @Test public void example47() { visit(); }
    @Test public void example48() { visit(); }
    @Test public void example49() { visit(); }
    @Test public void example50() { visit(); }
    @Test public void example51() { visit(); }
    @Test public void example52() { visit(); }
    @Test public void example53() { visit(); }
    @Test public void example54() { visit(); }
    @Test public void example55() { visit(); }
    @Test public void example56() { visit(); }
    @Test public void example57() { visit(); }
    @Test public void example58() { visit(); }
    @Test public void example59() { visit(); }
    @Test public void example60() { visit(); }
    @Test public void example61() { visit(); }
    @Test public void example62() { visit(); }
    @Test public void example63() { visit(); }
    @Test public void example64() { visit(); }
    @Test public void example65() { visit(); }
    @Test public void example66() { visit(); }
    @Test public void example67() { visit(); }
    @Test public void example68() { visit(); }
    @Test public void example69() { visit(); }
    @Test public void example70() { visit(); }
    @Test public void example71() { visit(); }
    @Test public void example72() { visit(); }
    @Test public void example73() { visit(); }
    @Test public void example74() { visit(); }
    @Test public void example75() { visit(); }
    @Test public void example76() { visit(); }
    @Test public void example77() { visit(); }
    @Test public void example78() { visit(); }
    @Test public void example79() { visit(); }
    @Test public void example80() { visit(); }
    @Test public void example81() { visit(); }
    @Test public void example82() { visit(); }
    @Test public void example83() { visit(); }
    @Test public void example84() { visit(); }
    @Test public void example85() { visit(); }
    @Test public void example86() { visit(); }
}
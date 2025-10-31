package ai.flexgalaxy.test;

import ai.flexgalaxy.cql2.*;
import ai.flexgalaxy.cql2.ast.AstNode;
import ai.flexgalaxy.cql2.converter.sql.AstToSql;
import ai.flexgalaxy.cql2.converter.CustomGeometrySerializer;
import ai.flexgalaxy.cql2.converter.JsonNodeToAST;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.parser.feature.FeatureConfiguration;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

import net.sf.jsqlparser.statement.Statement;

class AstToSqlTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String jsonPrefix = "schema/1.0/examples/json/";
    private final static String projectRoot = System.getProperty("user.dir");
    private final static String resultPrefix = "src/test/results/";

    public AstToSqlTest() {
        objectMapper.setDefaultPropertyInclusion(
                JsonInclude.Include.NON_NULL
        );
        SimpleModule module = new SimpleModule();
        module.addSerializer(Geometry.class, new CustomGeometrySerializer());
        objectMapper.registerModule(module);
    }

    void convert() {
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        Path pathJson = Paths.get(projectRoot, jsonPrefix + testName + ".json");
        try {
            String originJsonContent = Files.readString(pathJson, java.nio.charset.StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(originJsonContent);
            System.out.println(originJsonContent);

            JsonNodeToAST toAst = new JsonNodeToAST();
            AstNode astNode = toAst.convert(node);
            System.out.println(astNode.ToString());


            for (SqlDialect dialect : SqlDialect.values()) {
                if (dialect != SqlDialect.PostgreSQL) continue;
                AstToSql toSql = new AstToSql();
                String sqlWhere = toSql.convert(astNode);
                String selectStr = "SELECT * FROM t WHERE " + sqlWhere + ";";
                System.out.println(dialect + ": " +  selectStr);
                Files.writeString(
                        Paths.get(projectRoot, resultPrefix + dialect + ".sql"),
                        selectStr + " -- " + testName + "\n",
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE
                );

                FeatureConfiguration fconfig = new FeatureConfiguration();
                fconfig.setValue(Feature.allowSquareBracketQuotation, false);
                CCJSqlParser parser = new CCJSqlParser(selectStr);
                parser.withConfiguration(fconfig);
                Statement statement = parser.Statement();

                PlainSelect select = (PlainSelect)((Select)statement).getSelectBody();
                PrintSqlAst.printExpression(select.getWhere(), 0);
            }
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    // @formatter:off
//    @Test public void clause6_01() { convert(); }  // this example is not a boolean expression
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
    @Test public void example36() { convert(); }
    @Test public void example37() { convert(); }
    @Test public void example38() { convert(); }
    @Test public void example39() { convert(); }
    @Test public void example40() { convert(); }
    @Test public void example41() { convert(); }
    @Test public void example42() { convert(); }
    @Test public void example43() { convert(); }
    @Test public void example44() { convert(); }
    @Test public void example45() { convert(); }
    @Test public void example46() { convert(); }
    @Test public void example47() { convert(); }
    @Test public void example48() { convert(); }
    @Test public void example49() { convert(); }
    @Test public void example50() { convert(); }
    @Test public void example51() { convert(); }
    @Test public void example52() { convert(); }
    @Test public void example53() { convert(); }
    @Test public void example54() { convert(); }
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
//    @Test public void example81() { convert(); }  // don't support array with different type
//    @Test public void example82() { convert(); }  // don't support array with different type
    @Test public void example83() { convert(); }
    @Test public void example84() { convert(); }
    @Test public void example85() { convert(); }
    @Test public void example86() { convert(); }
}
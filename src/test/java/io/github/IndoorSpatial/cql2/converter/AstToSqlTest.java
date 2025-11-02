package io.github.IndoorSpatial.cql2.converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Stream;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.locationtech.jts.geom.Geometry;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import net.sf.jsqlparser.statement.Statement;

import io.github.IndoorSpatial.cql2.ast.AstNode;
import io.github.IndoorSpatial.cql2.converter.sql.AstToSql;
import io.github.IndoorSpatial.cql2.SqlDialect;

class AstToSqlTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String projectRoot = System.getProperty("user.dir");
    private final static String resultPrefix = "src/test/";

    static Stream<String> testFiles() {
        String jsonPrefix = "schema/1.0/examples/json/";
        File dir = new File(jsonPrefix);
        File[] files = dir.listFiles((f, name) -> name.endsWith(".json"));
        assertNotNull(files);
        return Arrays.stream(files).map(f -> jsonPrefix + f.getName());
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    void testAllFiles(String filename) throws IOException, ParseException {
        // read origin json
        String originJsonContent = Files.readString(Paths.get(filename), java.nio.charset.StandardCharsets.UTF_8);
        JsonNode node = objectMapper.readTree(originJsonContent);
        System.out.println(originJsonContent);

        // convert to AST
        JsonNodeToAST toAst = new JsonNodeToAST();
        AstNode astNode = toAst.convert(node);
        System.out.println(astNode.ToString());

        // convert to SQL
        SqlDialect dialect = SqlDialect.PostgreSQL;
        AstToSql toSql = new AstToSql();
        String sqlWhere = toSql.convert(astNode);
        String selectStr = "SELECT * FROM t WHERE " + sqlWhere + ";";
        Files.writeString(
                Paths.get(projectRoot, resultPrefix + dialect + ".sql"),
                selectStr + " -- " + filename + "\n",
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE
        );

        // parse SQL
        FeatureConfiguration fconfig = new FeatureConfiguration();
        fconfig.setValue(Feature.allowSquareBracketQuotation, false);
        CCJSqlParser parser = new CCJSqlParser(selectStr);
        parser.withConfiguration(fconfig);
        Statement statement = parser.Statement();
        PlainSelect select = (PlainSelect) ((Select) statement).getSelectBody();
        PrintSqlAst.printExpression(select.getWhere(), 0);
    }
}
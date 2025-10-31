package ai.flexgalaxy.cql2;

import ai.flexgalaxy.Cql2g4.Cql2Lexer;
import ai.flexgalaxy.Cql2g4.Cql2Parser;
import ai.flexgalaxy.cql2.ast.AstNode;
import ai.flexgalaxy.cql2.converter.sql.AstToSql;
import ai.flexgalaxy.cql2.converter.JsonNodeToAST;
import ai.flexgalaxy.cql2.converter.ParseTreeToJsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.function.Function;

public class Cql2G4 {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static final JsonNodeToAST jsonToAst = new JsonNodeToAST();
    final AstToSql astToSql;
    Function<String, String> propertyToQueryable;
    SqlDialect dialect;

    public Cql2G4() {
        this.propertyToQueryable = null;
        this.dialect = SqlDialect.PostgreSQL;
        astToSql = new AstToSql();
    }

    public Cql2G4(Function<String, String> propertyToQueryable){
        this.propertyToQueryable = propertyToQueryable;
        dialect = SqlDialect.PostgreSQL;
        astToSql = new AstToSql(propertyToQueryable);
    }

    public Cql2G4(SqlDialect dialect){
        propertyToQueryable = null;
        this.dialect = dialect;
        astToSql = new AstToSql();
    }

    public Cql2G4(Function<String, String> propertyToQueryable, SqlDialect sqlDialect) {
        this.propertyToQueryable = propertyToQueryable;
        this.dialect = sqlDialect;
        astToSql = new AstToSql(propertyToQueryable);
    }

    public String textToSql(String cqlText) {
        return textToSql(cqlText, SqlDialect.PostgreSQL);
    }

    public String textToSql(String cqlText, SqlDialect sqlDialect) {
        // text -> json node
        JsonNode jsonNode = textToJsonNode(cqlText);

        // json node -> sql
        return jsonNodeToSql(jsonNode, sqlDialect);
    }

    public String jsonNodeToSql(JsonNode node, SqlDialect sqlDialect) {
        if (sqlDialect != SqlDialect.PostgreSQL)
            throw new IllegalArgumentException("SQL dialect not supported");

        // json node -> ast node
        AstNode astNode = jsonToAst.convert(node);

        // ast node -> sql
        return astToSql.convert(astNode);
    }

    public String jsonToSql(String cqlJson, SqlDialect sqlDialect) throws JsonProcessingException {
        // json string -> json node
        JsonNode jsonNode = objectMapper.readTree(cqlJson);

        // json node -> sql
        return jsonNodeToSql(jsonNode, sqlDialect);
    }

    public JsonNode textToJsonNode(String cqlText) {
        // text -> parse tree
        Cql2Lexer lexer = new Cql2Lexer(CharStreams.fromString(cqlText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Cql2Parser parser = new Cql2Parser(tokens);
        ParseTree tree = parser.booleanExpression();

        // parse tree -> json node
        ParseTreeToJsonNode visitor = new ParseTreeToJsonNode(tokens);
        return visitor.visit(tree);
    }

    public String textToJsonString(String cqlText) throws JsonProcessingException {
        // text -> json node
        JsonNode jsonNode = textToJsonNode(cqlText);

        // json node -> json string
        return objectMapper.writeValueAsString(jsonNode);
    }
}

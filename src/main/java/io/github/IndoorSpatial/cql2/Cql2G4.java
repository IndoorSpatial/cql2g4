package io.github.IndoorSpatial.cql2;

import io.github.IndoorSpatial.cql2.ast.AstNode;
import io.github.IndoorSpatial.cql2.converter.*;
import io.github.IndoorSpatial.cql2.converter.AstToJsonNode;
import io.github.IndoorSpatial.cql2.converter.AstToText;
import io.github.IndoorSpatial.cql2.converter.JsonNodeToAST;
import io.github.IndoorSpatial.cql2.converter.ParseTreeToAst;
import io.github.IndoorSpatial.cql2.converter.sql.AstToSql;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.function.Function;

public class Cql2G4 {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonNodeToAST jsonToAst = new JsonNodeToAST();
    private final AstToSql astToSql;
    private final AstToText astToText = new AstToText();
    private final AstToJsonNode astToJson = new AstToJsonNode();
    private final SqlDialect dialect;

    public Cql2G4() {
        this.dialect = SqlDialect.PostgreSQL;
        astToSql = new AstToSql();
    }

    public Cql2G4(Function<String, String> propertyToQueryable){
        dialect = SqlDialect.PostgreSQL;
        astToSql = new AstToSql(propertyToQueryable);
    }

    public Cql2G4(SqlDialect dialect){
        this.dialect = dialect;
        astToSql = new AstToSql();
    }

    public Cql2G4(Function<String, String> propertyToQueryable, SqlDialect sqlDialect) {
        this.dialect = sqlDialect;
        astToSql = new AstToSql(propertyToQueryable);
    }

    public AstNode textToAst(String cqlText) {
        // text -> tokens -> parse tree
        Cql2Lexer lexer = new Cql2Lexer(CharStreams.fromString(cqlText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Cql2Parser parser = new Cql2Parser(tokens);
        ParseTree tree = parser.booleanExpression();

        // parse tree -> AST
        ParseTreeToAst toAst = new ParseTreeToAst(tokens);
        return toAst.visit(tree);
    }

    public AstNode jsonNodeToAst(JsonNode jsonNode) {
        return jsonToAst.convert(jsonNode);
    }

    public AstNode jsonStringToAst(String cqlJson) throws JsonProcessingException {
        // json string -> json node
        JsonNode jsonNode = objectMapper.readTree(cqlJson);

        // json node -> ast
        return jsonToAst.convert(jsonNode);
    }

    public String astToSql(AstNode astNode) {
        if (dialect != SqlDialect.PostgreSQL)
            throw new RuntimeException("Support PostgreSQL dialect only");
        return astToSql.convert(astNode);
    }

    public String astToText(AstNode astNode) {
        return astToText.convert(astNode);
    }

    public JsonNode astToJsonNode(AstNode astNode) {
        return astToJson.convert(astNode);
    }

    public String astToJsonString(AstNode astNode) throws JsonProcessingException {
        return objectMapper.writeValueAsString(astToJson.convert(astNode));
    }
}

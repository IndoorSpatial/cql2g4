package ai.flexgalaxy;

import ai.flexgalaxy.Cql2g4.Cql2Lexer;
import ai.flexgalaxy.Cql2g4.Cql2Parser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Cql2G4 {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static JsonNodeToAST jsonToAst = new JsonNodeToAST();
    static AstToSql astToSql = new AstToSql();

    public static String textToSql(String cqlText) {
        return textToSql(cqlText, SqlDialect.PostgreSQL);
    }

    public static String textToSql(String cqlText, SqlDialect sqlDialect) {
        // text -> parse tree
        Cql2Lexer lexer = new Cql2Lexer(CharStreams.fromString(cqlText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Cql2Parser parser = new Cql2Parser(tokens);
        ParseTree tree = parser.booleanExpression();

        // parse tree -> json node
        JsonConverterVisitor visitor = new JsonConverterVisitor(tokens);
        JsonNode jsonNode = visitor.visit(tree);

        // json node -> sql
        return jsonNodeToSql(jsonNode, sqlDialect);
    }

    public static String jsonNodeToSql(JsonNode node, SqlDialect sqlDialect) {
        if (sqlDialect != SqlDialect.PostgreSQL)
            throw new IllegalArgumentException("SQL dialect not supported");

        // json node -> ast node
        AstNode astNode = jsonToAst.visit(node);

        // ast node -> sql
        return astToSql.visit(astNode);
    }

    public static String jsonToSql(String cqlJson, SqlDialect sqlDialect) throws JsonProcessingException {
        // json string -> json node
        JsonNode jsonNode = objectMapper.readTree(cqlJson);

        // json node -> sql
        return jsonNodeToSql(jsonNode, sqlDialect);
    }
}

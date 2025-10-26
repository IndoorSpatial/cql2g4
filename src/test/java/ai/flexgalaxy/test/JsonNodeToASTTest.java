package ai.flexgalaxy.test;

import ai.flexgalaxy.AstNode;
import ai.flexgalaxy.JsonNodeToAST;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonNodeToASTTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void visit() throws JsonProcessingException {
        JsonNode json = objectMapper.readTree("{ \"op\": \"avg\", \"args\": [ { \"property\": \"windSpeed\" } ] }");

        JsonNodeToAST converter =  new JsonNodeToAST();
        AstNode astNode = converter.visit(json);

        System.out.println(json);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(astNode));
    }
}
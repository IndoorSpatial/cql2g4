package ai.flexgalaxy;

import ai.flexgalaxy.Cql2g4.Cql2Lexer;
import ai.flexgalaxy.Cql2g4.Cql2Parser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        String cqlText = "speed > 3 AND S_CONTAINS(POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0)), location)";
        System.out.println(cqlText);
        System.out.println(Cql2G4.textToJsonString(cqlText));
        System.out.println(Cql2G4.textToSql(cqlText));
    }
}
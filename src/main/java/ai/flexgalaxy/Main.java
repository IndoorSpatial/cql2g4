package ai.flexgalaxy;

import ai.flexgalaxy.Cql2g4.Cql2Lexer;
import ai.flexgalaxy.Cql2g4.Cql2Parser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        Cql2Lexer lexer = new Cql2Lexer(CharStreams.fromString("""
                S_CROSSES(road,POLYGON((43.7286 -79.2986, 43.7311 -79.2996, 43.7323 -79.2972,
                                        43.7326 -79.2971, 43.7350 -79.2981, 43.7350 -79.2982,
                                        43.7352 -79.2982, 43.7357 -79.2956, 43.7337 -79.2948,
                                        43.7343 -79.2933, 43.7339 -79.2923, 43.7327 -79.2947,
                                        43.7320 -79.2942, 43.7322 -79.2937, 43.7306 -79.2930,
                                        43.7303 -79.2930, 43.7299 -79.2928, 43.7286 -79.2986)))
                """));
        Cql2Parser parser = new Cql2Parser(new CommonTokenStream(lexer));

        ParseTree tree = parser.booleanExpression();

        JsonConverterVisitor visitor = new JsonConverterVisitor();

        System.out.println(tree.toStringTree(parser));
        System.out.println(visitor.toJsonString(tree));
    }
}
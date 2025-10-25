package ai.flexgalaxy;

import ai.flexgalaxy.Cql2g4.Cql2Lexer;
import ai.flexgalaxy.Cql2g4.Cql2Parser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        Cql2Lexer lexer = new Cql2Lexer(CharStreams.fromString("TRUE AND FALSE OR FALSE AND TRUE"));
        Cql2Parser parser = new Cql2Parser(new CommonTokenStream(lexer));

        ParseTree tree = parser.booleanExpression();

        JsonConverterVisitor visitor = new JsonConverterVisitor();

        System.out.println(tree.toStringTree(parser));
        System.out.println(visitor.toJsonString(tree));
    }
}
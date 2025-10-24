package ai.flexgalaxy;

import ai.flexgalaxy.Cql2g4.Cql2Lexer;
import ai.flexgalaxy.Cql2g4.Cql2Parser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Cql2Lexer lexer = new Cql2Lexer(CharStreams.fromString("3 > 5"));
        Cql2Parser parser = new Cql2Parser(new CommonTokenStream(lexer));

        ParseTree tree = parser.booleanExpression();

        System.out.println(tree.toStringTree(parser));
    }
}
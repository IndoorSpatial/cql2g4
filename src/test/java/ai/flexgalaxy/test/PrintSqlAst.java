package ai.flexgalaxy.test;

import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.expression.*;

public class PrintSqlAst {
    static public void printSelect(PlainSelect select, int indent) {
        String pad = "  ".repeat(indent);
        System.out.println(pad + "SELECT");
        select.getSelectItems().forEach(item ->
                System.out.println(pad + "  item: " + item)
        );
        System.out.println(pad + "FROM: " + select.getFromItem());
        if (select.getWhere() != null) {
            System.out.println(pad + "WHERE:");
            printExpression(select.getWhere(), indent + 1);
        }
    }

    static public void printExpression(Expression expr, int indent) {
        String pad = "  ".repeat(indent);
        System.out.println(pad + expr.getClass().getSimpleName() + ": " + expr);
        if (expr instanceof BinaryExpression be) {
            printExpression(be.getLeftExpression(), indent + 1);
            printExpression(be.getRightExpression(), indent + 1);
        }
    }
}
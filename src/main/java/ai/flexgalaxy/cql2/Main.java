package ai.flexgalaxy.cql2;

import ai.flexgalaxy.cql2.ast.AstNode;
import ai.flexgalaxy.cql2.converter.sql.PropertyToQueryable;
import ai.flexgalaxy.cql2.converter.sql.Queryable;
import ai.flexgalaxy.cql2.converter.sql.QueryableType;
import ai.flexgalaxy.cql2.converter.sql.SqlType;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        String cqlText = "speed > 3 AND S_CONTAINS(POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0)), location)";
        System.out.println("origin cql text: " + cqlText);

        Cql2G4 cql2G4 = new Cql2G4();
        AstNode astNode = cql2G4.textToAst(cqlText);
        System.out.println("convert to ast:\n" + astNode.ToString());
        System.out.println("convert to json: " + cql2G4.astToJsonString(astNode));
        System.out.println("convert to sql: " + cql2G4.astToSql(astNode));

        String cqlWithPropertyName = "\"obj.key1\" = 'value1' AND \"obj.key2\" = 5";
        System.out.println("another cql text: " + cqlWithPropertyName);
        AstNode astNode2 = cql2G4.textToAst(cqlWithPropertyName);
        System.out.println("convert it to sql directly: " + cql2G4.astToSql(astNode2));

        HashMap<String, Queryable> queryables = new HashMap<>() {{
            put("speed", new Queryable("speed", SqlType.Float, QueryableType.ColumnName));
            put("is_valid", new Queryable("is_valid", SqlType.Boolean, QueryableType.JsonField));
            put("obj.key1", new Queryable("obj.key1", SqlType.Text, QueryableType.JsonField));
            put("obj.key2", new Queryable("obj.key2", SqlType.Integer, QueryableType.JsonField));
        }};
        PropertyToQueryable propertyToQueryable = new PropertyToQueryable(queryables, "properties");
        Cql2G4 cql2G4WithQueryable = new Cql2G4(propertyToQueryable::toQueryable);
        System.out.println("convert it to sql according to queryables: " +  cql2G4WithQueryable.astToSql(astNode2));
    }
}
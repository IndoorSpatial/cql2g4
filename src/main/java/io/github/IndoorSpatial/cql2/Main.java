package io.github.IndoorSpatial.cql2;

import io.github.IndoorSpatial.cql2.ast.AstNode;
import io.github.IndoorSpatial.cql2.converter.sql.PropertyToQueryable;
import io.github.IndoorSpatial.cql2.converter.sql.Queryable;
import io.github.IndoorSpatial.cql2.converter.sql.QueryableType;
import io.github.IndoorSpatial.cql2.converter.sql.SqlType;
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

        String cqlWithPropertyName = "speed > 1.2 AND \"obj.key\" = 'value1' AND A_CONTAINS(obj.binlocations, ('A-1-2-01-a'))";
        System.out.println("another cql text: " + cqlWithPropertyName);
        AstNode astNode2 = cql2G4.textToAst(cqlWithPropertyName);
        System.out.println("convert it to sql directly:\n" + cql2G4.astToSql(astNode2));

        HashMap<String, Queryable> queryables = new HashMap<>() {{
            put("speed", new Queryable("speed", null, SqlType.Float));
            put("obj.key", new Queryable("properties", "$.obj.key", SqlType.Text));
            put("obj.binlocations", new Queryable("properties", "$.obj.binlocations[*]", SqlType.TextArray));
        }};
        PropertyToQueryable propertyToQueryable = new PropertyToQueryable(queryables);
        Cql2G4 cql2G4WithQueryable = new Cql2G4(propertyToQueryable::toQueryable);
        System.out.println("convert it to sql according to queryables:\n" +  cql2G4WithQueryable.astToSql(astNode2));
    }
}

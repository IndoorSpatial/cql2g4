package io.github.IndoorSpatial.cql2;

import io.github.IndoorSpatial.cql2.ast.AstNode;
import io.github.IndoorSpatial.cql2.converter.sql.PropertyToQueryable;
import io.github.IndoorSpatial.cql2.converter.sql.Queryable;
import io.github.IndoorSpatial.cql2.converter.sql.SqlType;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        try {
            // Example 1: Basic conversion - CQL2 text to AST, JSON, and SQL
            String cqlText = "speed > 3 AND S_CONTAINS(POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0)), location)";
            System.out.println("Original CQL2 text: " + cqlText);

            Cql2G4 cql2G4 = new Cql2G4();
            AstNode astNode = cql2G4.textToAst(cqlText);
            System.out.println("Convert to AST:\n" + astNode.ToString());
            System.out.println("Convert to JSON: " + cql2G4.astToJsonString(astNode));
            System.out.println("Convert to SQL: " + cql2G4.astToSql(astNode));

            // Example 2: Property mapping - map CQL2 properties to database queryable fields
            String cqlWithPropertyName = "speed > 1.2 AND \"obj.key\" = 'value1' AND A_CONTAINS(obj.binlocations, ('A-1-2-01-a'))";
            System.out.println("Another CQL2 text: " + cqlWithPropertyName);
            AstNode astNode2 = cql2G4.textToAst(cqlWithPropertyName);
            System.out.println("Convert to SQL directly:\n" + cql2G4.astToSql(astNode2));

            // Define property mappings: CQL2 property name -> database field/JSON path
            HashMap<String, Queryable> queryables = new HashMap<>() {{
                put("speed", new Queryable("speed", null, SqlType.Float));
                put("obj.key", new Queryable("properties", "$.obj.key", SqlType.Text));
                put("obj.binlocations", new Queryable("properties", "$.obj.binlocations[*]", SqlType.TextArray));
            }};
            PropertyToQueryable propertyToQueryable = new PropertyToQueryable(queryables);
            Cql2G4 cql2G4WithQueryable = new Cql2G4(propertyToQueryable::toQueryable);
            System.out.println("Convert to SQL according to queryables:\n" + cql2G4WithQueryable.astToSql(astNode2));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

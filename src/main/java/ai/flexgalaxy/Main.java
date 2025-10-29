package ai.flexgalaxy;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        String cqlText = "speed > 3 AND S_CONTAINS(POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0)), location)";
        System.out.println(cqlText);
        System.out.println(Cql2G4.textToJsonString(cqlText));
        System.out.println(Cql2G4.textToSql(cqlText));
    }
}
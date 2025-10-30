package ai.flexgalaxy.cql2.converter.sql;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Queryable {
    String name;
    SqlType sqlType;
    QueryableType queryableType;
    public Queryable(String name,  SqlType sqlType, QueryableType queryableType) {
        this.name = name;
        this.sqlType = sqlType;
        this.queryableType = queryableType;
    }
}

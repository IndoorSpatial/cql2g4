package io.github.IndoorSpatial.cql2.converter.sql;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Queryable {
    String name;
    String type;
    String title;
    String description;
    String format;
    @JsonIgnore
    SqlType sqlType;
    QueryableType queryableType;

    public Queryable(String name,  SqlType sqlType, QueryableType queryableType) {
        this.name = name;
        this.sqlType = sqlType;
        this.queryableType = queryableType;
    }

    public Queryable(String name, String type, String title, String description, String format, SqlType sqlType, QueryableType queryableType) {
        this.name = name;
        this.type = type;
        this.title = title;
        this.description = description;
        this.format = format;
        this.sqlType = sqlType;
        this.queryableType = queryableType;
    }

    public Queryable(String name, String type, QueryableType queryableType) {
        this(name, type, null, null, null, null, queryableType);
    }

    public static Queryable makeGeom(String name, String format, QueryableType queryableType) {
        return new Queryable(name, null, null, null, format, SqlType.Geometry, queryableType);
    }
}

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
    String column;
    String jsonPath;
    @JsonIgnore
    SqlType sqlType;

    public Queryable() {}

    public Queryable(String name, String type, String title, String description, String format, String column, String jsonPath, SqlType sqlType) {
        this.name = name;
        this.type = type;
        this.title = title;
        this.description = description;
        this.format = format;
        this.column = column;
        this.jsonPath = jsonPath;
        this.sqlType = sqlType;
    }

    public Queryable(String column, String jsonPath, SqlType sqlType) {
        this(null, null, null, null, null, column, jsonPath, sqlType);
    }

    public Queryable(String name, String type, String column, String jsonPath, SqlType sqlType) {
        this(name, type, null, null, null, column, jsonPath, sqlType);
    }

    public static Queryable makeGeom(String name, String format) {
        return new Queryable(name, null, null, null, format, name, null, SqlType.Geometry);
    }
}

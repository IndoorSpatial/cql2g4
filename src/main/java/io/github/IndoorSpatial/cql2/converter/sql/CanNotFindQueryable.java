package io.github.IndoorSpatial.cql2.converter.sql;

public class CanNotFindQueryable extends RuntimeException {
    private String propertyName;
    public CanNotFindQueryable(String propertyName, String message) {
        super(message);
        this.propertyName = propertyName;
    }
    public CanNotFindQueryable(String message) {
        super(message);
    }

    public String getPropertyName() {
        return propertyName;
    }
}

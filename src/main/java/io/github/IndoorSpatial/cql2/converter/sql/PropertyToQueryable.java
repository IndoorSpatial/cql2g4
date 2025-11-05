package io.github.IndoorSpatial.cql2.converter.sql;

import java.util.HashMap;

public class PropertyToQueryable {
    private final HashMap<String, Queryable> queryables;

    public PropertyToQueryable(HashMap<String, Queryable> queryables) {
        this.queryables = queryables;
    }

    public String toQueryable(String propertyName) {
        if (!queryables.containsKey(propertyName))
            throw new CanNotFindQueryable(propertyName, "Property not found: " + propertyName);
        Queryable queryable = queryables.get(propertyName);

        if (queryable.getJsonPath() == null || queryable.getJsonPath().isEmpty())
            return '"' + propertyName + '"';
        if (queryable.getSqlType() == SqlType.TextArray) {
            return String.format("(SELECT ARRAY_AGG(elem) FROM jsonb_array_elements_text(jsonb_path_query_array(%s, '%s')) AS elem)", queryable.getColumn(), queryable.getJsonPath());
        } else {
            String sqlTypeString = switch (queryable.getSqlType()) {
                case Integer -> "integer";
                case Float -> "double";
                case Text -> "text";
                case Date -> "date";
                case Timestamp -> "timestamp";
                case Boolean -> "boolean";
                case Geometry -> "geometry";
                default -> throw new IllegalStateException("Unexpected value: " + queryable.getSqlType());
            };
            String result = String.format("jsonb_path_query_first(%s, '%s')::%s", queryable.getColumn(), queryable.getJsonPath(), sqlTypeString);
            if (queryable.getSqlType() == SqlType.Text)
                return "TRIM(BOTH '\"' FROM " + result + ")";
            else
                return result;
        }
    }
}

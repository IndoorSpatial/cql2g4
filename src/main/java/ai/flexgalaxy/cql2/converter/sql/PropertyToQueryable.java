package ai.flexgalaxy.cql2.converter.sql;

import java.util.HashMap;

public class PropertyToQueryable {
    private final HashMap<String, Queryable> queryables;
    private final String jsonColumnName;

    public PropertyToQueryable(HashMap<String, Queryable> queryables, String jsonColumnName) {
        this.queryables = queryables;
        this.jsonColumnName = jsonColumnName;
    }

    public String toQueryable(String propertyName) {
        if (!queryables.containsKey(propertyName))
            throw new CanNotFindQueryable(propertyName, "Property not found: " + propertyName);
        Queryable queryable = queryables.get(propertyName);

        if (queryable.getQueryableType() == QueryableType.ColumnName)
            return '"' + propertyName + '"';
        if (queryable.getQueryableType() == QueryableType.JsonField) {
            String sqlTypeString = switch (queryable.getSqlType()) {
                case Integer -> "integer";
                case Float -> "double";
                case Text -> "text";
                case Date -> "date";
                case Timestamp -> "timestamp";
                case Boolean -> "boolean";
                case Geometry -> "geometry";
            };
            return String.format("%s #>> '%s'::%s", jsonColumnName, propertyName.replace('.', ','), sqlTypeString);
        }

        return null;
    }
}

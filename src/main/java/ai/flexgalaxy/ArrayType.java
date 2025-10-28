package ai.flexgalaxy;

public enum ArrayType {
    BuiltIn,  // PG: SELECT array_column FROM MY_TABLE WHERE array_column && ARRAY[1, 2];
    Json,     // PG: SELECT json_column FROM MY_TABLE WHERE json_column -> 'array_key' ?| ARRAY[1, 2];
}

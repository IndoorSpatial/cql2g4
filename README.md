# cql2g4
cql2g4 is a parer for CQL2(Common Query Language) using ANTLR4

https://www.ogc.org/standards/cql2/

https://docs.ogc.org/is/21-065r2/21-065r2.html

# Examples
```java
import ai.flexgalaxy.cql2.Cql2G4;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        String cqlText = "speed > 3 AND S_CONTAINS(POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0)), location)";
        System.out.println(cqlText);
        System.out.println(Cql2G4.textToJsonString(cqlText));
        /*
        {
          "op" : "and",
          "args" : [ {
            "op" : ">",
            "args" : [ {
              "property" : "speed"
            }, 3 ]
          }, {
            "op" : "s_contains",
            "args" : [ {
              "type" : "Polygon",
              "coordinates" : [ [ [ 0.0, 0.0 ], [ 1, 0.0 ], [ 1, 1 ], [ 0.0, 1 ], [ 0.0, 0.0 ] ] ]
            }, {
              "property" : "location"
            } ]
          } ]
        }
        */
        System.out.println(Cql2G4.textToSql(cqlText));
        /*
          "speed" > 3 AND ST_CONTAINS(ST_GeomFromText('POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))'), "location")
        */
    }
}
```

# Expression format
![cql2g4_progress_20251029.png](doc/cql2g4_progress_20251029.png)
There are 6 related expression format in cql2g4.

## Text
This is text format defined in the cql2 standard
```text
balance-150.0 > 0
```

## Json string and in-memory JsonNode
This is json format defined in the cql2 standard
```json
{
  "op": ">",
  "args": [
    {
      "op": "-",
      "args": [
        { "property": "balance" },
        150.0
      ]
    },
    0
  ]
}
```
It will be parsed by jackson to obtain JsonNodes in memory.

## Parse tree
The ANTLR4 generated code will parse the text format of expression into parse tree which looks like:

![parse_tree.png](doc/parse_tree.png)

## AST (Abstract Syntax Tree)
Both text and json format will be converted to AST.
```json
{
  "op" : ">",
  "type" : "binaryComparisonPredicate",
  "args" : [ {
    "op" : "-",
    "type" : "arithmeticExpression",
    "args" : [ {
      "type" : "Property",
      "value" : "balance",
      "literalType" : "Property"
    }, {
      "type" : "Double",
      "value" : 150.0,
      "literalType" : "Double"
    } ]
  }, {
    "type" : "Integer",
    "value" : 0,
    "literalType" : "Integer"
  } ]
}
```
It looks like json format but contains extra type information for example "arithmeticExpression", "Property".
The type information may help converters to do the conversion recursively.

## SQL
```sql
-- SELECT * FROM t WHERE
"balance" - 150.0 > 0;
```
The SqlConverter will generate the where clause according to AST format, without the "SELECT" part and the word "WHERE".


# SQL

## Dialect
The only currently supported SQL dialect is PostgreSQL, as it offers excellent support for intervals, arrays, and spatial data simultaneously.
We will support other mainstream SQL dialects in the future.

## Examples
| CQL2                                                                                             | SQL WHERE clause (PostgreSQL dialect)                                                                                                             |
|--------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| wind_speed > 2 * 3 + 4                                                                           | "wind_speed" > 2 * 3 + 4                                                                                                                          |
| city='Shenzhen'                                                                                  | "city" = 'Shenzhen'                                                                                                                               |
| value=field^2                                                                                    | "value" = POWER("field", 2)                                                                                                                       |
| value IN (1.0, 2.0, 3.0)                                                                         | "value" IN (1.0, 2.0, 3.0)                                                                                                                        |
| owner NOT LIKE '%Mike%'                                                                          | NOT ("owner" LIKE '%Mike%')                                                                                                                       |
| value IS NULL OR value BETWEEN 10 AND 20                                                         | "value" IS NULL OR "value" BETWEEN 10 AND 20                                                                                                      |
| A_CONTAINS(layer:ids, ('layers-ca','layers-us'))                                                 | "layer:ids" @> ARRAY ['layers-ca', 'layers-us']                                                                                                   |
| S_INTERSECTS(geom,POINT(36.3 32.2))                                                              | ST_INTERSECTS("geom", ST_GeomFromText('POINT (36.3 32.2)')) --(extension postgis needed)                                                          |
| S_WITHIN(location,BBOX(-118,33.8,-117.9,34))                                                     | ST_WITHIN("location", ST_GeomFromText('POLYGON((-118.0 33.8, -117.9 33.8, -117.9 34.0, -118.0 34.0, -118.0 33.8))')) --(extension postgis needed) |
| T_DURING(INTERVAL(starts_at, ends_at), INTERVAL('1990-08-09T23:30:00Z', '2025-10-29T17:39:00Z')) | TSRANGE("starts_at", "ends_at", '[]') <@ TSRANGE('1990-08-09T23:30:00Z', '2025-10-29T17:39:00Z', '[]')                                            |
| T_BEFORE(built, DATE('2015-01-01'))                                                              | TSRANGE("built", "built", '[]') << TSRANGE(DATE '2015-01-01', DATE '2015-01-01', '[]')                                                            |
| ACCENTI(etat_vol) = ACCENTI('débárquér')                                                         | UNACCENT("etat_vol") = UNACCENT('débárquér') --(extension unaccent needed)                                                                        |
| CASEI(road_class) IN (CASEI('Οδος'),CASEI('Straße'))                                             | LOWER(road_class) IN (LOWER('Οδος'),LOWER('Straße'))                                                                                              |
| my_function(windSpeed) < 4                                                                       | my_function("windSpeed") < 4 --(user defined function needed)                                                                                     |

# History
Syrius Robotics has been focused on developing applications for indoor robots, which led us to create an indoor geographic information system.
At the end of 2024, while working on this system, we recognized the need for a filter language to help robots query indoor features more efficiently.
Around that time, OGC CQL2 had just been released, but there were no existing any libraries available for it.
As a result, Syrius Robotics decided to develop their own solution.

Since the end of 2024, we have developed cql2cpp, a C++ parsing library, based on Flex and Bison.
However, this library can only be used on our embedded devices, which are robots.
To enable its use in server-side GIS, we have started reconstructing it based on ANTLR4 since the fourth quarter of 2025.
We aim to leverage ANTLR4's excellent portability to support multiple different programming languages.

# Progress and Plan
As of the end of October 2025, the Java-based parser is already capable of parsing all features of the CQL2 language, including those in TEXT and JSON formats.
Additionally, we have implemented the translation of CQL2 language into SQL WHERE clauses using Java.
We plan to gradually add support for other programming languages in the coming months.

# Dependencies

* antlr4:4.13.2
* jackson-core:2.19.1
* jackson-databind:2.19.1
* jts-core:1.19.0
* jts-io-common:1.19.0

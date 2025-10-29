# cql2g4
cql2g4 is a parer for CQL2(Common Query Language) using ANTLR4

https://www.ogc.org/standards/cql2/

https://docs.ogc.org/is/21-065r2/21-065r2.html

# Examples
```java
import ai.flexgalaxy.Cql2G4;

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
```

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

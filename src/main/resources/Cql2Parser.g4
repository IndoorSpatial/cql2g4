parser grammar Cql2Parser;

options { tokenVocab = Cql2Lexer; }

//  A CQL2 filter is a logically connected expression of one or more predicates.
//  Predicates include scalar or comparison predicates, spatial predicates or
//  temporal predicates.
booleanExpression : booleanTerm (OR booleanTerm)*;

booleanTerm : booleanFactor (AND booleanFactor)*;

booleanFactor : NOT? booleanPrimary;

booleanPrimary : function
               | predicate
               | booleanLiteral
               | LP booleanExpression RP
               ;

predicate : comparisonPredicate
          | spatialPredicate
          | temporalPredicate
          | arrayPredicate
          ;

//  A comparison predicate evaluates if two scalar expression statisfy the
//  specified comparison operator.  The comparion operators includes an operator
//  to evaluate pattern matching expressions (LIKE), a range evaluation operator
//  and an operator to test if a scalar expression is NULL or not.
comparisonPredicate : binaryComparisonPredicate
                    | isLikePredicate
                    | isBetweenPredicate
                    | isInListPredicate
                    | isNullPredicate
                    ;

//  Binary comparison predicate
binaryComparisonPredicate : scalarExpression comparisonOperator scalarExpression;

scalarExpression : propertyName
                 | function
                 | characterClause
                 | numericLiteral
                 | instantInstance
                 | booleanLiteral
                 | arithmeticExpression
                 ;

comparisonOperator : COMP;

//  LIKE predicate
isLikePredicate :  characterExpression NOT? LIKE patternExpression;

patternExpression : CASEI LP patternExpression RP
                  | ACCENTI LP patternExpression RP
                  | CharacterLiteral
                  ;

//  BETWEEN predicate
isBetweenPredicate : numericExpression NOT? BETWEEN numericExpression AND numericExpression;

numericExpression : propertyName
                  | arithmeticExpression
                  | numericLiteral
                  | function
                  ;

//  IN LIST predicate
isInListPredicate : scalarExpression NOT? IN LP inList RP;

inList : scalarExpression (COMMA scalarExpression)*;

//  IS NULL predicate
isNullPredicate : isNullOperand IS NOT? NULL;

isNullOperand : propertyName
              | characterClause
              | numericLiteral
              | temporalInstance
              | spatialInstance
              | arithmeticExpression
              | LP booleanExpression RP  // DIFF: we use '(', ')' to avoid loop: boolExpr -> boolTerm -> boolFactor -> boolPrimary -> predicate -> isNull -> boolExpr
              | function
              ;

//  A spatial predicate evaluates if two spatial expressions satisfy the
//  condition implied by a standardized spatial comparison function.  If the
//  conditions of the spatial comparison function are met, the function returns
//  a Boolean value of true.  Otherwise the function returns false.
spatialPredicate : spatialFunction LP geomExpression COMMA geomExpression RP;

//  NOTE: The buffer functions (DWITHIN and BEYOND) are not included because
//        these are outside the scope of a 'simple' core for CQL2.  These
//        can be added as extensions.
spatialFunction : SPATIAL;

//  A geometric expression is a property name of a geometry-valued property,
//  a geometric literal (expressed as WKT) or a function that returns a
//  geometric value.
geomExpression : spatialInstance
               | propertyName
               | function
               ;

//  A temporal predicate evaluates if two temporal expressions satisfy the
//  condition implied by a standardized temporal comparison function.  If the
//  conditions of the temporal comparison function are met, the function returns
//  a Boolean value of true.  Otherwise the function returns false.
temporalFunction : TEMPORAL;
temporalPredicate : temporalFunction LP temporalExpression COMMA temporalExpression RP;

temporalExpression : temporalInstance
                   | propertyName
                   | function
                   ;


//  An array predicate evaluates if two array expressions satisfy the
//  condition implied by a standardized array comparison function.  If the
//  conditions of the array comparison function are met, the function returns
//  a Boolean value of true.  Otherwise the function returns false.
arrayFunction : ARRAY;
arrayPredicate : arrayFunction LP arrayExpression COMMA arrayExpression RP;

arrayExpression : array
                | propertyName
                | function
                ;

//  An array is a parentheses-delimited, comma-separated list of array
//  elements.
array : LP RP
      | LP arrayElement (COMMA arrayElement)* RP
      ;

//  An array element is either a character literal, a numeric literal,
//  a geometric literal, a temporal instance, a property name, a function,
//  an arithmetic expression or an array.
arrayElement : characterClause
             | numericLiteral
             | temporalInstance
             | spatialInstance
             | array
             | arithmeticExpression
             | booleanExpression
             | propertyName
             | function
             ;

//  An arithmetic expression is an expression composed of an arithmetic
//  operand (a property name, a number or a function that returns a number),
//  an arithmetic operators (+,-,*,/,%,div,^) and another arithmetic operand.
arithmeticExpression : arithmeticTerm (Sign arithmeticTerm)*;  // TODO: should we use Sign or ArithmeticOperatorPlusMinus?

arithmeticTerm : powerTerm (ArithmeticOperatorMultDiv powerTerm)*;

powerTerm : arithmeticFactor (POWER arithmeticFactor)?;

arithmeticFactor : LP arithmeticExpression RP
                 | arithmeticOperand
                 | Sign arithmeticOperand
                 ;

arithmeticOperand : numericLiteral
                  | propertyName
                  | function
                  ;

//  Definition of a PROPERTYNAME
//  Production copied from: https://www.w3.org/TR/REC-xml/// sec-common-syn,
//                          'Names and Tokens'.
propertyName : Identifier
             | DQ Identifier DQ
             ;

// =============================================================================//
//  Definition of a FUNCTION
// =============================================================================//
function : Identifier LP RP
         | Identifier LP argumentList RP;

argumentList : argument (COMMA argument)*;

argument : propertyName
         | characterClause
         | numericLiteral
         | temporalInstance
         | spatialInstance
         | array
         | arithmeticExpression
         | booleanExpression
         | function
         ;

//  Character expression
characterExpression : characterClause
                    | propertyName
                    | function
                    ;

characterClause : CASEI LP characterExpression RP
                | ACCENTI LP characterExpression RP
                | CharacterLiteral
                ;

//  Definition of NUMERIC literals
signedInteger : UnsignedInteger
              | Sign UnsignedInteger
              ;
decimalNumericLiteral : UnsignedInteger DOT UnsignedInteger
                      | UnsignedInteger DOT
                      | DOT UnsignedInteger
                      | UnsignedInteger
                      ;
scientificNumericLiteral : decimalNumericLiteral EXP signedInteger;

unsignedNumericLiteral : decimalNumericLiteral
                       | scientificNumericLiteral
                       ;
signedNumericLiteral : Sign? unsignedNumericLiteral;

numericLiteral : unsignedNumericLiteral
               | signedNumericLiteral
               ;


//  Boolean literal
booleanLiteral : BOOL;

//  Definition of GEOMETRIC literals
//
//  NOTE: This is basically BNF that define WKT encoding. It would be nice
//        to instead reference some normative BNF for WKT.
spatialInstance : geometryLiteral
                | geometryCollectionTaggedText
                | bboxTaggedText
                ;

geometryLiteral : pointTaggedText
                | linestringTaggedText
                | polygonTaggedText
                | multipointTaggedText
                | multilinestringTaggedText
                | multipolygonTaggedText
                ;

pointTaggedText : POINT Z? pointText;
linestringTaggedText : LINESTRING Z? lineStringText;
polygonTaggedText : POLYGON Z? polygonText;
multipointTaggedText : MULTIPOINT Z? multiPointText;
multilinestringTaggedText : MULTILINESTRING Z? multiLineStringText;
multipolygonTaggedText : MULTIPOLYGON Z? multiPolygonText;
geometryCollectionTaggedText : GEOMETRYCOLLECTION Z? geometryCollectionText;

pointText : LP point RP;
point : xCoord yCoord | xCoord yCoord zCoord;
xCoord : signedNumericLiteral;
yCoord : signedNumericLiteral;
zCoord : signedNumericLiteral;

lineStringText : LP point COMMA point (COMMA point)* RP;
linearRingText : LP point COMMA point COMMA point COMMA point (COMMA point)* RP;
polygonText : LP linearRingText (COMMA linearRingText)* RP;
multiPointText : LP pointText (COMMA pointText)* RP;
multiLineStringText : LP lineStringText (COMMA lineStringText)* RP;
multiPolygonText : LP polygonText (COMMA polygonText)* RP;
geometryCollectionText : LP geometryLiteral (COMMA geometryLiteral)* RP;

bboxTaggedText : BBOX bboxText;
bboxText : LP westBoundLon COMMA southBoundLat COMMA (minElev COMMA)? eastBoundLon COMMA northBoundLat (COMMA maxElev)? RP;
westBoundLon : signedNumericLiteral;
eastBoundLon : signedNumericLiteral;
northBoundLat : signedNumericLiteral;
southBoundLat : signedNumericLiteral;

minElev : signedNumericLiteral;
maxElev : signedNumericLiteral;

temporalInstance : instantInstance | intervalInstance;
instantInstance : dateInstant | timestampInstant;
dateInstant : DATE LP CharacterLiteral RP;
timestampInstant : TIMESTAMP LP CharacterLiteral RP;
intervalInstance : INTERVAL LP instantParameter COMMA instantParameter RP;

instantParameter : CharacterLiteral
                 | dateInstant
                 | timestampInstant
                 | DDOT
                 | propertyName
                 | function;
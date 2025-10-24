grammar Cql2;

AND : 'AND' | 'and';
OR : 'OR' | 'or';
NOT : 'NOT' | 'not';

//  A CQL2 filter is a logically connected expression of one or more predicates.
//  Predicates include scalar or comparison predicates, spatial predicates or
//  temporal predicates.
booleanExpression : booleanTerm (OR booleanTerm)*;

booleanTerm : booleanFactor (AND booleanFactor)*;

booleanFactor : NOT? booleanPrimary;

booleanPrimary : function
               | predicate
               | booleanLiteral
               | '(' booleanExpression ')'
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
binaryComparisonPredicate : scalarExpression
                            comparisonOperator
                            scalarExpression;

scalarExpression : propertyName
                 | function
                 | characterClause
                 | numericLiteral
                 | instantInstance
                 | booleanLiteral
                 | arithmeticExpression
                 ;

comparisonOperator :  '='     //  equal
                   | '<' '>'  //  not equal
                   | '<'      //  less than
                   | '>'      //  greater than
                   | '<' '='  //  less than or equal
                   | '>' '='  //  greater than or equal
                   ;

//  LIKE predicate
LIKE : 'LIKE' | 'like';
isLikePredicate :  characterExpression NOT? LIKE patternExpression;

CASEI : 'CASEI' | 'casei';
ACCENTI : 'ACCENTI' | 'accenti';
patternExpression : CASEI '(' patternExpression ')'
                  | ACCENTI '(' patternExpression ')'
                  | CharacterLiteral
                  ;

//  BETWEEN predicate
BETWEEN : 'BETWEEN' | 'between';
isBetweenPredicate : numericExpression BETWEEN numericExpression AND numericExpression
                   | numericExpression NOT BETWEEN numericExpression AND numericExpression;

numericExpression : propertyName
                  | arithmeticExpression
                  | numericLiteral
                  | function
                  ;

//  IN LIST predicate
IN : 'IN' | 'in';
isInListPredicate : scalarExpression NOT? IN '(' inList ')';

inList : scalarExpression (',' scalarExpression)*;

//  IS NULL predicate
IS : 'IS' | 'is';
NULL : 'NULL' | 'null';
isNullPredicate : isNullOperand IS NOT? NULL;

isNullOperand : propertyName
              | characterClause
              | numericLiteral
              | temporalInstance
              | spatialInstance
              | arithmeticExpression
              | '(' booleanExpression ')'  // DIFF we use '(', ')' to avoid loop: boolExpr -> boolTerm -> boolFactor -> boolPrimary -> predicate -> isNull -> boolExpr
              | function
              ;

//  A spatial predicate evaluates if two spatial expressions satisfy the
//  condition implied by a standardized spatial comparison function.  If the
//  conditions of the spatial comparison function are met, the function returns
//  a Boolean value of true.  Otherwise the function returns false.
spatialPredicate : spatialFunction '(' geomExpression ',' geomExpression ')';

//  NOTE: The buffer functions (DWITHIN and BEYOND) are not included because
//        these are outside the scope of a 'simple' core for CQL2.  These
//        can be added as extensions.
spatialFunction : 'S_INTERSECTS' | 's_intersects'
                | 'S_EQUALS' | 's_equals'
                | 'S_DISJOINT' | 's_disjoint'
                | 'S_TOUCHES' | 's_touches'
                | 'S_WITHIN' | 's_within'
                | 'S_OVERLAPS' | 's_overlaps'
                | 'S_CROSSES' | 's_crosses'
                | 'S_CONTAINS' | 's_contains'
                ;

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
temporalPredicate : temporalFunction
                    '(' temporalExpression ',' temporalExpression ')';

temporalExpression :
                     temporalInstance
                   | propertyName
                   | function
                   ;

temporalFunction : 'T_AFTER' | 't_after'
                 | 'T_BEFORE' | 't_before'
                 | 'T_CONTAINS' | 't_contains'
                 | 'T_DISJOINT' | 't_disjoint'
                 | 'T_DURING' | 't_during'
                 | 'T_EQUALS' | 't_equals'
                 | 'T_FINISHEDBY' | 't_finishedby'
                 | 'T_FINISHES' | 't_finishes'
                 | 'T_INTERSECTS' | 't_intersects'
                 | 'T_MEETS' | 't_meets'
                 | 'T_METBY' | 't_metby'
                 | 'T_OVERLAPPEDBY' | 't_overlappedby'
                 | 'T_OVERLAPS' | 't_overlaps'
                 | 'T_STARTEDBY' | 't_startedby'
                 | 'T_STARTS' | 't_starts'
                 ;

//  An array predicate evaluates if two array expressions satisfy the
//  condition implied by a standardized array comparison function.  If the
//  conditions of the array comparison function are met, the function returns
//  a Boolean value of true.  Otherwise the function returns false.
arrayPredicate : arrayFunction
                 '(' arrayExpression ',' arrayExpression ')';

arrayExpression : array
                | propertyName
                | function
                ;

//  An array is a parentheses-delimited, comma-separated list of array
//  elements.
array : '(' ')'
      | '(' arrayElement (',' arrayElement)* ')';

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

arrayFunction : 'A_EQUALS' | 'a_equals'
              | 'A_CONTAINS' | 'a_contains'
              | 'A_CONTAINEDBY' | 'a_containedby'
              | 'A_OVERLAPS' | 'a_overlaps'
              ;

//  An arithmetic expression is an expression composed of an arithmetic
//  operand (a property name, a number or a function that returns a number),
//  an arithmetic operators (+,-,*,/,%,div,^) and another arithmetic operand.
arithmeticExpression : arithmeticTerm (Sign arithmeticTerm)*;  // TODO: should we use Sign or ArithmeticOperatorPlusMinus?
Sign : '+' | '-';
ArithmeticOperatorPlusMinus : '+' | '-';

arithmeticTerm : powerTerm (arithmeticOperatorMultDiv powerTerm)*;

arithmeticOperatorMultDiv : '*' | '/' | '%' | 'div';

powerTerm : arithmeticFactor ('^' arithmeticFactor)?;

arithmeticFactor : '(' arithmeticExpression ')'
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
propertyName : Identifier | '"' Identifier '"';

Identifier : IdentifierStart IdentifierPart*
           | IdentifierStart;

COMBINING_MARKS : [\u0300-\u036F];  //  combining and diacritical marks
TIE_SYMBOLS : [\u203F\u2040];       //  ‿ and ⁀

fragment IdentifierPart : IdentifierStart
               | '.'                    //  '\u002E'
               | Digit                  //  0-9
               | COMBINING_MARKS
               | TIE_SYMBOLS
               ;

fragment IdentifierStart : [\u003A]              //  colon
                | [\u005F]              //  underscore
                | [\u0041-\u005A]    //  A-Z
                | [\u0061-\u007A]    //  a-z
                | [\u00C0-\u00D6]    //  À-Ö Latin-1 Supplement Letters
                | [\u00D8-\u00F6]    //  Ø-ö Latin-1 Supplement Letters
                | [\u00F8-\u02FF]    //  ø-ÿ Latin-1 Supplement Letters
                | [\u0370-\u037D]    //  Ͱ-ͽ Greek and Coptic (without ';')
                | [\u037F-\u1FFE]    //  See note 1.
                | [\u200C-\u200D]    //  zero width non-joiner and joiner
                | [\u2070-\u218F]    //  See note 2.
                | [\u2C00-\u2FEF]    //  See note 3.
                | [\u3001-\uD7FF]    //  See note 4.
                | [\uF900-\uFDCF]    //  See note 5.
                | [\uFDF0-\uFFFD]    //  See note 6.
                | [\u{10000}-\u{EFFFF}]  //  See note 7.
                ;

//  See: https://unicode-table.com/en/blocks/

// =============================================================================//
//  Definition of a FUNCTION
// =============================================================================//
function : Identifier '(' ')'
         | Identifier '(' argumentList ')';

argumentList : argument (',' argument)*;

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

characterClause : CASEI '(' characterExpression ')'
                | ACCENTI '(' characterExpression ')'
                | CharacterLiteral
                ;

//  Definition of CHARACTER literals
CharacterLiteral : '\'' (Alpha | Digit | Whitespace | EscapeQuote)+ '\'';

fragment EscapeQuote : '\'\'' | '\\\'';

//  character & digit productions copied from:
//  https://www.w3.org/TR/REC-xml/// charsets
fragment Alpha : [\u0007-\u0008]     //  bell, bs
      | [\u0021-\u0026]     //  !, ', // , $, %, &
      | [\u0028-\u002F]     //  (, ), *, +, comma, -, ., /
      | [\u003A-\u0084]     //  --+
      | [\u0086-\u009F]     //    |
      | [\u00A1-\u167F]     //    |
      | [\u1681-\u1FFF]     //    |
      | [\u200B-\u2027]     //    +-> :,;,<,=,>,?,@,A-Z,[,\,],^,_,`,a-z,...
      | [\u202A-\u202E]     //    |
      | [\u2030-\u205E]     //    |
      | [\u2060-\u2FFF]     //    |
      | [\u3001-\uD7FF]     //  --+
      | [\uE000-\uFFFD]     //  See note 8.
      | [\u{10000}-\u{10FFFF}]  //  See note 9.
      ;

fragment Digit : [\u0030-\u0039];

fragment Whitespace : [\u0009]  //  Character tabulation
           | [\u000A]  //  Line feed
           | [\u000B]  //  Line tabulation
           | [\u000C]  //  Form feed
           | [\u000D]  //  Carriage return
           | [\u0020]  //  Space
           | [\u0085]  //  Next line
           | [\u00A0]  //  No-break space
           | [\u1680]  //  Ogham space mark
           | [\u2000]  //  En quad
           | [\u2001]  //  Em quad
           | [\u2002]  //  En space
           | [\u2003]  //  Em space
           | [\u2004]  //  Three-per-em space
           | [\u2005]  //  Four-per-em space
           | [\u2006]  //  Six-per-em space
           | [\u2007]  //  Figure space
           | [\u2008]  //  Punctuation space
           | [\u2009]  //  Thin space
           | [\u200A]  //  Hair space
           | [\u2028]  //  Line separator
           | [\u2029]  //  Paragraph separator
           | [\u202F]  //  Narrow no-break space
           | [\u205F]  //  Medium mathematical space
           | [\u3000]  //  Ideographic space
           ;
WS : Whitespace+ -> skip;

//  Definition of NUMERIC literals
UnsignedInteger : (Digit)+;  // 1234
signedInteger : UnsignedInteger | Sign UnsignedInteger;
decimalNumericLiteral : UnsignedInteger
                      | UnsignedInteger '.'
                      | UnsignedInteger '.' UnsignedInteger
                      | '.' UnsignedInteger
                      ;

scientificNumericLiteral : decimalNumericLiteral 'E' signedInteger;
unsignedNumericLiteral : decimalNumericLiteral | scientificNumericLiteral;
signedNumericLiteral : Sign? unsignedNumericLiteral;
numericLiteral : unsignedNumericLiteral | signedNumericLiteral;


//  Boolean literal
booleanLiteral : 'TRUE' | 'FALSE' | 'true' | 'false';

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

pointTaggedText : 'POINT' 'Z'? pointText;
linestringTaggedText : 'LINESTRING' 'Z'? lineStringText;
polygonTaggedText : 'POLYGON' 'Z'? polygonText;
multipointTaggedText : 'MULTIPOINT' 'Z'? multiPointText;
multilinestringTaggedText : 'MULTILINESTRING' 'Z'? multiLineStringText;
multipolygonTaggedText : 'MULTIPOLYGON' 'Z'? multiPolygonText;
geometryCollectionTaggedText : 'GEOMETRYCOLLECTION' 'Z'? geometryCollectionText;

pointText : '(' point ')';
point : xCoord yCoord | xCoord yCoord zCoord;
xCoord : signedNumericLiteral;
yCoord : signedNumericLiteral;
zCoord : signedNumericLiteral;

lineStringText : '(' point ',' point (',' point)* ')';
linearRingText : '(' point ',' point ',' point ',' point (',' point)* ')';
polygonText : '(' linearRingText (',' linearRingText)* ')';
multiPointText : '(' pointText (',' pointText)* ')';
multiLineStringText : '(' lineStringText (',' lineStringText)* ')';
multiPolygonText : '(' polygonText (',' polygonText)* ')';
geometryCollectionText : '(' geometryLiteral (',' geometryLiteral)* ')';

bboxTaggedText : 'BBOX' bboxText;
bboxText : '(' westBoundLon ',' southBoundLat ',' (minElev ',')? eastBoundLon ',' northBoundLat (',' maxElev)? ')';
westBoundLon : signedNumericLiteral;
eastBoundLon : signedNumericLiteral;
northBoundLat : signedNumericLiteral;
southBoundLat : signedNumericLiteral;

minElev : signedNumericLiteral;
maxElev : signedNumericLiteral;

temporalInstance : instantInstance | intervalInstance;
instantInstance : dateInstant | timestampInstant;
DATE : 'DATE' | 'date';
dateInstant : DATE '(' dateInstantString ')';
dateInstantString : '\'' fullDate '\'';
TIMESTAMP : 'TIMESTAMP' | 'timestamp';
timestampInstant : 'TIMESTAMP' '(' timestampInstantString ')';
timestampInstantString : '\'' fullDate '\'T\'' utcTime '\'';  // TODO the 'T' will crash "city='Toronto'"
intervalInstance : 'INTERVAL' '(' instantParameter ',' instantParameter ')';
instantParameter : dateInstantString
                 | timestampInstantString
                 | '..'
                 | propertyName
                 | function;

fullDate   : UnsignedInteger '/' UnsignedInteger '/' UnsignedInteger;  // TODO we should use mode for 1970-01-01
utcTime  : UnsignedInteger ':' UnsignedInteger ':' decimalNumericLiteral 'Z';  // TODO we should use mode for "hh:mm:ss.sssZ"
lexer grammar Cql2Lexer;

LP    : '(';
RP    : ')';
COMMA : ',';
DOT   : '.';
DDOT  : '..';
SQ    : '\'';
DQ    : '"';
COLON : ':';

BOOL : 'TRUE' | 'FALSE' | 'true' | 'false';
AND  : 'AND'  | 'and';
OR   : 'OR'   | 'or';
NOT  : 'NOT'  | 'not';

COMP_OP : '='      //  equal
        | '<' '>'  //  not equal
        | '<'      //  less than
        | '>'      //  greater than
        | '<' '='  //  less than or equal
        | '>' '='  //  greater than or equal
        ;

SPATIAL_FUNC : 'S_INTERSECTS' | 's_intersects'
             | 'S_EQUALS'     | 's_equals'
             | 'S_DISJOINT'   | 's_disjoint'
             | 'S_TOUCHES'    | 's_touches'
             | 'S_WITHIN'     | 's_within'
             | 'S_OVERLAPS'   | 's_overlaps'
             | 'S_CROSSES'    | 's_crosses'
             | 'S_CONTAINS'   | 's_contains'
             ;

POINT      : 'POINT';
LINESTRING : 'LINESTRING';
POLYGON    : 'POLYGON';
MULTIPOINT      : 'MULTIPOINT';
MULTILINESTRING : 'MULTILINESTRING';
MULTIPOLYGON    : 'MULTIPOLYGON';
GEOMETRYCOLLECTION : 'GEOMETRYCOLLECTION';
Z : 'Z' | 'M' | 'ZM';
BBOX : 'BBOX';

TEMPORAL_FUNC : 'T_AFTER'        | 't_after'
              | 'T_BEFORE'       | 't_before'
              | 'T_CONTAINS'     | 't_contains'
              | 'T_DISJOINT'     | 't_disjoint'
              | 'T_DURING'       | 't_during'
              | 'T_EQUALS'       | 't_equals'
              | 'T_FINISHEDBY'   | 't_finishedby'
              | 'T_FINISHES'     | 't_finishes'
              | 'T_INTERSECTS'   | 't_intersects'
              | 'T_MEETS'        | 't_meets'
              | 'T_METBY'        | 't_metby'
              | 'T_OVERLAPPEDBY' | 't_overlappedby'
              | 'T_OVERLAPS'     | 't_overlaps'
              | 'T_STARTEDBY'    | 't_startedby'
              | 'T_STARTS'       | 't_starts'
              ;

ARRAY_FUNC : 'A_EQUALS'      | 'a_equals'
           | 'A_CONTAINS'    | 'a_contains'
           | 'A_CONTAINEDBY' | 'a_containedby'
           | 'A_OVERLAPS'    | 'a_overlaps'
           ;


LIKE    : 'LIKE'    | 'like';
CASEI   : 'CASEI'   | 'casei';
ACCENTI : 'ACCENTI' | 'accenti';
BETWEEN : 'BETWEEN' | 'between';
IN      : 'IN'      | 'in';
IS      : 'IS'      | 'is';
NULL    : 'NULL'    | 'null';

Sign : '+' | '-';
//ArithmeticOperatorPlusMinus : '+' | '-';
ArithmeticOperatorMultDiv : '*' | '/' | '%' | 'div';
POWER : '^';
EXP : 'E';

TIMESTAMP : 'TIMESTAMP' | 'timestamp';
DATE      : 'DATE'      | 'date';
INTERVAL  : 'INTERVAL'  | 'interval';

Identifier : IdentifierStart IdentifierPart*
           | IdentifierStart;

COMBINING_MARKS : '\u0300'..'\u036F';  //  combining and diacritical marks
TIE_SYMBOLS : '\u203F'..'\u2040';      //  ‿ and ⁀
fragment
IdentifierPart : IdentifierStart
               | '.'                    //  '\u002E'
               | Digit                  //  0-9
               | COMBINING_MARKS
               | TIE_SYMBOLS
               ;
fragment
IdentifierStart : '\u003A'              //  colon
                | '\u005F'              //  underscore
                | '\u0041'..'\u005A'    //  A-Z
                | '\u0061'..'\u007A'    //  a-z
                | '\u00C0'..'\u00D6'    //  À-Ö Latin-1 Supplement Letters
                | '\u00D8'..'\u00F6'    //  Ø-ö Latin-1 Supplement Letters
                | '\u00F8'..'\u02FF'    //  ø-ÿ Latin-1 Supplement Letters
                | '\u0370'..'\u037D'    //  Ͱ-ͽ Greek and Coptic (without ';')
                | '\u037F'..'\u1FFE'    //  See note 1.
                | '\u200C'..'\u200D'    //  zero width non-joiner and joiner
                | '\u2070'..'\u218F'    //  See note 2.
                | '\u2C00'..'\u2FEF'    //  See note 3.
                | '\u3001'..'\uD7FF'    //  See note 4.
                | '\uF900'..'\uFDCF'    //  See note 5.
                | '\uFDF0'..'\uFFFD'    //  See note 6.
                | '\u{10000}'..'\u{EFFFF}'  //  See note 7.
                ;
//  See: https://unicode-table.com/en/blocks/

//  Definition of CHARACTER literals
CharacterLiteral : '\'' (Alpha | Digit | Whitespace | EscapeQuote)+ '\'';

fragment EscapeQuote : '\'\'' | '\\\'';

//  character & digit productions copied from:
//  https://www.w3.org/TR/REC-xml/// charsets
fragment
Alpha : '\u0007'..'\u0008'     //  bell, bs
      | '\u0021'..'\u0026'     //  !, ', // , $, %, &
      | '\u0028'..'\u002F'     //  (, ), *, +, comma, -, ., /
      | '\u003A'..'\u0084'     //  --+
      | '\u0086'..'\u009F'     //    |
      | '\u00A1'..'\u167F'     //    |
      | '\u1681'..'\u1FFF'     //    |
      | '\u200B'..'\u2027'     //    +-> :,;,<,=,>,?,@,A-Z,[,\,],^,_,`,a-z,...
      | '\u202A'..'\u202E'     //    |
      | '\u2030'..'\u205E'     //    |
      | '\u2060'..'\u2FFF'     //    |
      | '\u3001'..'\uD7FF'     //  --+
      | '\uE000'..'\uFFFD'     //  See note 8.
      | '\u{10000}'..'\u{10FFFF}'  //  See note 9.
      ;

fragment
Digit : '\u0030'..'\u0039';
UnsignedInteger : Digit+;

fragment
Whitespace : '\u0009'  //  Character tabulation
           | '\u000A'  //  Line feed
           | '\u000B'  //  Line tabulation
           | '\u000C'  //  Form feed
           | '\u000D'  //  Carriage return
           | '\u0020'  //  Space
           | '\u0085'  //  Next line
           | '\u00A0'  //  No-break space
           | '\u1680'  //  Ogham space mark
           | '\u2000'  //  En quad
           | '\u2001'  //  Em quad
           | '\u2002'  //  En space
           | '\u2003'  //  Em space
           | '\u2004'  //  Three-per-em space
           | '\u2005'  //  Four-per-em space
           | '\u2006'  //  Six-per-em space
           | '\u2007'  //  Figure space
           | '\u2008'  //  Punctuation space
           | '\u2009'  //  Thin space
           | '\u200A'  //  Hair space
           | '\u2028'  //  Line separator
           | '\u2029'  //  Paragraph separator
           | '\u202F'  //  Narrow no-break space
           | '\u205F'  //  Medium mathematical space
           | '\u3000'  //  Ideographic space
           ;
WS : Whitespace+ -> skip;
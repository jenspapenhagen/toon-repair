// Toon.g4
// ANTLR4 grammar for TOON format (Java target)

grammar Toon;

options { language = Java; }

// --------------------------- PARSER RULES ---------------------------

toonFile
    : (documentLine)* EOF
    ;

documentLine
    : blankLine
    | commentLine
    | objectFieldLine
    | listItemLine
    | tabularHeaderLine
    | tabularRowBlock
    | primitiveRootLine
    ;

blankLine
    : NEWLINE
    ;

commentLine
    : COMMENT NEWLINE
    ;

primitiveRootLine
    : value NEWLINE
    ;

/* object field line uses INDENT/DEDENT produced by lexer */
objectFieldLine
    : (INDENT)? key COLON (WS_INLINE? value)? NEWLINE
    ;

/* list item: hyphen at start of line; content may be header or inline object/value */
listItemLine
    : (INDENT)? DASH WS_INLINE? listItemContent? NEWLINE
    ;

listItemContent
    : tabularHeaderLine
    | inlineObjectFragment
    | inlineCells
    | value
    ;

/* inline object fragments */
inlineObjectFragment
    : (key COLON WS_INLINE? value)
      (WS_INLINE? (COMMA | SEMI) WS_INLINE? (key COLON WS_INLINE? value))*
    ;

/* Tabular header (single line header) */
tabularHeaderLine
    : (INDENT)? LBRACK bracketLength? bracketDelimSpec? RBRACK WS_INLINE? fieldList COLON? NEWLINE
    ;

/* Block of tabular rows */
tabularRowBlock
    : (INDENT)? tabularRowLine+
    ;

tabularRowLine
    : tabularRow NEWLINE
    ;

tabularRow
    : cell (rowSep cell)*
    ;

inlineCells
    : cell (inlineSep cell)*
    ;

rowSep
    : COMMA | PIPE | TAB
    ;

inlineSep
    : COMMA | PIPE | TAB
    ;

cell
    : QUOTED_STRING
    | UNQUOTED_CELL
    ;

fieldList
    : field (fieldSep field)*
    ;

field
    : IDENT
    | QUOTED_STRING
    ;

fieldSep
    : COMMA | PIPE | TAB
    ;

key
    : IDENT
    | QUOTED_STRING
    ;

value
    : QUOTED_STRING
    | NUMBER
    | BOOL
    | NULL
    | UNQUOTED_VALUE
    ;

// bracket length/delim
bracketLength
    : NUMBER
    ;

bracketDelimSpec
    : COMMA | PIPE | TAB
    ;

// --------------------------- LEXER ---------------------------

COLON : ':' ;
LBRACK : '[' ;
RBRACK : ']' ;
SEMI : ';' ;
COMMA : ',' ;
PIPE  : '|' ;
TAB   : '\t' ;
fragment DASH_CHAR : '-' ;
DASH : DASH_CHAR ;

COMMENT : '#' ~[\r\n]* ;

NEWLINE
    : '\r'? '\n'
    ;

QUOTED_STRING
    : '"' ( ESC_SEQ | ~["\\\r\n] )* '"'
    ;

BOOL
    : 'true' | 'false'
    ;

NULL
    : 'null'
    ;

IDENT
    : [A-Za-z_] [A-Za-z0-9_]*
    ;

NUMBER
    : '-'? INT ('.' [0-9]+)? EXP?
    ;

fragment INT
 // integer part forbids leading 0s (e.g. `01`)
    : '0'
    | [1-9] [0-9]*
    ;

// no leading zeros
fragment EXP
 // exponent number permits leading 0s (e.g. `1e01`)
    : [Ee] [+-]? [0-9]+
    ;

UNQUOTED_VALUE
    : IDENT
    ;

UNQUOTED_CELL
    : ~[\r\n[\]\\,| \t]+              // unquoted table cell content (restricted to exclude whitespace)
    ;


fragment HEX : [0-9a-fA-F] ;

WS_INLINE : [ \t]+ -> skip ;

INDENT : '<INDENT>' ;  // Placeholder for custom lexer logic
DEDENT : '<DEDENT>' ;  // Placeholder for custom lexer logic

fragment ESC_SEQ
    : '\\' (['"\\/bfnrt] | 'u' HEX HEX HEX HEX)
    ;

import java_cup.runtime.*;
%%
//%debug
%cup

Int = 0|[1-9][0-9]*
Rexp = [eE][+-]?[0-9]+
Real = (0|[0-9])+\.[0-9]*{Rexp}?|[0-9]*\.(0|[0-9])+{Rexp}?|[0-9]+{Rexp}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*
Comment =  {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

%%

/* Reserved words */
"int"                   { return new Symbol(sym.VAR_INT); }
"float"                 { return new Symbol(sym.VAR_FLOAT); }
"(int)"                 { return new Symbol(sym.CAST_INT); }
"(float)"               { return new Symbol(sym.CAST_FLOAT); }
"to"                    { return new Symbol(sym.TO); }
"in"                    { return new Symbol(sym.IN); }
"downto"                { return new Symbol(sym.DOWNTO); }
"step"                  { return new Symbol(sym.STEP); }
"if"                    { return new Symbol(sym.IF); }
"else"                  { return new Symbol(sym.ELSE); }
"while"                 { return new Symbol(sym.WHILE); }
"do"                    { return new Symbol(sym.DO); }
"for"                   { return new Symbol(sym.FOR); }
"print"                 { return new Symbol(sym.PRINT); }

/* Separators */
"("                     { return new Symbol(sym.OP); }
")"                     { return new Symbol(sym.CP); }
"{"                     { return new Symbol(sym.OB); }
"}"                     { return new Symbol(sym.CB); }
"["                     { return new Symbol(sym.OSB); }
"]"                     { return new Symbol(sym.CSB); }
";"                     { return new Symbol(sym.SEMICOLON); }
","                     { return new Symbol(sym.COMMA); }

/* Operators */
"++"                    { return new Symbol(sym.INC); }
"--"                    { return new Symbol(sym.DEC); }
"%"                     { return new Symbol(sym.MOD); }
"="                     { return new Symbol(sym.ASIG); }
"+"                     { return new Symbol(sym.PLUS); }
"-"                     { return new Symbol(sym.MINUS); }
"*"                     { return new Symbol(sym.MUL); }
"/"                     { return new Symbol(sym.DIV); }
"<"                     { return new Symbol(sym.LT); }
">"                     { return new Symbol(sym.GT); }
"<="                    { return new Symbol(sym.LTOREQ); }
">="                    { return new Symbol(sym.GTOREQ); }
"=="                    { return new Symbol(sym.EQ); }
"!="                    { return new Symbol(sym.NOTEQ); }
"&&"                    { return new Symbol(sym.AND); }
"||"                    { return new Symbol(sym.OR); }
"!"                     { return new Symbol(sym.NOT); }

/* Numbers */
{Int}                   { return new Symbol(sym.INT, yytext()); }
{Real}                  { return new Symbol(sym.FLOAT, yytext());}  

/* Variables */
[a-zA-Z_][a-zA-Z0-9]*   { return new Symbol(sym.VAR, yytext()); }

/* Everything else */
{Comment}               {}
[^]                     {}
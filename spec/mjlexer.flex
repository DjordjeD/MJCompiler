package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

%%

%{
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}

	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}
%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" " 	{ }
"\b" 	{ }
"\t" 	{ }
"\r\n" 	{ }
"\n"	{}
"\f" 	{ }

"program"   { return new_symbol(sym.PROG, yytext()); }
"break"		{ return new_symbol(sym.BREAK, yytext()); }
"else"		{ return new_symbol(sym.ELSE, yytext()); }
"const"		{ return new_symbol(sym.CONST, yytext()); }
"if"		{ return new_symbol(sym.IF, yytext()); }
"do"		{ return new_symbol(sym.DO, yytext()); }
"while"		{ return new_symbol(sym.WHILE, yytext()); }
"new"		{ return new_symbol(sym.NEW, yytext()); }
"print" 	{ return new_symbol(sym.PRINT, yytext()); }
"read"		{ return new_symbol(sym.READ, yytext()); }
"return" 	{ return new_symbol(sym.RETURN, yytext()); }
"void" 		{ return new_symbol(sym.VOID, yytext()); }
"continue"	{ return new_symbol(sym.CONTINUE, yytext()); }
"goto" 		{ return new_symbol(sym.GOTO, yytext()); }
"record"    { return new_symbol(sym.RECORD, yytext()); }

"+" 		{ return new_symbol(sym.PLUS, yytext()); }
"-" 		{ return new_symbol(sym.MINUS, yytext()); }
"*" 		{ return new_symbol(sym.MUL, yytext()); }
"/" 		{ return new_symbol(sym.DIV, yytext()); }
"%" 		{ return new_symbol(sym.MOD, yytext()); }

"=="		{ return new_symbol(sym.RELEQUAL, yytext()); }
"!="		{ return new_symbol(sym.NOTEQUAL, yytext()); }
">"			{ return new_symbol(sym.GREATER, yytext()); }
">="		{ return new_symbol(sym.GREATEREQ, yytext()); }
"<"			{ return new_symbol(sym.LESS, yytext()); }
"<="		{ return new_symbol(sym.LESSEQ, yytext()); }
"&&"		{ return new_symbol(sym.AND, yytext()); }
"||"		{ return new_symbol(sym.OR, yytext()); }

"=" 		{ return new_symbol(sym.EQUAL, yytext()); }
"++" 		{ return new_symbol(sym.INCR, yytext()); }
"--" 		{ return new_symbol(sym.DECR, yytext()); }
";" 		{ return new_symbol(sym.SEMI, yytext()); }
"," 		{ return new_symbol(sym.COMMA, yytext()); }
"." 		{ return new_symbol(sym.DOT, yytext()); }

"(" 		{ return new_symbol(sym.LPAREN, yytext()); }
")" 		{ return new_symbol(sym.RPAREN, yytext()); }
"{" 		{ return new_symbol(sym.LBRACE, yytext()); }
"}"			{ return new_symbol(sym.RBRACE, yytext()); }
"[" 		{ return new_symbol(sym.LSQUARE, yytext()); }
"]"			{ return new_symbol(sym.RSQUARE, yytext()); }
"?" 		{ return new_symbol(sym.QUESTION, yytext()); }
":"			{ return new_symbol(sym.COLON, yytext()); }

"//" 		     { yybegin(COMMENT); }
<COMMENT> .      { yybegin(COMMENT); }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }
<COMMENT> "\n" { yybegin(YYINITIAL); }

"true"|"false"		{ return new_symbol(sym.BOOLEAN, "true".equals(yytext())); }
"'"."'"				{ return new_symbol(sym.CHARACTER, yytext().charAt(1)); }
[1-9][0-9]*|0  		{ return new_symbol(sym.NUMBER, Integer.parseInt(yytext())); }
[a-zA-Z][a-zA-Z0-9_]* 	{return new_symbol(sym.IDENT, yytext()); }

. { System.err.println("Lexical error ("+yytext()+") in line "+(yyline+1)); }

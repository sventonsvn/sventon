/*
 * Copyright 2008 Gábor Fehér <feherga@gmail.com>
 * Copyright 2000-2006 Omnicore Software, Hans Kratz & Dennis Strein GbR,
 *                     Geert Bevin <gbevin[remove] at uwyn dot com>.
 * Distributed under the terms of either:
 * - the common development and distribution license (CDDL), v1.0; or
 * - the GNU Lesser General Public License, v2.1 or later
 * $Id$
 */
package com.uwyn.jhighlight.highlighter;

import java.io.Reader;
import java.io.IOException;

%%

%class CSharpHighlighter
%implements ExplicitStateHighlighter

%unicode
%pack

%buffer 128

%public

%int

%{
	/* styles */
	
	public static final byte PLAIN_STYLE = 1;
	public static final byte KEYWORD_STYLE = 2;
	public static final byte TYPE_STYLE = 3;
	public static final byte OPERATOR_STYLE = 4;
	public static final byte SEPARATOR_STYLE = 5;
	public static final byte STRING_LITERAL_STYLE = 61;
        public static final byte NUM_LITERAL_STYLE = 62;
        public static final byte CHAR_LITERAL_STYLE = 63;
	public static final byte COMMENT_STYLE = 7;
        public static final byte DIRECTIVE_STYLE = 8;
	
	/* Highlighter implementation */
	
	public int getStyleCount()
	{
		return 9;
	}
	
	public byte getStartState()
	{
		return YYINITIAL+1;
	}
	
	public byte getCurrentState()
	{
		return (byte) (yystate()+1);
	}
	
	public void setState(byte newState)
	{
		yybegin(newState-1);
	}
	
	public byte getNextToken()
	throws IOException
	{
		return (byte) yylex();
	}
	
	public int getTokenLength()
	{
		return yylength();
	}
	
	public void setReader(Reader r)
	{
		this.zzReader = r;
	}

	public CSharpHighlighter()
	{
	}
%}

/* main character classes */

WhiteSpace = [ \t\f]

Identifier = "@"? {IdentifierOrKeyword}
IdentifierOrKeyword =
    {IdentifierStartCharacter} {IdentifierPartCharacter}*
IdentifierStartCharacter = [:letter:] | _
IdentifierPartCharacter = [:letter:] | [:digit:] | "_" | "-" //FIXME

//http://www.jaggersoft.com/csharp_grammar.html#real-literal
//numeric literals
NumericLiteral = {IntegerLiteral} | {RealLiteral}

IntegerLiteral = {DecimalIntegerLiteral} | {HexadecimalIntegerLiteral}
DecimalIntegerLiteral = {DecimalDigits} {IntegerTypeSuffix}?
DecimalDigits = {DecimalDigit}+
IntegerTypeSuffix = U | u  | L | l | UL | Ul | uL | ul | LU | Lu | lU | lu 
HexadecimalIntegerLiteral =
    ("0x" | "0X") {HexDigit}+ {IntegerTypeSuffix}?

RealLiteral = 
    {DecimalDigit}* . {DecimalDigit}+ {ExponentPart}? {RealTypeSuffix}?
    | {DecimalDigit}+ {ExponentPart} {RealTypeSuffix}?
    | {DecimalDigit}+ {RealTypeSuffix}
ExponentPart = [Ee] [+-]? {DecimalDigit}?
RealTypeSuffix = F | f | D | d | M | m 

CharacterLiteral = ' {Character} ' 
Character = 
    {SingleCharacter} 
    | {SimpleEscapeSequence}
    | {HexadecimalEscapeSequence} 
    | {UnicodeCharacterEscapeSequence} 
SingleCharacter = [^'\\\n]
SimpleEscapeSequence = "\\'" | "\\\"" | "\\\\" | "\\a" | "\\b" 
                     | "\\f" | "\\n" | "\\r" | "\\t" | "\\v"
HexadecimalEscapeSequence = "\\x" {HexDigit} {HexDigit}? {HexDigit}? {HexDigit}?
UnicodeCharacterEscapeSequence = 
    "\\u" {HexDigit} {HexDigit} {HexDigit} {HexDigit}
    | "\\U" {HexDigit} {HexDigit} {HexDigit} {HexDigit} {HexDigit} {HexDigit} {HexDigit} {HexDigit}

StringLiteral = {RegularStringLiteral} | {VerbatimStringLiteral}
RegularStringLiteral =  "\"" {RegularStringLiteralCharacter}+ "\""
RegularStringLiteralCharacter = {SingleCharacter}
VerbatimStringLiteral = "\"@" {VerbatimStringLiteralCharacter}+ "\""
VerbatimStringLiteralCharacter = 
    [^\"] | "\"" "\""


HexDigit = [0-9a-fA-F]
DecimalDigit = [0-9]

//Comment = {SingleLineComment} | {DelimitedComment}
SingleLineComment = "//" [^\n]*
//DelimitedComment = "/*" {DelimitedCommentSection}* "*"+ "/"
//DelimitedCommentSection = 
//    [^*]
//    | [*]+ [^/]
//    | [.]


%state IN_COMMENT, IN_DIRECTIVE
%%

<YYINITIAL> {


    "abstract" |
    "event" |
    "new" |
    "struct" |
    "as" |
    "explicit" |
    "null" |
    "switch" |
    "base" |
    "extern" |
    "object" |
    "this" |
    "false" |
    "operator" |
    "throw" |
    "break" |
    "finally" |
    "out" |
    "true" |
    "fixed" |
    "override"
    "try" |
    "case" |
    "params" |
    "typeof" |
    "catch" |
    "for" |
    "private" |
    "foreach" |
    "protected" |
    "checked" |
    "goto" |
    "public" |
    "unchecked" |
    "class" |
    "if" |
    "readonly" |
    "unsafe" |
    "const" |
    "implicit" |
    "ref" |
    "ushort" |
    "continue" |
    "in" |
    "return" |
    "using" |
    "decimal" |
    "virtual" |
    "default" |
    "interface" |
    "sealed" |
    "volatile" |
    "delegate" |
    "internal" |
    "do" |
    "is" |
    "sizeof" |
    "while" |
    "lock" |
    "stackalloc" |
    "else" |
    "static" |
    "enum" |
    "namespace"
        { return KEYWORD_STYLE; }

    "byte" |
    "char" |
    "int" |
    "sbyte" |
    "ulong" |
    "short" |
    "void" |
    "double" |        
    "bool" | 
    "float" |
    "long" |
    "uint" |
    "string"
   { return TYPE_STYLE; }

  /* num literals */
  {NumericLiteral}
	{ return NUM_LITERAL_STYLE; }

  /* string literals */
  {StringLiteral}
        { return STRING_LITERAL_STYLE; }
  {CharacterLiteral}
        { return CHAR_LITERAL_STYLE; }

  /* separators */
    "{" | "}" | "[" | "]" | "(" | ")" | "." | "," | ":" | ";"
        { return SEPARATOR_STYLE; }
  
  /* operators */
    "+" | "-" | "*" | "/" | "%" | "&" | "|" | "^" | "!" | "~" 
    | "=" | "<" | ">" | "?" | "++" | "--" | "&&" | "||" | "<<" | ">>" 
    | "==" | "!=" | "<=" | ">=" | "+=" | "-=" | "*=" | "/=" | "%=" | "&=" 
    | "|=" | "^=" | "<<=" | ">>=" | "->"
        { return OPERATOR_STYLE; }


  \n |
  {Identifier} |
  {WhiteSpace}                   { return PLAIN_STYLE; }


  {SingleLineComment} { return COMMENT_STYLE; }
  "/*" { yybegin(IN_COMMENT); return COMMENT_STYLE; }

  
    ("#if" 
    | "#else"
    | "#elif"
    | "#endif"
    | "#define"
    | "#undef"
    | "#warning"
    | "#error"
    | "#line"
    | "#region"
    | "#endregion") [^\n]* "\n" 
        {return DIRECTIVE_STYLE;}


}


<IN_COMMENT>  {
    [^\*]* |
    ([^\*]*\*[^/])*
        { return COMMENT_STYLE; }

    [^\*]* |
    ([^\*]*\*[^/])* \*\/
        { yybegin(YYINITIAL); return COMMENT_STYLE; }
}



/* error fallback */

.|\n                             { return PLAIN_STYLE; }

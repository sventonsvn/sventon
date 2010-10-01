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

%class PascalHighlighter
%implements ExplicitStateHighlighter

%unicode
%pack
%ignorecase

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
	public static final byte COMMENT_STYLE = 7;
	
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

	public PascalHighlighter()
	{
	}
%}

/* main character classes */

WhiteSpace = [ \t\f]

Identifier = [a-zA-Z_][a-zA-Z0-9_]*

//numeric literals

HexDigit = [0-9a-fA-F]
OctDigit = [0-7]
Digit = [0-9]
BinDigit = [0-1]
HexDigitSeq = {HexDigit}+
OctDigitSeq = {OctDigit}+
BinDigitSeq = {BinDigit}+
DigitSeq = {Digit}+

UnsignedInt = {DigitSeq} | ("$" {HexDigitSeq}) | ("%" {BinDigitSeq}) | ("&" {OctDigitSeq})
ScaleFactor = [Ee][+-]?{DigitSeq}
UnsignedReal = {DigitSeq} (\. {DigitSeq} )? {ScaleFactor} ?

NumericLiteral = [+-]? {UnsignedInt} | {UnsignedReal}


%state IN_COMMENT1, IN_COMMENT2

%%

<YYINITIAL> {


  /* Trubo Pascal keywords */
    "absolute" |
    "and" |
    "asm" |
    "begin" |
    "break" |
    "case" |
    "const" |
    "constructor" |
    "continue" |
    "destructor" |
    "div" |
    "do" |
    "downto" |
    "else" |
    "end" |
    "for" |
    "function" |
    "goto" |
    "if " |
    "implementation" |
    "in" |
    "inherited" |
    "inline" |
    "interface" |
    "label" |
    "mod" |
    "nil"
    "not" |
    "object" |
    "on" |
    "operator" |
    "or" |
    "packed" |
    "procedure" |
    "program" |
    "reintroduce" |
    "repeat" |
    "self" |
    "set" |
    "shl" |
    "shr" |
    "then" |
    "to" |
    "type" |
    "unit" |
    "until" |
    "uses" |
    "var" |
    "while" |
    "with" |
    "xor" |
 /* Delphi keywords */
    "as" |
    "class" |
    "except" |
    "exports" |
    "finalization" |
    "finally" |
    "initialization" |
    "is" |
    "library" |
    "on" |
    "out" |
    "property" |
    "raise" |
    "threadvar" |
    "try" |
/* Freepascal keywords */
    "dispose" |
    "exit" |
    "false" |
    "new" |
    "true" |
/* More keywords*/
    "not" 
        { return KEYWORD_STYLE; }
        
    "array" |
    "of" |
    "record" |
    "string" |
    "Text" |
    "File" |

    "Name" |
    "Integer" |
    "Shortint" |
    "SmallInt" |
    "Longint" |
    "Longword" |
    "Int64" |
    "Byte" |
    "Word" |
    "Cardinal" |
    "QWord" |
    "Boolean" |
    "ByteBool" |
    "LongBool" |
    "Char" |
    "Pointer" |
    "PChar" |
    "AnsiString" |
    "ShortString" |
    "WideChar" |
    "WideString"
   { return TYPE_STYLE; }


  /* num literals */
  {NumericLiteral} /*|
  "$" [0-9]+*/
	{ return NUM_LITERAL_STYLE; }

  /* string literals */
     ( ( \' [^\'\n]* \' ) | (\#[0-9]+) ) *
        { return STRING_LITERAL_STYLE; }
  
  /* separators */
  "(" |
  ")" |
  "[" |
  "]" |
  ";" |
  "," |
  "."                          { return SEPARATOR_STYLE; }
  
  /* operators */
  "=" |
  ">" |
  "<" |
  "!" |
  "~" |
  "?" |
  ":" |
  "+" |
  "-" |
  "*" |
  "/" |
  "&" |
  "|" |
  "^" |
  "%"                      { return OPERATOR_STYLE; }


  \n |
  {Identifier} |
  {WhiteSpace}                   { return PLAIN_STYLE; }


// single line comment

  "//" [^\n]* \n |

// short comment

  "(**)"   	{ return COMMENT_STYLE; }
  
// comment start
  
  "(*"     { yybegin(IN_COMMENT1); return COMMENT_STYLE;}
  "{"      { yybegin(IN_COMMENT2);  return COMMENT_STYLE;}

}


<IN_COMMENT1> {
    
    [^\*]* |
    ([^\*]*\*[^\)])*
        { return COMMENT_STYLE; }

    [^\*]* |
    ([^\*]*\*[^\)])* \*\)
        { yybegin(YYINITIAL); return COMMENT_STYLE; }
}

<IN_COMMENT2> {

  // comment unterminated
  
  [^\}]*    { return COMMENT_STYLE; }

  // comment terminated

  [^\}]*\}  { yybegin(YYINITIAL); return COMMENT_STYLE; }
  
}



/* error fallback */

.|\n                             { return PLAIN_STYLE; }

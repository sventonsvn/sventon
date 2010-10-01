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

%class VisualBasicHighlighter
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
        public static final byte CHAR_LITERAL_STYLE = 63;
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

	public VisualBasicHighlighter()
	{
	}
%}

/* main character classes */

WhiteSpace = [ \t\f]

Identifier = {Identifier0} {TypeCharacter} ?
TypeCharacter = [\%\&\@\!\#\$]
Identifier0 = ([:letter:] | "_") ([:letter:] | [:digit:] | '_')*

//numeric literals
NumericLiteral = {IntegerLiteral} | {FloatingPointLiteral}
IntegerLiteral  =  {IntegralLiteralValue}  {IntegralTypeCharacter}?
IntegralLiteralValue = {IntLiteral} | {HexLiteral} | {OctalLiteral}

IntegralTypeCharacter = "S" | "US" | "I" | "UI" | "L" | "UL" | "%" | "&"
IntLiteral  =  {Digit}+
HexLiteral  =  "&H"  {HexDigit}+
OctalLiteral  =  "&O"  {OctalDigit}+
Digit  =  [0-9] //0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9
HexDigit  =  [0-9A-F] //0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9  |  A  |  B  |  C  |  D  |  E  |  F
OctalDigit  =  [0-7] //0  |  1  |  2  |  3  |  4  |  5  |  6  |  7


FloatingPointLiteral  =
	{FloatingPointLiteralValue}  {FloatingPointTypeCharacter}?
	{IntLiteral}  {FloatingPointTypeCharacter}
FloatingPointTypeCharacter  = "F" | "R" | "D" | "!" | "#" | "@"
FloatingPointLiteralValue  = 
    {IntLiteral} "." {IntLiteral} {Exponent}?  
    | "."  {IntLiteral} {Exponent} ?
    | {IntLiteral} {Exponent}

Exponent  =  "E" {Sign}? {IntLiteral}
Sign  =  [+-]

StringLiteral = {DoubleQuoteCharacter} {StringCharacter}+ {DoubleQuoteCharacter}
DoubleQuoteCharacter  =	"\""  
StringCharacter  =
        [^\"] //FIXME
        | {DoubleQuoteCharacter} {DoubleQuoteCharacter}

CharacterLiteral =  {DoubleQuoteCharacter}  {StringCharacter}  {DoubleQuoteCharacter}  C



SingleLineComment = ("REM" | "'") [^\n]*

%%

<YYINITIAL> {

    "AddHandler"
    | "AddressOf"
    | "Alias"
    | "And"
    | "AndAlso"
    | "As"
    | "ByRef"
    | "ByVal"
    | "Call"
    | "Case"
    | "Catch"
    | "Class"
    | "CObj"
    | "Const"
    | "Continue"
    | "Declare"
    | "Default"
    | "Delegate"
    | "Dim"
    | "DirectCast"
    | "Do"
    | "Each"
    | "Else"
    | "ElseIf"
    | "End"
    | "EndIf"
    | "Erase"
    | "Error"
    | "Event"
    | "Exit"
    | "False"
    | "Finally"
    | "For"
    | "Friend"
    | "Function"
    | "Get"
    | "GetType"
    | "GetXmlNamespace"
    | "Global"
    | "GoSub"
    | "GoTo"
    | "Handles"
    | "If"
    | "Implements"
    | "Imports"
    | "In"
    | "Inherits"
    | "Interface"
    | "Is"
    | "IsNot"
    | "Let"
    | "Lib"
    | "Like"
    | "Loop"
    | "Me"
    | "Mod"
    | "Module"
    | "MustInherit"
    | "MustOverride"
    | "MyBase"
    | "MyClass"
    | "Namespace"
    | "Narrowing"
    | "New"
    | "Next"
    | "Not"
    | "Nothing"
    | "NotInheritable"
    | "NotOverridable"
    | "Object"
    | "Of"
    | "On"
    | "Operator"
    | "Option"
    | "Optional"
    | "Or"
    | "OrElse"
    | "Overloads"
    | "Overridable"
    | "Overrides"
    | "ParamArray"
    | "Partial"
    | "Private"
    | "Property"
    | "Protected"
    | "Public"
    | "RaiseEvent"
    | "ReadOnly"
    | "ReDim"
    | "REM"
    | "RemoveHandler"
    | "Resume"
    | "Return"
    | "Select"
    | "Set"
    | "Shadows"
    | "Shared"
    | "Single"
    | "Static"
    | "Step"
    | "Stop"
    | "Structure"
    | "Sub"
    | "SyncLock"
    | "Then"
    | "Throw"
    | "To"
    | "True"
    | "Try"
    | "TryCast"
    | "TypeOf"
    | "Using"
    | "Varint"
    | "Went"
    | "When"
    | "While"
    | "Widening"
    | "With"
    | "WithEvents"
    | "WriteOnly"
    | "Xor"
        { return KEYWORD_STYLE; }

    "Boolean" 
    | "Byte"
    | "CBool"
    | "CByte"
    | "CChar"
    | "CDate"
    | "CDbl"
    | "CDec"
    | "Char"
    | "CInt"
    | "CLng"
    | "CSByte"
    | "CShort"
    | "CSng"
    | "CStr"
    | "CType"
    | "CUInt"
    | "CULng"
    | "CUShort"
    | "Date"
    | "Decimal"
    | "Double"
    | "Enum"
    | "Integer"
    | "Long"
    | "SByte"
    | "Short"
    | "String"
    | "UInteger"
    | "ULong"
    | "UShort"
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
  
}

/* error fallback */

.|\n                             { return PLAIN_STYLE; }

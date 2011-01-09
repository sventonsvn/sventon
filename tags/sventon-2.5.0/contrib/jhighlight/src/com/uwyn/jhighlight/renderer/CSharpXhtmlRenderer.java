/*
 * Copyright 2008 Gábor Fehér <feherga@gmail.com>
 * Copyright 2004-2006 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Distributed under the terms of either:
 * - the common development and distribution license (CDDL), v1.0; or
 * - the GNU Lesser General Public License, v2.1 or later
 * $Id: JavaXhtmlRenderer.java 3108 2006-03-13 18:03:00Z gbevin $
 */
package com.uwyn.jhighlight.renderer;

import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;
import com.uwyn.jhighlight.highlighter.CSharpHighlighter;
import com.uwyn.jhighlight.renderer.XhtmlRenderer;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates highlighted syntax in XHTML from Java source.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3108 $
 * @since 1.0
 */
public class CSharpXhtmlRenderer extends XhtmlRenderer
{
	public final static HashMap DEFAULT_CSS = new HashMap() {{
			put("h1",
				"font-family: sans-serif; " +
				"font-size: 16pt; " +
				"font-weight: bold; " +
				"color: rgb(0,0,0); " +
				"background: rgb(210,210,210); " +
				"border: solid 1px black; " +
				"padding: 5px; " +
				"text-align: center;");
			
			put("code",
				"color: rgb(0,0,0); " +
				"font-family: monospace; " +
				"font-size: 12px; " +
				"white-space: nowrap;");
			
			put(".csharp_plain",
				"color: rgb(0,0,0);");
			
			put(".csharp_keyword",
				"color: rgb(0,0,0); " +
				"font-weight: bold;");
			
			put(".csharp_type",
				"color: rgb(128,0,0);");
			
			put(".csharp_operator",
				"color: rgb(0,124,31);");
			
			put(".csharp_separator",
				"color: rgb(0,33,255);");
			
			put(".csharp_string_literal",
				"color: rgb(255,0,0);");
			put(".csharp_char_literal",
				"color: rgb(255,0,0);");                        
			put(".csharp_num_literal",
				"color: rgb(0,0,255);");			
			put(".csharp_comment",
				"color: rgb(147,147,147); " +
				"background-color: rgb(247,247,247);");
			put(".csharp_directive",
				"color: rgb(0,147,0); ");
		}};
	
	protected Map getDefaultCssStyles()
	{
		return DEFAULT_CSS;
	}
		
	protected String getCssClass(int style)
	{
		switch (style)
		{
			case CSharpHighlighter.PLAIN_STYLE:
				return "csharp_plain";
			case CSharpHighlighter.KEYWORD_STYLE:
				return "csharp_keyword";
			case CSharpHighlighter.TYPE_STYLE:
				return "csharp_type";
			case CSharpHighlighter.OPERATOR_STYLE:
				return "csharp_operator";
			case CSharpHighlighter.SEPARATOR_STYLE:
				return "csharp_separator";
			case CSharpHighlighter.STRING_LITERAL_STYLE:
				return "csharp_string_literal";
                        case CSharpHighlighter.NUM_LITERAL_STYLE:                                
                                return "csharp_num_literal";
                        case CSharpHighlighter.CHAR_LITERAL_STYLE:                                
                                return "csharp_char_literal";                                
			case CSharpHighlighter.COMMENT_STYLE:
				return "csharp_comment";
			case CSharpHighlighter.DIRECTIVE_STYLE:
				return "csharp_directive";
		}
		
		return null;
	}
	
	protected ExplicitStateHighlighter getHighlighter()
	{
		CSharpHighlighter highlighter = new CSharpHighlighter();
		
		return highlighter;
	}
}


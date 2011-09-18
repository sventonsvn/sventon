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
import com.uwyn.jhighlight.highlighter.VisualBasicHighlighter;
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
public class VisualBasicXhtmlRenderer extends XhtmlRenderer
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
			
			put(".vb_plain",
				"color: rgb(0,0,0);");
			
			put(".vb_keyword",
				"color: rgb(0,0,0); " +
				"font-weight: bold;");
			
			put(".vb_type",
				"color: rgb(128,0,0);");
			
			put(".vb_operator",
				"color: rgb(0,124,31);");
			
			put(".vb_separator",
				"color: rgb(0,33,255);");
			
			put(".vb_string_literal",
				"color: rgb(255,0,0);");
			put(".vb_char_literal",
				"color: rgb(255,0,0);");                        
			put(".vb_num_literal",
				"color: rgb(0,0,255);");			
			put(".vb_comment",
				"color: rgb(147,147,147); " +
				"background-color: rgb(247,247,247);");
		}};
	
	protected Map getDefaultCssStyles()
	{
		return DEFAULT_CSS;
	}
		
	protected String getCssClass(int style)
	{
		switch (style)
		{
			case VisualBasicHighlighter.PLAIN_STYLE:
				return "vb_plain";
			case VisualBasicHighlighter.KEYWORD_STYLE:
				return "vb_keyword";
			case VisualBasicHighlighter.TYPE_STYLE:
				return "vb_type";
			case VisualBasicHighlighter.OPERATOR_STYLE:
				return "vb_operator";
			case VisualBasicHighlighter.SEPARATOR_STYLE:
				return "vb_separator";
			case VisualBasicHighlighter.STRING_LITERAL_STYLE:
				return "vb_string_literal";
                        case VisualBasicHighlighter.NUM_LITERAL_STYLE:                                
                                return "vb_num_literal";
                        case VisualBasicHighlighter.CHAR_LITERAL_STYLE:                                
                                return "vb_char_literal";                                
			case VisualBasicHighlighter.COMMENT_STYLE:
				return "vb_comment";
		}
		
		return null;
	}
	
	protected ExplicitStateHighlighter getHighlighter()
	{
		VisualBasicHighlighter highlighter = new VisualBasicHighlighter();
		
		return highlighter;
	}
}


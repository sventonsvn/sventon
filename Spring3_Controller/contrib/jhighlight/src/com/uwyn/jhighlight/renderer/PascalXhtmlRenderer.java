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
import com.uwyn.jhighlight.highlighter.PascalHighlighter;
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
public class PascalXhtmlRenderer extends XhtmlRenderer
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
			
			put(".pascal_plain",
				"color: rgb(0,0,0);");
			
			put(".pascal_keyword",
				"color: rgb(0,0,0); " +
				"font-weight: bold;");
			
			put(".pascal_type",
				"color: rgb(128,0,0);");
			
			put(".pascal_operator",
				"color: rgb(0,124,31);");
			
			put(".pascal_separator",
				"color: rgb(0,33,255);");
			
			put(".pascal_string_literal",
				"color: rgb(255,0,0);");
			put(".pascal_num_literal",
				"color: rgb(0,0,255);");			
			put(".pascal_comment",
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
			case PascalHighlighter.PLAIN_STYLE:
				return "pascal_plain";
			case PascalHighlighter.KEYWORD_STYLE:
				return "pascal_keyword";
			case PascalHighlighter.TYPE_STYLE:
				return "pascal_type";
			case PascalHighlighter.OPERATOR_STYLE:
				return "pascal_operator";
			case PascalHighlighter.SEPARATOR_STYLE:
				return "pascal_separator";
			case PascalHighlighter.STRING_LITERAL_STYLE:
				return "pascal_string_literal";
                        case PascalHighlighter.NUM_LITERAL_STYLE:                                
                                return "pascal_num_literal";
			case PascalHighlighter.COMMENT_STYLE:
				return "pascal_comment";
		}
		
		return null;
	}
	
	protected ExplicitStateHighlighter getHighlighter()
	{
		PascalHighlighter highlighter = new PascalHighlighter();
		
		return highlighter;
	}
}


/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.tags;

import de.berlios.sventon.util.ByteFormatter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Locale;
import java.io.IOException;

/**
 * JSP Tag for formatting bytes size values.
 *
 * @author jesper@users.berlios.de
 * @see de.berlios.sventon.util.ByteFormatter
 */
public class ByteFormatterTag extends TagSupport {

  private long size;
  private Locale locale;

  /**
   * {@inheritDoc} 
   */
  public int doStartTag() throws JspException {
    if (size > 0) {
      try {
        pageContext.getOut().write(ByteFormatter.format(size, locale));
      } catch (final IOException e) {
        throw new JspException(e);
      }
    }
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Sets the byte size to format.
   *
   * @param size Size to format
   */
  public void setSize(final long size) {
    this.size = size;
  }

  /**
   * Sets the locale to use when formatting.
   *
   * @param locale Locale
   */
  public void setLocale(final Locale locale) {
    this.locale = locale;
  }

}
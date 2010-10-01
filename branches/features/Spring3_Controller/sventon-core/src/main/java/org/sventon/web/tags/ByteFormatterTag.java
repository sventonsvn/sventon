/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.tags;

import org.sventon.util.ByteFormatter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Locale;

/**
 * JSP Tag for formatting bytes size values.
 *
 * @author jesper@sventon.org
 * @see org.sventon.util.ByteFormatter
 */
public final class ByteFormatterTag extends TagSupport {

  private static final long serialVersionUID = -7625429549478354923L;

  /**
   * The size.
   */
  private long size;
  /**
   * Locale.
   */
  private Locale locale;

  @Override
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

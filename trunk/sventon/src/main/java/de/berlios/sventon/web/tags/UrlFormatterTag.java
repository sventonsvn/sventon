/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * JSP Tag that unescapes legal URL characters (ie. slash and colon).
 *
 * @author jesper@users.berlios.de
 */
public final class UrlFormatterTag extends TagSupport {

  /**
   * The url.
   */
  private String url;

  /**
   * {@inheritDoc}
   */
  @Override
  public int doStartTag() throws JspException {
    try {
      pageContext.getOut().write(unescapeLegalCharacters(url));
    } catch (final IOException e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

  protected String unescapeLegalCharacters(final String url) {
    String result = url;
    // un-encode colons
    result = result.replaceAll("(?i)%3A", ":");
    // un-encode forward slashes
    result = result.replaceAll("(?i)%2F", "/");
    return result;
  }

  /**
   * Sets the URL
   *
   * @param url URL.
   */
  public void setUrl(final String url) {
    this.url = url;
  }

}

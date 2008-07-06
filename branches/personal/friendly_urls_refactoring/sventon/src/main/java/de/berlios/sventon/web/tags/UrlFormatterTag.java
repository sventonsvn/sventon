/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.tags;

import de.berlios.sventon.util.EncodingUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * JSP Tag that unescapes legal URL characters (ie. slash and colon).
 *
 * @author jesper@users.berlios.de
 * @see de.berlios.sventon.util.EncodingUtils
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
      pageContext.getOut().write(EncodingUtils.encodeUrl(url));
    } catch (final IOException e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Sets the URL.
   *
   * @param url URL.
   */
  public void setUrl(final String url) {
    this.url = url;
  }

}

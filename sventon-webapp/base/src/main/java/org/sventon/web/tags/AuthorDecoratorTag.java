/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.tags;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;
import org.sventon.web.transform.AuthorNameTransformer;

/**
 * JSP Tag for decorating/transforming author names.
 *
 * @author jesper@sventon.org
 */
public final class AuthorDecoratorTag extends RequestContextAwareTag {

  private static final long serialVersionUID = -7625429549478354923L;

  /**
   * The author.
   */
  private String author;

  @Override
  protected int doStartTagInternal() throws Exception {
    String result = author;

    try {
      final WebApplicationContext webApplicationContext = getRequestContext().getWebApplicationContext();
      final AuthorNameTransformer transformer = webApplicationContext.getBean(AuthorNameTransformer.class);
      result = transformer.transform(author);
    } catch (Exception e) {
      // Ignore - simply return the unprocessed input string.
    }
    pageContext.getOut().write(result);
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Sets the author.
   *
   * @param author Author
   */
  public void setAuthor(final String author) {
    this.author = author;
  }

}

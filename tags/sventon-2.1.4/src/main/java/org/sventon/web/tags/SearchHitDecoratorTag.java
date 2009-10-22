/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.tags;

import org.apache.commons.lang.StringUtils;
import org.sventon.web.ctrl.template.SearchEntriesController;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * JSP Tag for decorating search hits by applying a certain CSS.
 *
 * @author jesper@sventon.org
 */
public final class SearchHitDecoratorTag extends TagSupport {

  private static final long serialVersionUID = 1174478203198572953L;

  /**
   * CSS class to use.
   */
  private String cssName;

  /**
   * Search type, 'TEXT' or 'CAMELCASE'.
   */
  private String searchType;

  /**
   * The string to search for.
   */
  private String searchString;

  /**
   * The string to search in.
   */
  private String text;

  /**
   * {@inheritDoc}
   */
  @Override
  public int doStartTag() throws JspException {
    try {
      pageContext.getOut().write(decorate(cssName, searchType, searchString, text));
    } catch (final IOException e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Decorates a string.
   *
   * @param cssName      CSS name
   * @param searchType   Type
   * @param searchString The string to search for
   * @param text         The text to search in
   * @return Decorated text
   */
  protected static String decorate(final String cssName, final String searchType,
                                   final String searchString, final String text) {

    if (StringUtils.isEmpty(cssName)) {
      throw new IllegalArgumentException("No CSS name specified!");
    }

    final StringBuilder work = new StringBuilder(StringUtils.trimToEmpty(text));
    if (StringUtils.trimToEmpty(searchType).equals(SearchEntriesController.SearchType.TEXT.name())) {
      final int offset = lastIndexOfIgnoreCase(work.toString(), searchString);
      if (offset >= 0) {
        work.insert(offset + searchString.length(), "</span>");
        work.insert(offset, "<span class=\"" + cssName + "\">");
      }
    }
    return work.toString();
  }

  protected static int lastIndexOfIgnoreCase(final String work, final String searchString) {
    return work.toLowerCase().lastIndexOf(searchString.toLowerCase());
  }

  /**
   * @param cssName CSS Name
   */
  public void setCssName(final String cssName) {
    this.cssName = cssName;
  }

  /**
   * @param searchType Search type
   */
  public void setSearchType(final String searchType) {
    this.searchType = searchType;
  }

  /**
   * @param searchString The string searched for
   */
  public void setSearchString(final String searchString) {
    this.searchString = searchString;
  }

  /**
   * @param text The text to search in
   */
  public void setText(final String text) {
    this.text = text;
  }

}
/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.tags;

import org.apache.commons.lang.time.DurationFormatUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSP Tag for formatting a date into an age string.
 *
 * @author jesper@sventon.org
 */
public final class AgeTag extends TagSupport {

  private static final long serialVersionUID = 5299403672028193493L;

  private static final Pattern PATTERN = Pattern.compile("(\\S+\\s\\S+).*");

  /**
   * Locale.
   */
  private Date date;

  @Override
  public int doStartTag() throws JspException {
    try {
      pageContext.getOut().write(getAsAgeString(date, Calendar.getInstance().getTime()));
    } catch (final IOException e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Gets an age string based on given date.
   *
   * @param start Start date.
   * @param stop  End data.
   * @return Age string.
   */
  protected static String getAsAgeString(final Date start, final Date stop) {
    if (start == null || stop == null) {
      return "";
    }
    final String s = DurationFormatUtils.formatDurationWords(stop.getTime() - start.getTime(), true, true);
    final Matcher m = PATTERN.matcher(s);
    m.matches();
    return m.group(1);
  }

  /**
   * Sets the date.
   * Needed by the framework.
   *
   * @param date Date.
   */
  public void setDate(final Date date) {
    this.date = date != null ? (Date) date.clone() : null;
  }

}

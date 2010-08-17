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
package org.sventon.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Misc helper for date operations such as uniform formatting etc
 */
public class DateUtil {
  public static final String ISO8601_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

  // TODO: is this really ISO8601 or can we be more precise, like saying something like svn date format or...
  // TODO: What to do when parsing fails? Try another format? 

  public static Date parseISO8601(final String date) {
    try {
      return new SimpleDateFormat(ISO8601_FORMAT_PATTERN).parse(date);
    } catch (ParseException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  public static String formatISO8601(final Date date) {
    return new SimpleDateFormat(ISO8601_FORMAT_PATTERN).format(date);
  }

}

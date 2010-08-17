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
 * 
 */
public class DateUtil {
  private static final String ISO8601_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
  private static final String SHORT_FORMAT_PATTERN =  "yyyy-MM-dd' 'HH:mm:ss'Z'";

  // TODO: is this really ISO8601 or can we be more precise, like saying something like svn date format or...
  public static Date parseISO8601(final String date) {
    return parse(date, ISO8601_FORMAT_PATTERN);
  }

   
  public static Date parseShort(final String date) {
    return parse(date, SHORT_FORMAT_PATTERN);
  }

  private static Date parse(String date, final String formatPattern) {
    try {
      return new SimpleDateFormat(formatPattern).parse(date);
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }


  public static String formatISO8601(final Date date) {
    return new SimpleDateFormat(ISO8601_FORMAT_PATTERN).format(date);
  }

  public static String formatShort(final Date date) {
    return new SimpleDateFormat(SHORT_FORMAT_PATTERN).format(date);
  }


}

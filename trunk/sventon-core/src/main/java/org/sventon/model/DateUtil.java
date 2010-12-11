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
package org.sventon.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Misc helper for date operations such as uniform formatting etc
 */
public class DateUtil {

  private static final String ISO8601_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

  /**
   * Interface used by pattern matcher to calculate the time and date part
   */
  interface DateCalculator {
    Calendar calculate(final Matcher matcher);
  }

  /**
   * Interface used by pattern matcher to adjust Time Zone to a given date (as Calendar)
   */
  interface ZoneClosure {
    void execute(final Matcher matcher, Calendar calendar);
  }

  static final ZoneClosure basicZoneCalc = new ZoneClosure() {
    @Override
    public void execute(Matcher matcher, Calendar calendar) {

      int zoneOffsetInMillis = "+".equals(matcher.group(10)) ? +1 : -1;
      int hoursOffset = Integer.parseInt(matcher.group(11));
      int minutesOffset = matcher.group(12) != null ? Integer.parseInt(matcher.group(13)) : 0;
      zoneOffsetInMillis = zoneOffsetInMillis * ((hoursOffset * 3600 + minutesOffset * 60) * 1000);

      calendar.set(Calendar.ZONE_OFFSET, zoneOffsetInMillis);
    }
  };

  static final ZoneClosure logZoneCalc = new ZoneClosure() {
    @Override
    public void execute(Matcher matcher, Calendar calendar) {

      // Check if no TZD is given
      if (matcher.group(10) == null) {
        return;
      }

      int zoneOffsetInMillis = "+".equals(matcher.group(11)) ? +1 : -1;
      int hoursOffset = Integer.parseInt(matcher.group(12));
      int minutesOffset = matcher.group(13) != null ? Integer.parseInt(matcher.group(13)) : 0;
      zoneOffsetInMillis = zoneOffsetInMillis * ((hoursOffset * 3600 + minutesOffset * 60) * 1000);

      calendar.set(Calendar.ZONE_OFFSET, zoneOffsetInMillis);
    }
  };

  static final ZoneClosure utcZoneCalc = new ZoneClosure() {
    @Override
    public void execute(Matcher matcher, Calendar calendar) {

      if ("Z".equals(matcher.group(10))) {
        calendar.set(Calendar.ZONE_OFFSET, 0);
        calendar.set(Calendar.DST_OFFSET, 0);
      }
    }
  };

  static final ZoneClosure nullZoneCalc = new ZoneClosure() {
    @Override
    public void execute(Matcher matcher, Calendar calendar) {
      // Let it be.
    }
  };

  static final DateCalculator dateOnlyCalc = new DateCalculator() {
    @Override
    public Calendar calculate(Matcher matcher) {
      Calendar calendar = Calendar.getInstance();
      calendar.clear();

      int year = Integer.parseInt(matcher.group(1));
      int month = Integer.parseInt(matcher.group(2));
      int day = Integer.parseInt(matcher.group(3));

      calendar.set(year, month - 1, day);

      return calendar;
    }
  };

  static final DateCalculator dateTimeCalc = new DateCalculator() {
    @Override
    public Calendar calculate(Matcher matcher) {
      Calendar calendar = Calendar.getInstance();
      calendar.clear();

      int year = Integer.parseInt(matcher.group(1));
      int month = Integer.parseInt(matcher.group(2));
      int day = Integer.parseInt(matcher.group(3));
      int hours = Integer.parseInt(matcher.group(4));
      int minutes = Integer.parseInt(matcher.group(5));
      int seconds = 0;
      int milliseconds = 0;
      if (matcher.group(6) != null) {
        seconds = Integer.parseInt(matcher.group(7));
        if (matcher.group(8) != null) {
          String millis = matcher.group(9);
          millis = millis.length() <= 3 ? millis : millis.substring(0, 3);
          milliseconds = Integer.parseInt(millis);
        }
      }

      calendar.set(year, month - 1, day);
      calendar.set(Calendar.HOUR, hours);
      calendar.set(Calendar.MINUTE, minutes);
      calendar.set(Calendar.SECOND, seconds);
      calendar.set(Calendar.MILLISECOND, milliseconds);

      return calendar;
    }
  };

  static final DateCalculator timeOnlyDateCalc = new DateCalculator() {
    @Override
    public Calendar calculate(Matcher matcher) {

      Calendar calendar = Calendar.getInstance();
      int hours = Integer.parseInt(matcher.group(1));
      int minutes = Integer.parseInt(matcher.group(2));
      int seconds = 0;
      int milliseconds = 0;
      if (matcher.group(3) != null) {
        seconds = Integer.parseInt(matcher.group(4));
        if (matcher.group(5) != null) {
          String millis = matcher.group(6);
          millis = millis.length() <= 3 ? millis : millis.substring(0, 3);
          milliseconds = Integer.parseInt(millis);
        }
      }

      calendar.set(Calendar.HOUR_OF_DAY, hours);
      calendar.set(Calendar.MINUTE, minutes);
      calendar.set(Calendar.SECOND, seconds);
      calendar.set(Calendar.MILLISECOND, milliseconds);

      return calendar;
    }
  };


  /**
   * DatePattern holds the pattern definition used to check for a match and if found, call the given DateCalculator and
   * ZoneClosure to parse the date (as Calendar) for a revision string
   */
  private enum DatePattern {
    ISO8601_DATE_ONLY_DASH_PATTERN("(\\d{4})-(\\d{1,2})-(\\d{1,2})", DateUtil.dateOnlyCalc, DateUtil.nullZoneCalc),
    ISO8601_DATE_TIME_DASH_UTC_PATTERN("(\\d{4})-(\\d{1,2})-(\\d{1,2})T(\\d{1,2}):(\\d{2})(:(\\d{2})([.,](\\d{1,6}))?)?(Z)?", DateUtil.dateTimeCalc, DateUtil.utcZoneCalc),
    ISO8601_DATE_TIME_DASH_OFFSET_PATTERN("(\\d{4})-(\\d{1,2})-(\\d{1,2})T(\\d{1,2}):(\\d{2})(:(\\d{2})([.,](\\d{1,6}))?)?([+-])(\\d{2})(:(\\d{2}))?", DateUtil.dateTimeCalc, DateUtil.basicZoneCalc),
    ISO8601_DATE_ONLY_COMPACT_PATTERN("(\\d{4})(\\d{2})(\\d{2})", DateUtil.dateOnlyCalc, DateUtil.nullZoneCalc),
    ISO8601_DATE_TIME_COMPACT_UTC_PATTERN("(\\d{4})(\\d{2})(\\d{2})T(\\d{2})(\\d{2})((\\d{2})([.,](\\d{1,6}))?)?(Z)?", DateUtil.dateTimeCalc, DateUtil.utcZoneCalc),
    ISO8601_DATE_TIME_COMPACT_OFFSET_PATTERN("(\\d{4})(\\d{2})(\\d{2})T(\\d{2})(\\d{2})((\\d{2})([.,](\\d{1,6}))?)?([+-])(\\d{2})((\\d{2}))?", DateUtil.dateTimeCalc, DateUtil.basicZoneCalc),
    ISO8601_GNU_FORMAT_PATTERN("(\\d{4})-(\\d{1,2})-(\\d{1,2})T(\\d{1,2}):(\\d{2})(:(\\d{2})([.,](\\d{1,6}))?)?([+-])(\\d{2})((\\d{2}))?", DateUtil.dateTimeCalc, DateUtil.basicZoneCalc),
    SVN_LOG_DATE_FORMAT_PATTERN("(\\d{4})-(\\d{1,2})-(\\d{1,2}) (\\d{1,2}):(\\d{2})(:(\\d{2})([.,](\\d{1,6}))?)?( ([+-])(\\d{2})(\\d{2})?)?", DateUtil.dateTimeCalc, DateUtil.logZoneCalc),
    TIME_ONLY_PATTERN("(\\d{1,2}):(\\d{2})(:(\\d{2})([.,](\\d{1,6}))?)?", DateUtil.timeOnlyDateCalc, DateUtil.nullZoneCalc);

    /* 2006-02-17T15:30:00.314Z */

    private Pattern pattern;
    private final DateCalculator dateCalculator;
    private final ZoneClosure zoneClosure;

    private DatePattern(final String datePattern, final DateUtil.DateCalculator dateCalc, final DateUtil.ZoneClosure zoneCalc) {
      dateCalculator = dateCalc;
      zoneClosure = zoneCalc;
      this.pattern = Pattern.compile(datePattern);
    }

    public Calendar parse(final String date) {
      final Matcher matcher = pattern.matcher(date);
      if (!matcher.matches()) {
        return null;
      }

      final Calendar calendar = dateCalculator.calculate(matcher);
      zoneClosure.execute(matcher, calendar);
      return calendar;
    }

  }


  public static Date parseISO8601(final String date) {
    for (DatePattern pattern : DatePattern.values()) {
      final Calendar calendar = pattern.parse(date);
      if (calendar != null) {
        return calendar.getTime();
      }
    }
    throw new IllegalArgumentException("Could not parse date " + date);
  }

  public static String formatISO8601(final Date date) {
    if (date == null) return "";
    SimpleDateFormat dateFormat = new SimpleDateFormat(ISO8601_FORMAT_PATTERN);
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return dateFormat.format(date);
  }

}

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
package org.sventon.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static java.util.concurrent.TimeUnit.*;

/**
 *
 */
public class DateUtilTest {

  /*
  Test ISO8601 + some SVN unique formats. svnbook gives the following examples:
$ svn checkout -r {2006-02-17}
$ svn checkout -r {15:30}
$ svn checkout -r {15:30:00.200000}

$ svn checkout -r {"2006-02-17 15:30"}
$ svn checkout -r {"2006-02-17 15:30 +0230"}

$ svn checkout -r {2006-02-17T15:30}
$ svn checkout -r {2006-02-17T15:30Z}
$ svn checkout -r {2006-02-17T15:30-04:00}

$ svn checkout -r {20060217T1530}
$ svn checkout -r {20060217T1530Z}
$ svn checkout -r {20060217T1530-0500}
   */

  @Test
  public void parseISO8601NoDate() throws Exception {
    final Calendar today = Calendar.getInstance();
    today.set(Calendar.HOUR_OF_DAY, 15);
    today.set(Calendar.MINUTE, 30);
    today.set(Calendar.SECOND, 0);
    today.set(Calendar.MILLISECOND, 0);

    Assert.assertEquals(today.getTime(), DateUtil.parseISO8601("15:30"));

    // Note: We only use the last three digits (SSS) in the millisecond field, the rest is truncated.
    today.set(Calendar.MILLISECOND, 200);
    Assert.assertEquals(today.getTime(), DateUtil.parseISO8601("15:30:00.200000"));
  }

  @Test
  public void parseISO8601Simple() throws Exception {
    final Calendar cal = Calendar.getInstance();
    cal.set(2006, 2 - 1, 17, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17"));

    cal.set(Calendar.HOUR_OF_DAY, 15);
    cal.set(Calendar.MINUTE, 30);
    cal.set(Calendar.SECOND, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17 15:30"));


    cal.set(Calendar.ZONE_OFFSET, (int) (MILLISECONDS.convert(2, HOURS) + MILLISECONDS.convert(30, MINUTES)));
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17 15:30 +0230"));
  }

  @Test
  public void parseISO8601T_UTCFormat() throws Exception {
    final Calendar cal = Calendar.getInstance();
    cal.set(2006, 2 - 1, 17, 15, 30, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17T15:30"));

    cal.set(Calendar.ZONE_OFFSET, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17T15:30Z"));

    cal.set(Calendar.MILLISECOND, 314);
    cal.set(Calendar.ZONE_OFFSET, 0); // Note we must reset the Z again. Calendar set it back to default TZD after any operation ... !
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17T15:30:00.314Z"));
    //2010-01-01T12:34:56.789Z
  }

  @Test
  public void parseISO8601T_GMTFormat() throws Exception {
    final Calendar cal = Calendar.getInstance();
    cal.set(2006, 2 - 1, 17, 15, 30, 0);
    cal.set(Calendar.MILLISECOND, 0);

    cal.set(Calendar.ZONE_OFFSET, -(int) (MILLISECONDS.convert(4, HOURS)));
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17T15:30-04:00"));

    cal.set(Calendar.MILLISECOND, 314);
    cal.set(Calendar.ZONE_OFFSET, -(int) (MILLISECONDS.convert(4, HOURS)));
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17T15:30:00.314-04:00"));

  }

  @Test
  public void parseISO8601CompapctTFormat() throws Exception {
    final Calendar cal = Calendar.getInstance();
    cal.set(2006, 2 - 1, 17, 15, 30, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("20060217T1530"));

    cal.set(Calendar.ZONE_OFFSET, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("20060217T1530Z"));

    cal.set(Calendar.ZONE_OFFSET, -(int) (MILLISECONDS.convert(4, HOURS)));
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("20060217T1530-0400"));
  }


  @Test
  public void formatISO8601() throws Exception {
    //yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'
    final Calendar calStockholm = Calendar.getInstance(TimeZone.getTimeZone("Europe/Stockholm"));
    calStockholm.set(2010, 0, 1, 12, 34, 56);
    calStockholm.set(Calendar.MILLISECOND, 0);

    Assert.assertEquals("2010-01-01T11:34:56.000000Z", DateUtil.formatISO8601(calStockholm.getTime()));

    Calendar calUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calUtc.set(2010, 0, 1, 12, 34, 56);
    calUtc.set(Calendar.MILLISECOND, 0);
    Assert.assertEquals("2010-01-01T12:34:56.000000Z", DateUtil.formatISO8601(calUtc.getTime()));
  }


}

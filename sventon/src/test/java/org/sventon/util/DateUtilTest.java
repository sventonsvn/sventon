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

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

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
    cal.set(2006, 02 - 1, 17, 00, 00 ,00);
    cal.set(Calendar.MILLISECOND, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17"));

    cal.set(Calendar.HOUR_OF_DAY, 15);
    cal.set(Calendar.MINUTE, 30);
    cal.set(Calendar.SECOND, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17 15:30"));


    cal.set(Calendar.ZONE_OFFSET, (int)(MILLISECONDS.convert(2, HOURS) + MILLISECONDS.convert(30, MINUTES)));
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17 15:30 +0230"));
  }

  @Test
  public void parseISO8601TFormat() throws Exception {
    final Calendar cal = Calendar.getInstance();
    cal.set(2006, 02 - 1, 17, 15, 30 ,00);
    cal.set(Calendar.MILLISECOND, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17T15:30"));

    cal.set(Calendar.ZONE_OFFSET, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17T15:30Z"));

    cal.set(Calendar.ZONE_OFFSET, -(int)(MILLISECONDS.convert(4, HOURS)));
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2006-02-17T15:30-04:00"));
  }

  @Test
  public void parseISO8601CompapctTFormat() throws Exception {
    final Calendar cal = Calendar.getInstance();
    cal.set(2006, 02 - 1, 17, 15, 30 ,00);
    cal.set(Calendar.MILLISECOND, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("20060217T1530"));

    cal.set(Calendar.ZONE_OFFSET, 0);
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("20060217T1530Z"));

    cal.set(Calendar.ZONE_OFFSET, -(int)(MILLISECONDS.convert(4, HOURS)));
    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("20060217T1530-0400"));
  }



  @Test
  public void formatISO8601() throws Exception {
    //yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'
    final Calendar cal = Calendar.getInstance();
    cal.set(2010, 00, 01, 12, 34, 56);
    cal.set(Calendar.MILLISECOND, 0);

    Assert.assertEquals("2010-01-01T12:34:56.000000Z", DateUtil.formatISO8601(cal.getTime()));
  }


}

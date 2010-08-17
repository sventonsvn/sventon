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

import static org.junit.Assert.fail;

/**
 *
 */
public class DateUtilTest {
  @Test
  public void parseISO8601() throws Exception {
    //yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'
    final Calendar cal = Calendar.getInstance();
    cal.set(2010, 00, 01, 12, 34, 56);
    cal.set(Calendar.MILLISECOND, 0);

    Assert.assertEquals(cal.getTime(), DateUtil.parseISO8601("2010-01-01T12:34:56.000000Z"));
  }
  @Test
  public void formatISO8601() throws Exception {
    //yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'
    final Calendar cal = Calendar.getInstance();
    cal.set(2010, 00, 01, 12, 34, 56);
    cal.set(Calendar.MILLISECOND, 0);

    Assert.assertEquals("2010-01-01T12:34:56.000000Z", DateUtil.formatISO8601(cal.getTime()));
  }

  @Test
  public void parseError() throws Exception {
    try {
      DateUtil.parseISO8601("2010-01-01 12:34:56");
      fail("Parse error should throw IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

}

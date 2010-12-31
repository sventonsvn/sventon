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

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class AgeTagTest {

  @Test
  public void testGetAsAgeString() throws Exception {
    final Calendar start = new GregorianCalendar(2007, 0, 1, 0, 0, 0);
    final Calendar stop = (Calendar) start.clone();

    stop.add(Calendar.SECOND, 25);
    assertEquals("25 seconds", AgeTag.getAsAgeString(start.getTime(), stop.getTime()));
    stop.add(Calendar.MINUTE, 31);
    assertEquals("31 minutes", AgeTag.getAsAgeString(start.getTime(), stop.getTime()));
    stop.add(Calendar.HOUR, 25);
    assertEquals("1 day", AgeTag.getAsAgeString(start.getTime(), stop.getTime()));
    stop.add(Calendar.MONTH, 8);
    assertEquals("244 days", AgeTag.getAsAgeString(start.getTime(), stop.getTime()));
    stop.add(Calendar.WEEK_OF_YEAR, 6);
    assertEquals("286 days", AgeTag.getAsAgeString(start.getTime(), stop.getTime()));
    stop.add(Calendar.YEAR, 1);
    assertEquals("652 days", AgeTag.getAsAgeString(start.getTime(), stop.getTime()));
    stop.add(Calendar.SECOND, 10);
    assertEquals("652 days", AgeTag.getAsAgeString(start.getTime(), stop.getTime()));

    assertEquals("", AgeTag.getAsAgeString(null, null));
  }
}
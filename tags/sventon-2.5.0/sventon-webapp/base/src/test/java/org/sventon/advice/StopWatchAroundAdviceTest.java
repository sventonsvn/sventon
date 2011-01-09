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
package org.sventon.advice;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StopWatchAroundAdviceTest {

  @Test
  public void testCreateMessage() throws Exception {
    final StopWatchAroundAdvice advice = new StopWatchAroundAdvice();
    assertEquals("Method [null] took [0] ms", advice.createMessage(null, 0));
    assertEquals("Method [test] took [0] ms", advice.createMessage("test", 0));
    assertEquals("Method [test] took [99] ms", advice.createMessage("test", 99));
    assertEquals("Method [test] took [1000] ms", advice.createMessage("test", 1000));
  }
}

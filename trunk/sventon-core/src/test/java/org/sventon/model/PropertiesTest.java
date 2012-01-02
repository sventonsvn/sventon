/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertiesTest {

  @Test
  public void testToString() throws Exception {
    final Properties props = new Properties();
    assertEquals("{}", props.toString());
    props.put(new Property("key"), new PropertyValue("value"));
    assertEquals("{key=value}", props.toString());
  }

}

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

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PropertyTest {
  @Test
  /*
  Test that a Property can be a custom string and not just predefined 'svn:' , 'svn:entry:' etc properties.
   */
  public void customProperty() throws Exception {
    Property p = new Property("jesper:test Yo!");

    assertEquals("jesper:test Yo!", p.getName());
  }

  @Test
  /*
  Test some predefined Properties that we use in the application.
   */
  public void predefinedProperty() throws Exception {
    assertEquals("svn:mime-type", Property.MIME_TYPE.getName());
    assertEquals("svn:entry:checksum", Property.CHECKSUM.getName());
    assertEquals("svn:entry:last-author", Property.LAST_AUTHOR.getName());
    assertEquals("svn:entry:committed-date", Property.COMMITTED_DATE.getName());
    assertEquals("svn:entry:committed-rev", Property.COMMITTED_REVISION.getName());
  }

  @Test
  /*
  Test to see if given string is a mime-type description. (Why here?)
   */
  public void isTextMimeType() throws Exception {
    assertTrue(Property.isTextMimeType("text/foo-bar"));
    assertTrue(Property.isTextMimeType(null));
    assertFalse(Property.isTextMimeType("audio/crazy-frog"));

  }
}

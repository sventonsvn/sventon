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
package org.sventon.web.command.editor;

import org.junit.Test;
import org.sventon.model.PathRevision;
import org.sventon.model.Revision;

import static org.junit.Assert.assertEquals;

public class PathRevisionEditorTest {

  @Test
  public void testGetSetAsText() {
    final PathRevisionEditor editor = new PathRevisionEditor();
    assertEquals("", editor.getAsText());

    editor.setAsText("/trunk/test.java@3");
    PathRevision fileRevision = new PathRevision("/trunk/test.java", Revision.create(3));
    assertEquals(fileRevision.getPath(), ((PathRevision) editor.getValue()).getPath());
    assertEquals(fileRevision.getRevision(), ((PathRevision) editor.getValue()).getRevision());
  }

  @Test
  public void testParseEntriesWithDelimitersInPath() throws Exception {
    final String[] parameters = new String[]{
        "/trunk/test@file.java@123",
    };
    final PathRevision[] entries = PathRevision.parse(parameters);
    assertEquals(1, entries.length);

    final PathRevision entry0 = entries[0];
    assertEquals("/trunk/test@file.java", entry0.getPath());
    assertEquals(123, entry0.getRevision().getNumber());
  }

  @Test
  public void testParseEntries() throws Exception {
    final String[] parameters = new String[]{
        "/trunk/test.java@3",
        "/trunk/test.java@2",
        "/trunk/test.java@1"
    };
    final PathRevision[] entries = PathRevision.parse(parameters);
    assertEquals(3, entries.length);

    final PathRevision entry2 = entries[0];
    assertEquals("/trunk/test.java", entry2.getPath());
    assertEquals(3, entry2.getRevision().getNumber());

    final PathRevision entry0 = entries[1];
    assertEquals("/trunk/test.java", entry0.getPath());
    assertEquals(2, entry0.getRevision().getNumber());

    final PathRevision entry1 = entries[2];
    assertEquals("/trunk/test.java", entry1.getPath());
    assertEquals(1, entry1.getRevision().getNumber());
  }

}

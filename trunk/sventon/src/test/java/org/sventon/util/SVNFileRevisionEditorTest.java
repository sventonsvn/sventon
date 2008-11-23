package org.sventon.util;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.io.SVNFileRevision;

public class SVNFileRevisionEditorTest extends TestCase {

  public void testGetSetAsText() {
    final SVNFileRevisionEditor editor = new SVNFileRevisionEditor();
    assertEquals("", editor.getAsText());

    editor.setAsText("/trunk/test.java;;3");
    SVNFileRevision fileRevision = new SVNFileRevision("/trunk/test.java", 3, null, null);
    assertEquals(fileRevision.getPath(), ((SVNFileRevision) editor.getValue()).getPath());
    assertEquals(fileRevision.getRevision(), ((SVNFileRevision) editor.getValue()).getRevision());
  }


  public void testParseEntriesWithDelimitersInPath() throws Exception {
    final SVNFileRevisionEditor editor = new SVNFileRevisionEditor();
    final String[] parameters = new String[]{
        "/trunk/test;;file.java;;123",
    };
    final SVNFileRevision[] entries = editor.convert(parameters);
    assertEquals(1, entries.length);

    final SVNFileRevision entry0 = entries[0];
    assertEquals("/trunk/test;;file.java", entry0.getPath());
    assertEquals(123, entry0.getRevision());
  }

  public void testParseEntries() throws Exception {
    final SVNFileRevisionEditor editor = new SVNFileRevisionEditor();
    final String[] parameters = new String[]{
        "/trunk/test.java;;3",
        "/trunk/test.java;;2",
        "/trunk/test.java;;1"
    };
    final SVNFileRevision[] entries = editor.convert(parameters);
    assertEquals(3, entries.length);

    final SVNFileRevision entry2 = entries[0];
    assertEquals("/trunk/test.java", entry2.getPath());
    assertEquals(3, entry2.getRevision());

    final SVNFileRevision entry0 = entries[1];
    assertEquals("/trunk/test.java", entry0.getPath());
    assertEquals(2, entry0.getRevision());

    final SVNFileRevision entry1 = entries[2];
    assertEquals("/trunk/test.java", entry1.getPath());
    assertEquals(1, entry1.getRevision());
  }

}

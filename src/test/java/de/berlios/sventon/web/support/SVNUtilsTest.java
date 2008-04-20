package de.berlios.sventon.web.support;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SVNUtilsTest extends TestCase {

  public void testIsAccessible() throws Exception {
    assertFalse(SVNUtils.isAccessible(new SVNLogEntry(null, 12, null, null, null)));
    assertFalse(SVNUtils.isAccessible(new SVNLogEntry(null, 12, null, null, "message")));
    assertFalse(SVNUtils.isAccessible(new SVNLogEntry(null, 12, null, new Date(), null)));

    final Map<String, SVNLogEntryPath> map = new HashMap<String, SVNLogEntryPath>();
    map.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    assertTrue(SVNUtils.isAccessible(new SVNLogEntry(map, 12, null, new Date(), null)));
  }
}
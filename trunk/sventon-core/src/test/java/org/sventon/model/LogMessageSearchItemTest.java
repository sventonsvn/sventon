package org.sventon.model;

import org.junit.Test;
import org.sventon.TestUtils;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class LogMessageSearchItemTest {

  @Test
  public void testToStringList() throws Exception {
    final SortedSet<ChangedPath> paths = new TreeSet<ChangedPath>();
    paths.add(new ChangedPath("/trunk", null, -1, ChangeType.ADDED));
    paths.add(new ChangedPath("/tags", null, -1, ChangeType.ADDED));
    assertEquals("[/tags, /trunk]", LogMessageSearchItem.toStringList(paths).toString());
  }

  @Test
  public void testGetPaths() throws Exception {
    final LogMessageSearchItem log = new LogMessageSearchItem(TestUtils.getLogEntryStub());
    assertEquals("[/trunk/file1.java]", log.getPaths().toString());
  }
}

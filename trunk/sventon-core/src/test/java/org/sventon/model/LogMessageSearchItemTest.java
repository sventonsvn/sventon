package org.sventon.model;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;

public class LogMessageSearchItemTest {

  @Test
  public void testExtractAndConcatinatePaths() throws Exception {
    final SortedSet<ChangedPath> paths = new TreeSet<ChangedPath>();
    paths.add(new ChangedPath("/trunk", null, -1, ChangeType.ADDED));
    paths.add(new ChangedPath("/tags", null, -1, ChangeType.ADDED));
    assertEquals(LogMessageSearchItem.PATHS_DELIMITER + "/tags" + LogMessageSearchItem.PATHS_DELIMITER + "/trunk",
        LogMessageSearchItem.extractAndConcatinatePaths(paths));
  }
}

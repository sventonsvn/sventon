package org.sventon.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RevisionRangeSplitterTest extends TestCase {

  public void testSplit() {
    final List<Long> input = new ArrayList<Long>();
    input.add(1L);
    input.add(3L);
    input.add(8L);
    input.add(11L);
    input.add(145L);

    RevisionRangeSplitter splitter = new RevisionRangeSplitter(Collections.EMPTY_LIST, 3);
    assertEquals(0, splitter.size());

    splitter = new RevisionRangeSplitter(input, 3);

    assertEquals(2, splitter.size());
    final RevisionRangeIterator rangeIterator = splitter.iterator();
    assertEquals("1-8", rangeIterator.next().toString());
    assertEquals("11-145", rangeIterator.next().toString());
  }
}

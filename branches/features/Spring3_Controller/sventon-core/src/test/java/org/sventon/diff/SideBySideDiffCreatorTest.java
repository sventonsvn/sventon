package org.sventon.diff;

import org.junit.Test;
import org.sventon.model.SideBySideDiffRow;
import org.sventon.model.TextFile;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class SideBySideDiffCreatorTest {

  @Test
  public void testCreateFromDiffResult() throws Exception {
    final String diffResult = "1d1\r\n<\r\n";
    final TextFile fromFile = new TextFile("\r\ntest\r\nfile\r\n");
    final TextFile toFile = new TextFile("test\r\nfile\r\n");

    final SideBySideDiffCreator diffCreator = new SideBySideDiffCreator(fromFile, toFile);

    final List<SideBySideDiffRow> result = diffCreator.createFromDiffResult(diffResult);
    assertEquals(3, result.size());
    final Iterator<SideBySideDiffRow> iterator = result.iterator();
    assertFalse(iterator.next().getIsUnchanged());
    assertTrue(iterator.next().getIsUnchanged());
    assertTrue(iterator.next().getIsUnchanged());

    for (SideBySideDiffRow row : result) {
      assertEquals(row.getLeft().getLine(), row.getRight().getLine());
    }
  }
}

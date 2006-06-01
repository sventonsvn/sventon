package de.berlios.sventon.diff;

import junit.framework.TestCase;

public class DiffActionTest extends TestCase {

  public void testDiffActionTestValueOfDeleted() throws Exception {
    assertEquals(DiffAction.d, DiffAction.valueOf("d"));
    assertEquals("Deleted", DiffAction.valueOf("d").getDescription());
    assertEquals("-", DiffAction.valueOf("d").getSymbol());
  }

  public void testDiffActionTestValueOfAdded() throws Exception {
    assertEquals(DiffAction.a, DiffAction.valueOf("a"));
    assertEquals("Added", DiffAction.valueOf("a").getDescription());
    assertEquals("+", DiffAction.valueOf("a").getSymbol());
  }

  public void testDiffActionTestValueOfChanged() throws Exception {
    assertEquals(DiffAction.c, DiffAction.valueOf("c"));
    assertEquals("Changed", DiffAction.valueOf("c").getDescription());
    assertEquals("&#8800;", DiffAction.valueOf("c").getSymbol());
  }

}
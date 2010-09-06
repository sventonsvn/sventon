package org.sventon.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiffActionTest {

  @Test
  public void testDiffActionTestValueOfDeleted() throws Exception {
    assertEquals(DiffAction.DELETED, DiffAction.parse("d"));
    assertEquals("Deleted", DiffAction.parse("d").getDescription());
    assertEquals("-", DiffAction.parse("d").getSymbol());
  }

  @Test
  public void testDiffActionTestValueOfAdded() throws Exception {
    assertEquals(DiffAction.ADDED, DiffAction.parse("a"));
    assertEquals("Added", DiffAction.parse("a").getDescription());
    assertEquals("+", DiffAction.parse("a").getSymbol());
  }

  @Test
  public void testDiffActionTestValueOfChanged() throws Exception {
    assertEquals(DiffAction.CHANGED, DiffAction.parse("c"));
    assertEquals("Changed", DiffAction.parse("c").getDescription());
    assertEquals("&#8800;", DiffAction.parse("c").getSymbol());
  }

}
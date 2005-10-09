package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

import java.util.Iterator;

public class DiffResultParserTest extends TestCase {

  public void testDiffHelperDelete() throws Exception {
    String result = "2,3d2\r\n<OneMore=2";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffAction action = actions.next();
    assertEquals(DiffAction.DELETE_ACTION, action.getAction());
    assertEquals(2, action.getLeftLineIntervalStart());
    assertEquals(3, action.getLeftLineIntervalEnd());
    assertEquals(2, action.getRightLineIntervalStart());
    assertEquals(2, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: d, left: 2-3, right: 2-2", action.toString());
  }

  public void testDiffHelperDeleteII() throws Exception {
    String result = "10d10\r\n<OneMore=2";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffAction action = actions.next();
    assertEquals(DiffAction.DELETE_ACTION, action.getAction());
    assertEquals(10, action.getLeftLineIntervalStart());
    assertEquals(10, action.getLeftLineIntervalEnd());
    assertEquals(10, action.getRightLineIntervalStart());
    assertEquals(10, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: d, left: 10-10, right: 10-10", action.toString());
  }

  public void testDiffHelperAdd() throws Exception {
    String result = "9a10,11\r\n>OneMore=1\r\n>OneMore=2\r\n>OneMore=3";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffAction action = actions.next();
    assertEquals(DiffAction.ADD_ACTION, action.getAction());
    assertEquals(9, action.getLeftLineIntervalStart());
    assertEquals(9, action.getLeftLineIntervalEnd());
    assertEquals(10, action.getRightLineIntervalStart());
    assertEquals(11, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: a, left: 9-9, right: 10-11", action.toString());
  }

  public void testDiffHelperChange() throws Exception {
    String result = "2c2\r\n<IconIndex=-2388\r\n---\r\n>IconIndex=-238";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffAction action = actions.next();
    assertEquals(DiffAction.CHANGE_ACTION, action.getAction());
    assertEquals(2, action.getLeftLineIntervalStart());
    assertEquals(2, action.getLeftLineIntervalEnd());
    assertEquals(2, action.getRightLineIntervalStart());
    assertEquals(2, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: c, left: 2-2, right: 2-2", action.toString());
  }

  public void testDiffHelperChangeII() throws Exception {
    String result =
        "10,12c3,4\n" +
        "< * $HeadURL$\n" +
        "< * $URL$\n" +
        "< * $Id$";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffAction action = actions.next();
    assertEquals(DiffAction.CHANGE_ACTION, action.getAction());
    assertEquals(10, action.getLeftLineIntervalStart());
    assertEquals(12, action.getLeftLineIntervalEnd());
    assertEquals(3, action.getRightLineIntervalStart());
    assertEquals(4, action.getRightLineIntervalEnd());

    assertEquals("DiffAction: c, left: 10-12, right: 3-4", action.toString());
  }

  public void testDiffHelperAddAndChange() throws Exception {
    String result = "2c2\r\n<IconIndex=-2388\r\n---\r\n>IconIndex=-238\r\n8a8,9\r\n>OneMore=true\r\n>";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffAction action = actions.next();
    assertEquals(DiffAction.ADD_ACTION, action.getAction());
    assertEquals(8, action.getLeftLineIntervalStart());
    assertEquals(8, action.getLeftLineIntervalEnd());
    assertEquals(8, action.getRightLineIntervalStart());
    assertEquals(9, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: a, left: 8-8, right: 8-9", action.toString());
    action = actions.next();
    assertEquals(DiffAction.CHANGE_ACTION, action.getAction());
    assertEquals(2, action.getLeftLineIntervalStart());
    assertEquals(2, action.getLeftLineIntervalEnd());
    assertEquals(2, action.getRightLineIntervalStart());
    assertEquals(2, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: c, left: 2-2, right: 2-2", action.toString());
  }

}
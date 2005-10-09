package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

import java.util.Iterator;

public class DiffResultParserTest extends TestCase {

  public void testDiffHelperDelete() throws Exception {
    String result = "2,3d2\r\n<OneMore=2";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result);
    DiffAction action = actions.next();
    assertEquals(DiffAction.DELETE_ACTION, action.getAction());
    assertEquals(2, action.getLineIntervalStart());
    assertEquals(3, action.getLineIntervalEnd());
    assertEquals("DiffAction: d,2-3", action.toString());
  }

  public void testDiffHelperDeleteII() throws Exception {
    String result = "10d10\r\n<OneMore=2";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result);
    DiffAction action = actions.next();
    assertEquals(DiffAction.DELETE_ACTION, action.getAction());
    assertEquals(10, action.getLineIntervalStart());
    assertEquals(10, action.getLineIntervalEnd());
    assertEquals("DiffAction: d,10-10", action.toString());
  }

  public void testDiffHelperAdd() throws Exception {
    String result = "9a10,11\r\n>OneMore=1\r\n>OneMore=2\r\n>OneMore=3";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result);
    DiffAction action = actions.next();
    assertEquals(DiffAction.ADD_ACTION, action.getAction());
    assertEquals(10, action.getLineIntervalStart());
    assertEquals(11, action.getLineIntervalEnd());
    assertEquals("DiffAction: a,10-11", action.toString());
  }

  public void testDiffHelperChange() throws Exception {
    String result = "2c2\r\n<IconIndex=-2388\r\n---\r\n>IconIndex=-238";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result);
    DiffAction action = actions.next();
    assertEquals(DiffAction.CHANGE_ACTION, action.getAction());
    assertEquals(2, action.getLineIntervalStart());
    assertEquals(2, action.getLineIntervalEnd());
    assertEquals("DiffAction: c,2-2", action.toString());
  }

  public void testDiffHelperChangeII() throws Exception {
    String result =
        "10,12c3,4\n" +
        "< * $HeadURL$\n" +
        "< * $URL$\n" +
        "< * $Id$";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result);
    DiffAction action = actions.next();
    assertEquals(DiffAction.CHANGE_ACTION, action.getAction());
    assertEquals(10, action.getLineIntervalStart());
    assertEquals(12, action.getLineIntervalEnd());
    assertEquals("DiffAction: c,10-12", action.toString());
  }

  public void testDiffHelperAddAndChange() throws Exception {
    String result = "2c2\r\n<IconIndex=-2388\r\n---\r\n>IconIndex=-238\r\n8a8,9\r\n>OneMore=true\r\n>";
    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result);
    DiffAction action = actions.next();
    assertEquals(DiffAction.ADD_ACTION, action.getAction());
    assertEquals(8, action.getLineIntervalStart());
    assertEquals(9, action.getLineIntervalEnd());
    assertEquals("DiffAction: a,8-9", action.toString());
    action = actions.next();
    assertEquals(DiffAction.CHANGE_ACTION, action.getAction());
    assertEquals(2, action.getLineIntervalStart());
    assertEquals(2, action.getLineIntervalEnd());
    assertEquals("DiffAction: c,2-2", action.toString());
  }

}
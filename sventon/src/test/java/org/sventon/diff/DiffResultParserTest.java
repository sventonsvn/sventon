package org.sventon.diff;

import junit.framework.TestCase;
import org.sventon.model.DiffAction;

import java.util.Iterator;

import static org.sventon.diff.DiffSegment.Side.LEFT;
import static org.sventon.diff.DiffSegment.Side.RIGHT;

public class DiffResultParserTest extends TestCase {

  public void testDiffHelperDelete() throws Exception {
    String result = "2,3d2\n<OneMore=2";
    Iterator<DiffSegment> diffSegments = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffSegment diffSegment = diffSegments.next();
    assertSame(DiffAction.DELETED, diffSegment.getAction());
    assertEquals(2, diffSegment.getLineIntervalStart(LEFT));
    assertEquals(3, diffSegment.getLineIntervalEnd(LEFT));
    assertEquals(2, diffSegment.getLineIntervalStart(RIGHT));
    assertEquals(2, diffSegment.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: DELETED, left: 2-3, right: 2-2", diffSegment.toString());
  }

  public void testDiffHelperDeleteII() throws Exception {
    String result = "10d10\n<OneMore=2";
    Iterator<DiffSegment> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffSegment action = actions.next();
    assertSame(DiffAction.DELETED, action.getAction());
    assertEquals(10, action.getLineIntervalStart(LEFT));
    assertEquals(10, action.getLineIntervalEnd(LEFT));
    assertEquals(10, action.getLineIntervalStart(RIGHT));
    assertEquals(10, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: DELETED, left: 10-10, right: 10-10", action.toString());
  }

  public void testDiffHelperAdd() throws Exception {
    String result = "9a10,11\n>OneMore=1\n>OneMore=2\n>OneMore=3";
    Iterator<DiffSegment> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffSegment action = actions.next();
    assertSame(DiffAction.ADDED, action.getAction());
    assertEquals(9, action.getLineIntervalStart(LEFT));
    assertEquals(9, action.getLineIntervalEnd(LEFT));
    assertEquals(10, action.getLineIntervalStart(RIGHT));
    assertEquals(11, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: ADDED, left: 9-9, right: 10-11", action.toString());
  }

  public void testDiffHelperChange() throws Exception {
    String result = "2c2\n<IconIndex=-2388\n---\n>IconIndex=-238";
    Iterator<DiffSegment> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffSegment action = actions.next();
    assertSame(DiffAction.CHANGED, action.getAction());
    assertEquals(2, action.getLineIntervalStart(LEFT));
    assertEquals(2, action.getLineIntervalEnd(LEFT));
    assertEquals(2, action.getLineIntervalStart(RIGHT));
    assertEquals(2, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: CHANGED, left: 2-2, right: 2-2", action.toString());
  }

  public void testDiffHelperChangeII() throws Exception {
    String result =
        "10,12c3,4\n" +
            "< * $HeadURL$\n" +
            "< * $URL$\n" +
            "< * $Id$";
    Iterator<DiffSegment> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffSegment action = actions.next();
    assertSame(DiffAction.CHANGED, action.getAction());
    assertEquals(10, action.getLineIntervalStart(LEFT));
    assertEquals(12, action.getLineIntervalEnd(LEFT));
    assertEquals(3, action.getLineIntervalStart(RIGHT));
    assertEquals(4, action.getLineIntervalEnd(RIGHT));

    assertEquals("DiffSegment: CHANGED, left: 10-12, right: 3-4", action.toString());
  }

  public void testDiffHelperAddAndChange() throws Exception {
    String result = "2c2\n<IconIndex=-2388\n---\n>IconIndex=-238\n8a8,9\n>OneMore=true\n>";
    Iterator<DiffSegment> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffSegment action = actions.next();
    assertSame(DiffAction.CHANGED, action.getAction());
    assertEquals(2, action.getLineIntervalStart(LEFT));
    assertEquals(2, action.getLineIntervalEnd(LEFT));
    assertEquals(2, action.getLineIntervalStart(RIGHT));
    assertEquals(2, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: CHANGED, left: 2-2, right: 2-2", action.toString());
    action = actions.next();
    assertSame(DiffAction.ADDED, action.getAction());
    assertEquals(8, action.getLineIntervalStart(LEFT));
    assertEquals(8, action.getLineIntervalEnd(LEFT));
    assertEquals(8, action.getLineIntervalStart(RIGHT));
    assertEquals(9, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: ADDED, left: 8-8, right: 8-9", action.toString());
  }

}
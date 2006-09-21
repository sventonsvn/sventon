package de.berlios.sventon.diff;

import static de.berlios.sventon.diff.DiffSegment.Side.RIGHT;
import static de.berlios.sventon.diff.DiffSegment.Side.LEFT;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class DiffProducerTest extends TestCase {

  public static final String LINE_BREAK = System.getProperty("line.separator");

  public void testDoNormalDiff() throws Exception {
    String leftString =
        "[.ShellClassInfo]" + LINE_BREAK +
        "IconIndex=-238" + LINE_BREAK +
        "[DeleteOnCopy]" + LINE_BREAK +
        "Owner=Jesper" + LINE_BREAK +
        "Owner=Patrik&Jesper" +  LINE_BREAK +
        "Personalized=14" +  LINE_BREAK +
        "PersonalizedName=Mina videoklipp" + LINE_BREAK;

    String rightString =
        "[.ShellClassInfo]" + LINE_BREAK +
        "IconIndex=-2388" + LINE_BREAK +
        "[DeleteOnCopy]" + LINE_BREAK +
        "Owner=Jesper" + LINE_BREAK +
        "Owner=Patrik&Jesper" + LINE_BREAK +
        "Personalized=14" + LINE_BREAK +
        "PersonalizedName=Mina videoklipp" + LINE_BREAK +
        "OneMore=true" + LINE_BREAK +
        "OneMore=1" + LINE_BREAK +
        "OneMore=2" + LINE_BREAK +
        "OneMore=3" + LINE_BREAK + LINE_BREAK;

    String result =
        "2c2" + LINE_BREAK +
        "<IconIndex=-2388" +  LINE_BREAK +
        "---" + LINE_BREAK +
        ">IconIndex=-238" + LINE_BREAK +
        "8a8,12" +  LINE_BREAK +
        ">OneMore=true" + LINE_BREAK +
        ">OneMore=1" + LINE_BREAK +
        ">OneMore=2" + LINE_BREAK +
        ">OneMore=3" + LINE_BREAK +
        ">" + LINE_BREAK;
    InputStream left = new ByteArrayInputStream(leftString.getBytes());
    InputStream right = new ByteArrayInputStream(rightString.getBytes());

    DiffProducer diffProducer = new DiffProducer(left, right, null);
    OutputStream output = new ByteArrayOutputStream();
    diffProducer.doNormalDiff(output);
    assertEquals(result, output.toString());

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
    assertEquals(12, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: ADDED, left: 8-8, right: 8-12", action.toString());
  }

  public void testDoNormalDiffII() throws Exception {
    String leftString =
        "/**" + LINE_BREAK +
        " * $Author$" +LINE_BREAK +
        " * $Revision$" +LINE_BREAK +
        " * $Date:$" +LINE_BREAK +
        " */" +LINE_BREAK +
        "Test1" +LINE_BREAK +
        "Another test!" +LINE_BREAK +
        "More!" + LINE_BREAK +
        "Even more!" + LINE_BREAK;

    String rightString =
        "/**" + LINE_BREAK +
        " * $Id$" + LINE_BREAK +
        " * $LastChangedDate$" + LINE_BREAK +
        " * $Date$" + LINE_BREAK +
        " * $LastChangedRevision$" + LINE_BREAK +
        " * $Revision$" + LINE_BREAK +
        " * $Rev$" + LINE_BREAK +
        " * $LastChangedBy$" + LINE_BREAK +
        " * $Author$" + LINE_BREAK +
        " * $HeadURL$" + LINE_BREAK +
        " * $URL$" + LINE_BREAK +
        " * $Id$" + LINE_BREAK +
        " */" + LINE_BREAK +
        "Test1" + LINE_BREAK +
        "Another test!" + LINE_BREAK +
        "More!" + LINE_BREAK +
        "Even more!" + LINE_BREAK +
        LINE_BREAK +
        "public String getRev {" + LINE_BREAK +
        " return \"$Rev$\";" + LINE_BREAK +
        LINE_BREAK +
        "}" + LINE_BREAK;

    String result =
        "2a2,8" + LINE_BREAK +
        "> * $Id$" + LINE_BREAK +
        "> * $LastChangedDate$" + LINE_BREAK +
        "> * $Date$" + LINE_BREAK +
        "> * $LastChangedRevision$" + LINE_BREAK +
        "> * $Revision$" + LINE_BREAK +
        "> * $Rev$" + LINE_BREAK +
        "> * $LastChangedBy$" + LINE_BREAK +
        "10,12c3,4" + LINE_BREAK +
        "< * $HeadURL$" + LINE_BREAK +
        "< * $URL$" + LINE_BREAK +
        "< * $Id$" + LINE_BREAK +
        "---" + LINE_BREAK +
        "> * $Revision$" + LINE_BREAK +
        "> * $Date:$" + LINE_BREAK +
        "10a18,22" + LINE_BREAK +
        ">" + LINE_BREAK +
        ">public String getRev {" + LINE_BREAK +
        "> return \"$Rev$\";" + LINE_BREAK +
        ">" + LINE_BREAK +
        ">}" + LINE_BREAK;
    InputStream left = new ByteArrayInputStream(leftString.getBytes());
    InputStream right = new ByteArrayInputStream(rightString.getBytes());

    DiffProducer diffProducer = new DiffProducer(left, right, null);
    OutputStream output = new ByteArrayOutputStream();
    diffProducer.doNormalDiff(output);
    assertEquals(result, output.toString());

    Iterator<DiffSegment> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffSegment action = actions.next();
    assertSame(DiffAction.ADDED, action.getAction());
    assertEquals(2, action.getLineIntervalStart(LEFT));
    assertEquals(2, action.getLineIntervalEnd(LEFT));
    assertEquals(2, action.getLineIntervalStart(RIGHT));
    assertEquals(8, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: ADDED, left: 2-2, right: 2-8", action.toString());
    action = actions.next();
    assertSame(DiffAction.CHANGED, action.getAction());
    assertEquals(10, action.getLineIntervalStart(LEFT));
    assertEquals(12, action.getLineIntervalEnd(LEFT));
    assertEquals(3, action.getLineIntervalStart(RIGHT));
    assertEquals(4, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: CHANGED, left: 10-12, right: 3-4", action.toString());
    action = actions.next();
    assertSame(DiffAction.ADDED, action.getAction());
    assertEquals(10, action.getLineIntervalStart(LEFT));
    assertEquals(10, action.getLineIntervalEnd(LEFT));
    assertEquals(18, action.getLineIntervalStart(RIGHT));
    assertEquals(22, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: ADDED, left: 10-10, right: 18-22", action.toString());
  }

  public void testDoNormalDiffNoDiff() throws Exception {
    String leftString =
        "More!" + LINE_BREAK +
        "Even more!" + LINE_BREAK;

    String rightString =
        "More!" + LINE_BREAK +
        "Even more!" + LINE_BREAK;

    InputStream left = new ByteArrayInputStream(leftString.getBytes());
    InputStream right = new ByteArrayInputStream(rightString.getBytes());

    DiffProducer diffProducer = new DiffProducer(left, right, null);
    OutputStream output = new ByteArrayOutputStream();
    diffProducer.doNormalDiff(output);
    assertEquals("", output.toString());
  }
}
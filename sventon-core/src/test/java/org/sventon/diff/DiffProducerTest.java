package org.sventon.diff;

import de.regnis.q.sequence.line.diff.QDiffGeneratorFactory;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.sventon.diff.DiffProducer;
import org.sventon.diff.DiffResultParser;
import org.sventon.diff.DiffSegment;
import org.sventon.model.DiffAction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.sventon.diff.DiffSegment.Side.LEFT;
import static org.sventon.diff.DiffSegment.Side.RIGHT;

public class DiffProducerTest extends TestCase {

  public static final String NL = System.getProperty("line.separator");

  public void testDoNormalDiff() throws Exception {
    final String leftString =
        "[.ShellClassInfo]" + NL +
            "IconIndex=-238" + NL +
            "[DeleteOnCopy]" + NL +
            "Owner=Jesper" + NL +
            "Owner=Patrik&Jesper" + NL +
            "Personalized=14" + NL +
            "PersonalizedName=Mina videoklipp" + NL;

    final String rightString =
        "[.ShellClassInfo]" + NL +
            "IconIndex=-2388" + NL +
            "[DeleteOnCopy]" + NL +
            "Owner=Jesper" + NL +
            "Owner=Patrik&Jesper" + NL +
            "Personalized=14" + NL +
            "PersonalizedName=Mina videoklipp" + NL +
            "OneMore=true" + NL +
            "OneMore=1" + NL +
            "OneMore=2" + NL +
            "OneMore=3" + NL + NL;

    final String result =
        "2c2" + NL +
            "<IconIndex=-2388" + NL +
            "---" + NL +
            ">IconIndex=-238" + NL +
            "8a8,12" + NL +
            ">OneMore=true" + NL +
            ">OneMore=1" + NL +
            ">OneMore=2" + NL +
            ">OneMore=3" + NL +
            ">" + NL;

    final InputStream left = IOUtils.toInputStream(leftString);
    final InputStream right = IOUtils.toInputStream(rightString);

    final DiffProducer diffProducer = new DiffProducer(left, right, null);
    final OutputStream output = new ByteArrayOutputStream();
    diffProducer.doNormalDiff(output);
    assertEquals(result, output.toString());

    final Iterator<DiffSegment> actions = DiffResultParser.parseNormalDiffResult(result).iterator();

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

  @SuppressWarnings({"ConstantConditions"})
  public void testDoNormalDiffII() throws Exception {
    final String leftString =
        "/**" + NL +
            " * $Author$" + NL +
            " * $Revision$" + NL +
            " * $Date:$" + NL +
            " */" + NL +
            "Test1" + NL +
            "Another test!" + NL +
            "More!" + NL +
            "Even more!" + NL;

    final String rightString =
        "/**" + NL +
            " * $Id$" + NL +
            " * $LastChangedDate$" + NL +
            " * $Date$" + NL +
            " * $LastChangedRevision$" + NL +
            " * $Revision$" + NL +
            " * $Rev$" + NL +
            " * $LastChangedBy$" + NL +
            " * $Author$" + NL +
            " * $HeadURL$" + NL +
            " * $URL$" + NL +
            " * $Id$" + NL +
            " */" + NL +
            "Test1" + NL +
            "Another test!" + NL +
            "More!" + NL +
            "Even more!" + NL +
            NL +
            "public String getRev {" + NL +
            " return \"$Rev$\";" + NL +
            NL +
            "}" + NL;

    final String result =
        "2,8d2" + NL +
            "< * $Id$" + NL +
            "< * $LastChangedDate$" + NL +
            "< * $Date$" + NL +
            "< * $LastChangedRevision$" + NL +
            "< * $Revision$" + NL +
            "< * $Rev$" + NL +
            "< * $LastChangedBy$" + NL +
            "3,4c10,12" + NL +
            "< * $Revision$" + NL +
            "< * $Date:$" + NL +
            "---" + NL +
            "> * $HeadURL$" + NL +
            "> * $URL$" + NL +
            "> * $Id$" + NL +
            "18,22d10" + NL +
            "<" + NL +
            "<public String getRev {" + NL +
            "< return \"$Rev$\";" + NL +
            "<" + NL +
            "<}" + NL;

    final InputStream left = IOUtils.toInputStream(leftString);
    final InputStream right = IOUtils.toInputStream(rightString);

    final DiffProducer diffProducer = new DiffProducer(right, left, null);
    final OutputStream output = new ByteArrayOutputStream();
    diffProducer.doNormalDiff(output);
    assertEquals(result, output.toString());

    final Iterator<DiffSegment> actions = DiffResultParser.parseNormalDiffResult(result).iterator();

    DiffSegment action = actions.next();
    assertSame(DiffAction.DELETED, action.getAction());
    assertEquals(2, action.getLineIntervalStart(LEFT));
    assertEquals(8, action.getLineIntervalEnd(LEFT));
    assertEquals(2, action.getLineIntervalStart(RIGHT));
    assertEquals(2, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: DELETED, left: 2-8, right: 2-2", action.toString());
    action = actions.next();
    assertSame(DiffAction.CHANGED, action.getAction());
    assertEquals(3, action.getLineIntervalStart(LEFT));
    assertEquals(4, action.getLineIntervalEnd(LEFT));
    assertEquals(10, action.getLineIntervalStart(RIGHT));
    assertEquals(12, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: CHANGED, left: 3-4, right: 10-12", action.toString());
    action = actions.next();
    assertSame(DiffAction.DELETED, action.getAction());
    assertEquals(18, action.getLineIntervalStart(LEFT));
    assertEquals(22, action.getLineIntervalEnd(LEFT));
    assertEquals(10, action.getLineIntervalStart(RIGHT));
    assertEquals(10, action.getLineIntervalEnd(RIGHT));
    assertEquals("DiffSegment: DELETED, left: 18-22, right: 10-10", action.toString());
  }

  public void testDoNormalDiffIII() throws Exception {
    final String leftString = NL + "test" + NL;
    final String rightString = "test" + NL;
    final String result = "1d1" + NL + "<" + NL;

    final InputStream left = new ByteArrayInputStream(leftString.getBytes());
    final InputStream right = new ByteArrayInputStream(rightString.getBytes());

    final DiffProducer diffProducer = new DiffProducer(left, right, null);
    final OutputStream output = new ByteArrayOutputStream();
    diffProducer.doNormalDiff(output);
    assertEquals(result, output.toString());
  }

  public void testDoNormalDiffNoDiff() throws Exception {
    final String leftString =
        "More!" + NL +
            "Even more!" + NL;

    final String rightString =
        "More!" + NL +
            "Even more!" + NL;

    final InputStream left = IOUtils.toInputStream(leftString);
    final InputStream right = IOUtils.toInputStream(rightString);

    final DiffProducer diffProducer = new DiffProducer(left, right, null);
    final OutputStream output = new ByteArrayOutputStream();
    diffProducer.doNormalDiff(output);
    assertEquals("", output.toString());
  }

  public void testDoUnifiedDiffNoGutter() throws Exception {
    final String leftString =
        "[.ShellClassInfo]" + NL +
            "IconIndex=-238" + NL +
            "[DeleteOnCopy]" + NL +
            "Owner=Jesper" + NL +
            "Owner=Patrik&Jesper" + NL +
            "Personalized=14" + NL +
            "PersonalizedName=Mina videoklipp" + NL;

    final String rightString =
        "[.ShellClassInfo]" + NL +
            "IconIndex=-2388" + NL +
            "[DeleteOnCopy]" + NL +
            "Owner=Jesper" + NL +
            "Owner=Patrik&Jesper" + NL +
            "Personalized=14" + NL +
            "PersonalizedName=Mina videoklipp" + NL +
            "OneMore=true" + NL +
            "OneMore=1" + NL +
            "OneMore=2" + NL +
            "OneMore=3" + NL + NL;

    final String result =
        "@@ -2 +2 @@" + NL +
            "-IconIndex=-238" + NL +
            "+IconIndex=-2388" + NL +
            "@@ -8 +8,5 @@" + NL +
            "+OneMore=true" + NL +
            "+OneMore=1" + NL +
            "+OneMore=2" + NL +
            "+OneMore=3" + NL +
            "+" + NL;


    final InputStream left = IOUtils.toInputStream(leftString);
    final InputStream right = IOUtils.toInputStream(rightString);

    final Map props = new HashMap();
    props.put(QDiffGeneratorFactory.GUTTER_PROPERTY, 0);
    final DiffProducer diffProducer = new DiffProducer(left, right, null, props);
    final OutputStream output = new ByteArrayOutputStream();
    diffProducer.doUnifiedDiff(output);
    assertEquals(result, output.toString());
  }
}
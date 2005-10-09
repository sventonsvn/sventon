package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

import java.io.*;
import java.util.Iterator;

public class DiffProducerTest extends TestCase {

  public void testDoNormalDiff() throws Exception {
    String leftString =
        "[.ShellClassInfo]\n" +
        "IconIndex=-238\n" +
        "[DeleteOnCopy]\n" +
        "Owner=Jesper\n" +
        "Owner=Patrik&Jesper\n" +
        "Personalized=14\n" +
        "PersonalizedName=Mina videoklipp\n";

    String rightString =
        "[.ShellClassInfo]\n" +
        "IconIndex=-2388\n" +
        "[DeleteOnCopy]\n" +
        "Owner=Jesper\n" +
        "Owner=Patrik&Jesper\n" +
        "Personalized=14\n" +
        "PersonalizedName=Mina videoklipp\n" +
        "OneMore=true\n" +
        "OneMore=1\n" +
        "OneMore=2\n" +
        "OneMore=3\n\n";

    String result =
        "2c2\r\n" +
        "<IconIndex=-2388\n" +
        "---\r\n" +
        ">IconIndex=-238\n" +
        "8a8,12\r\n" +
        ">OneMore=true\n" +
        ">OneMore=1\n" +
        ">OneMore=2\n" +
        ">OneMore=3\n" +
        ">\n";
    InputStream left = new ByteArrayInputStream(leftString.getBytes());
    InputStream right = new ByteArrayInputStream(rightString.getBytes());

    DiffProducer diffProducer = new DiffProducer(left, right, null);
    OutputStream output = new ByteArrayOutputStream();
    diffProducer.doNormalDiff(output);
    assertEquals(result, output.toString());

    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffAction action = actions.next();
    assertEquals(DiffAction.ADD_ACTION, action.getAction());
    assertEquals(8, action.getLeftLineIntervalStart());
    assertEquals(8, action.getLeftLineIntervalEnd());
    assertEquals(8, action.getRightLineIntervalStart());
    assertEquals(12, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: a, left: 8-8, right: 8-12", action.toString());
    action = actions.next();
    assertEquals(DiffAction.CHANGE_ACTION, action.getAction());
    assertEquals(2, action.getLeftLineIntervalStart());
    assertEquals(2, action.getLeftLineIntervalEnd());
    assertEquals(2, action.getRightLineIntervalStart());
    assertEquals(2, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: c, left: 2-2, right: 2-2", action.toString());
  }

  public void testDoNormalDiffII() throws Exception {
    String leftString =
        "/**\n" +
        " * $Author$\n" +
        " * $Revision$\n" +
        " * $Date:$\n" +
        " */\n" +
        "Test1\n" +
        "Another test!\n" +
        "More!\n" +
        "Even more!\n";

    String rightString =
        "/**\n" +
        " * $Id$\n" +
        " * $LastChangedDate$\n" +
        " * $Date$\n" +
        " * $LastChangedRevision$\n" +
        " * $Revision$\n" +
        " * $Rev$\n" +
        " * $LastChangedBy$\n" +
        " * $Author$\n" +
        " * $HeadURL$\n" +
        " * $URL$\n" +
        " * $Id$\n" +
        " */\n" +
        "Test1\n" +
        "Another test!\n" +
        "More!\n" +
        "Even more!\n" +
        "\n" +
        "public String getRev {\n" +
        " return \"$Rev$\";\n" +
        "\n" +
        "}\n";

    String result =
        "2a2,8\r\n" +
        "> * $Id$\n" +
        "> * $LastChangedDate$\n" +
        "> * $Date$\n" +
        "> * $LastChangedRevision$\n" +
        "> * $Revision$\n" +
        "> * $Rev$\n" +
        "> * $LastChangedBy$\n" +
        "10,12c3,4\r\n" +
        "< * $HeadURL$\n" +
        "< * $URL$\n" +
        "< * $Id$\n" +
        "---\r\n" +
        "> * $Revision$\n" +
        "> * $Date:$\n" +
        "10a18,22\r\n" +
        ">\n" +
        ">public String getRev {\n" +
        "> return \"$Rev$\";\n" +
        ">\n" +
        ">}\n";
    InputStream left = new ByteArrayInputStream(leftString.getBytes());
    InputStream right = new ByteArrayInputStream(rightString.getBytes());

    DiffProducer diffProducer = new DiffProducer(left, right, null);
    OutputStream output = new ByteArrayOutputStream();
    diffProducer.doNormalDiff(output);
    assertEquals(result, output.toString());

    Iterator<DiffAction> actions = DiffResultParser.parseNormalDiffResult(result).iterator();
    DiffAction action = actions.next();
    assertEquals(DiffAction.ADD_ACTION, action.getAction());
    assertEquals(10, action.getLeftLineIntervalStart());
    assertEquals(10, action.getLeftLineIntervalEnd());
    assertEquals(18, action.getRightLineIntervalStart());
    assertEquals(22, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: a, left: 10-10, right: 18-22", action.toString());
    action = actions.next();
    assertEquals(DiffAction.CHANGE_ACTION, action.getAction());
    assertEquals(10, action.getLeftLineIntervalStart());
    assertEquals(12, action.getLeftLineIntervalEnd());
    assertEquals(3, action.getRightLineIntervalStart());
    assertEquals(4, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: c, left: 10-12, right: 3-4", action.toString());
    action = actions.next();
    assertEquals(DiffAction.ADD_ACTION, action.getAction());
    assertEquals(2, action.getLeftLineIntervalStart());
    assertEquals(2, action.getLeftLineIntervalEnd());
    assertEquals(2, action.getRightLineIntervalStart());
    assertEquals(8, action.getRightLineIntervalEnd());
    assertEquals("DiffAction: a, left: 2-2, right: 2-8", action.toString());
  }
}
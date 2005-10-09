package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

import java.io.*;

public class DiffHandlerTest extends TestCase {

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

    DiffHandler diffHandler = new DiffHandler(left, right, null);
    OutputStream output = new ByteArrayOutputStream();
    diffHandler.doNormalDiff(output);
    assertEquals(result, output.toString());
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

    DiffHandler diffHandler = new DiffHandler(left, right, null);
    OutputStream output = new ByteArrayOutputStream();
    diffHandler.doNormalDiff(output);
    assertEquals(result, output.toString());
  }


}
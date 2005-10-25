package de.berlios.sventon.diff;

import de.berlios.sventon.svnsupport.SourceLine;
import junit.framework.TestCase;

public class DiffTest extends TestCase {

  public void testDiff() throws Exception {

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
        "OneMore=3\n";

    Diff diff = new Diff(leftString, rightString);
    assertEquals(diff.getLeft().size(), diff.getRight().size());
  }

  public void testDiffII() throws Exception {

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

    String leftResult =
        "/**\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        " * $Author$\n" +
        "c * $Revision$\n" +
        "c * $Date:$\n" +
        "c\n" +
        " */\n" +
        "Test1\n" +
        "Another test!\n" +
        "More!\n" +
        "Even more!\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n";

    String rightResult =
        "/**\n" +
        "a * $Id$\n" +
        "a * $LastChangedDate$\n" +
        "a * $Date$\n" +
        "a * $LastChangedRevision$\n" +
        "a * $Revision$\n" +
        "a * $Rev$\n" +
        "a * $LastChangedBy$\n" +
        " * $Author$\n" +
        "c * $HeadURL$\n" +
        "c * $URL$\n" +
        "c * $Id$\n" +
        " */\n" +
        "Test1\n" +
        "Another test!\n" +
        "More!\n" +
        "Even more!\n" +
        "a\n" +
        "apublic String getRev {\n" +
        "a return \"$Rev$\";\n" +
        "a\n" +
        "a}\n";

/*

2a2,8
> * $Id$
> * $LastChangedDate$
> * $Date$
> * $LastChangedRevision$
> * $Revision$
> * $Rev$
> * $LastChangedBy$
10,12c3,4
< * $HeadURL$
< * $URL$
< * $Id$

*/
    Diff diff = new Diff(leftString, rightString);
    assertEquals(diff.getLeft().size(), diff.getRight().size());

    StringBuffer sb = new StringBuffer();
    for (SourceLine tempStr : diff.getLeft()) {
      sb.append(tempStr.getAction());
      sb.append(tempStr.getLine());
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuffer();
    for (SourceLine tempStr : diff.getRight()) {
      sb.append(tempStr.getAction());
      sb.append(tempStr.getLine());
      sb.append("\n");
    }
    assertEquals(rightResult, sb.toString());

  }

  public void testDiffIII() throws Exception {

    String leftString =
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
        "OneMore=3\n" +
        "OneMore=4\n" +
        "OneMore=5\n" +
        "OneMore=6\n" +
        "OneMore=7\n" +
        "OneMore=8\n" +
        "OneMore=9\n" +
        "";

    String rightString =
        "[.ShellClassInfo]\n" +
        "IconIndex=-2388\n" +
        "[DeleteOnCopy]\n" +
        "Owner=Jesper\n" +
        "Owner=Patrik&Jesper\n" +
        "Personalized=14\n" +
        "PersonalizedName=Mina videoklipp\n" +
        "OneMore=true\n" +
        "OneMore=4\n" +
        "OneMore=5\n" +
        "OneMore=6\n" +
        "OneMore=9\n" +
        "";

// Diff result
/*

9,11d9
<OneMore=1
<OneMore=2
<OneMore=3
15,16d12
<OneMore=7
<OneMore=8

*/
    Diff diff = new Diff(leftString, rightString);
//    System.out.println("diff.getLeft() = " + diff.getLeft());
//    System.out.println("diff.getRight() = " + diff.getRight());

    assertEquals(diff.getLeft().size(), diff.getRight().size());
  }


  public void testDiffIV() throws Exception {

    String leftString =
        "[.ShellClassInfo]\n" +
        "InfoTip=@Shell32.dll,-12690\n" +
        "IconFile=%SystemRoot%\\system32\\SHELL32.dll\n" +
        "IconIndex=-238\n" +
        "[DeleteOnCopy]\n" +
        "Owner=Jesper\n" +
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
        "OneMore=4\n" +
        "OneMore=5\n" +
        "OneMore=6\n" +
        "OneMore=9\n";

    String leftResult =
        "[.ShellClassInfo]\n" +
        "cInfoTip=@Shell32.dll,-12690\n" +
        "cIconFile=%SystemRoot%\\system32\\SHELL32.dll\n" +
        "cIconIndex=-238\n" +
        "[DeleteOnCopy]\n" +
        "Owner=Jesper\n" +
        "a\n" +
        "Personalized=14\n" +
        "PersonalizedName=Mina videoklipp\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n";

    String rightResult =
        "[.ShellClassInfo]\n" +
        "cIconIndex=-2388\n" +
        "c\n" +
        "c\n" +
        "[DeleteOnCopy]\n" +
        "Owner=Jesper\n" +
        "aOwner=Patrik&Jesper\n" +
        "Personalized=14\n" +
        "PersonalizedName=Mina videoklipp\n" +
        "aOneMore=true\n" +
        "aOneMore=4\n" +
        "aOneMore=5\n" +
        "aOneMore=6\n" +
        "aOneMore=9\n";


// Diff result
/*

2c2,4
<IconIndex=-2388
---
>InfoTip=@Shell32.dll,-12690
>IconFile=%SystemRoot%\system32\SHELL32.dll
>IconIndex=-238
7a5
>Owner=Patrik&Jesper
9a8,12
>OneMore=true
>OneMore=4
>OneMore=5
>OneMore=6
>OneMore=9

*/
    Diff diff = new Diff(leftString, rightString);

//    System.out.println("diff.getDiffResultString() = " + diff.getDiffResultString());

    assertEquals(diff.getLeft().size(), diff.getRight().size());

    StringBuffer sb = new StringBuffer();
    for (SourceLine tempStr : diff.getLeft()) {
      sb.append(tempStr.getAction());
      sb.append(tempStr.getLine());
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuffer();
    for (SourceLine tempStr : diff.getRight()) {
      sb.append(tempStr.getAction());
      sb.append(tempStr.getLine());
      sb.append("\n");
    }
    assertEquals(rightResult, sb.toString());
  }

  public void testDiffNoDiff() throws Exception {

    String leftString =
        "[.ShellClassInfo]\n";

    String rightString =
        "[.ShellClassInfo]\n";

    Diff diff;
    try {
      diff = new Diff(leftString, rightString);
      fail("Should raise DiffException");
    } catch (DiffException de) {
      // expected
    }

  }
}
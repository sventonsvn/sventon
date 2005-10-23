package de.berlios.sventon.svnsupport;

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
        "A\n" +
        "A\n" +
        "A\n" +
        "A\n" +
        "A\n" +
        "A\n" +
        "A\n" +
        " * $Author$\n" +
        "C * $Revision$\n" +
        "C * $Date:$\n" +
        "C\n" +
        " */\n" +
        "Test1\n" +
        "Another test!\n" +
        "More!\n" +
        "Even more!\n" +
        "A\n" +
        "A\n" +
        "A\n" +
        "A\n" +
        "A\n";

    String rightResult =
        "/**\n" +
        "A * $Id$\n" +
        "A * $LastChangedDate$\n" +
        "A * $Date$\n" +
        "A * $LastChangedRevision$\n" +
        "A * $Revision$\n" +
        "A * $Rev$\n" +
        "A * $LastChangedBy$\n" +
        " * $Author$\n" +
        "C * $HeadURL$\n" +
        "C * $URL$\n" +
        "C * $Id$\n" +
        " */\n" +
        "Test1\n" +
        "Another test!\n" +
        "More!\n" +
        "Even more!\n" +
        "A\n" +
        "Apublic String getRev {\n" +
        "A return \"$Rev$\";\n" +
        "A\n" +
        "A}\n";

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
      sb.append(tempStr);
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuffer();
    for (SourceLine tempStr : diff.getRight()) {
      sb.append(tempStr);
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
        "CInfoTip=@Shell32.dll,-12690\n" +
        "CIconFile=%SystemRoot%\\system32\\SHELL32.dll\n" +
        "CIconIndex=-238\n" +
        "[DeleteOnCopy]\n" +
        "Owner=Jesper\n" +
        "A\n" +
        "Personalized=14\n" +
        "PersonalizedName=Mina videoklipp\n" +
        "A\n" +
        "A\n" +
        "A\n" +
        "A\n" +
        "A\n";

    String rightResult =
        "[.ShellClassInfo]\n" +
        "CIconIndex=-2388\n" +
        "C\n" +
        "C\n" +
        "[DeleteOnCopy]\n" +
        "Owner=Jesper\n" +
        "AOwner=Patrik&Jesper\n" +
        "Personalized=14\n" +
        "PersonalizedName=Mina videoklipp\n" +
        "AOneMore=true\n" +
        "AOneMore=4\n" +
        "AOneMore=5\n" +
        "AOneMore=6\n" +
        "AOneMore=9\n";


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
      sb.append(tempStr);
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuffer();
    for (SourceLine tempStr : diff.getRight()) {
      sb.append(tempStr);
      sb.append("\n");
    }
    assertEquals(rightResult, sb.toString());
  }
}
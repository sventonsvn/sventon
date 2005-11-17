package de.berlios.sventon.diff;

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
        "<span class=\"sventonLineNo\">1: </span>/**\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "<span class=\"sventonLineNo\">2: </span> * $Author$\n" +
        "c<span class=\"sventonLineNo\">3: </span> * $Revision$\n" +
        "c<span class=\"sventonLineNo\">4: </span> * $Date:$\n" +
        "c\n" +
        "<span class=\"sventonLineNo\">5: </span> */\n" +
        "<span class=\"sventonLineNo\">6: </span>Test1\n" +
        "<span class=\"sventonLineNo\">7: </span>Another test!\n" +
        "<span class=\"sventonLineNo\">8: </span>More!\n" +
        "<span class=\"sventonLineNo\">9: </span>Even more!\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n";

    String rightResult =
        "<span class=\"sventonLineNo\">1: </span>/**\n" +
        "a<span class=\"sventonLineNo\">2: </span> * $Id$\n" +
        "a<span class=\"sventonLineNo\">3: </span> * $LastChangedDate$\n" +
        "a<span class=\"sventonLineNo\">4: </span> * $Date$\n" +
        "a<span class=\"sventonLineNo\">5: </span> * $LastChangedRevision$\n" +
        "a<span class=\"sventonLineNo\">6: </span> * $Revision$\n" +
        "a<span class=\"sventonLineNo\">7: </span> * $Rev$\n" +
        "a<span class=\"sventonLineNo\">8: </span> * $LastChangedBy$\n" +
        "<span class=\"sventonLineNo\">9: </span> * $Author$\n" +
        "c<span class=\"sventonLineNo\">10: </span> * $HeadURL$\n" +
        "c<span class=\"sventonLineNo\">11: </span> * $URL$\n" +
        "c<span class=\"sventonLineNo\">12: </span> * $Id$\n" +
        "<span class=\"sventonLineNo\">13: </span> */\n" +
        "<span class=\"sventonLineNo\">14: </span>Test1\n" +
        "<span class=\"sventonLineNo\">15: </span>Another test!\n" +
        "<span class=\"sventonLineNo\">16: </span>More!\n" +
        "<span class=\"sventonLineNo\">17: </span>Even more!\n" +
        "a<span class=\"sventonLineNo\">18: </span>\n" +
        "a<span class=\"sventonLineNo\">19: </span>public String getRev {\n" +
        "a<span class=\"sventonLineNo\">20: </span> return \"$Rev$\";\n" +
        "a<span class=\"sventonLineNo\">21: </span>\n" +
        "a<span class=\"sventonLineNo\">22: </span>}\n";

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
        "<span class=\"sventonLineNo\">1: </span>[.ShellClassInfo]\n" +
        "c<span class=\"sventonLineNo\">2: </span>InfoTip=@Shell32.dll,-12690\n" +
        "c<span class=\"sventonLineNo\">3: </span>IconFile=%SystemRoot%\\system32\\SHELL32.dll\n" +
        "c<span class=\"sventonLineNo\">4: </span>IconIndex=-238\n" +
        "<span class=\"sventonLineNo\">5: </span>[DeleteOnCopy]\n" +
        "<span class=\"sventonLineNo\">6: </span>Owner=Jesper\n" +
        "a\n" +
        "<span class=\"sventonLineNo\">7: </span>Personalized=14\n" +
        "<span class=\"sventonLineNo\">8: </span>PersonalizedName=Mina videoklipp\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n" +
        "a\n";

    String rightResult =
        "<span class=\"sventonLineNo\">1: </span>[.ShellClassInfo]\n" +
        "c<span class=\"sventonLineNo\">2: </span>IconIndex=-2388\n" +
        "c\n" +
        "c\n" +
        "<span class=\"sventonLineNo\">3: </span>[DeleteOnCopy]\n" +
        "<span class=\"sventonLineNo\">4: </span>Owner=Jesper\n" +
        "a<span class=\"sventonLineNo\">5: </span>Owner=Patrik&Jesper\n" +
        "<span class=\"sventonLineNo\">6: </span>Personalized=14\n" +
        "<span class=\"sventonLineNo\">7: </span>PersonalizedName=Mina videoklipp\n" +
        "a<span class=\"sventonLineNo\">8: </span>OneMore=true\n" +
        "a<span class=\"sventonLineNo\">9: </span>OneMore=4\n" +
        "a<span class=\"sventonLineNo\">10: </span>OneMore=5\n" +
        "a<span class=\"sventonLineNo\">11: </span>OneMore=6\n" +
        "a<span class=\"sventonLineNo\">12: </span>OneMore=9\n";


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
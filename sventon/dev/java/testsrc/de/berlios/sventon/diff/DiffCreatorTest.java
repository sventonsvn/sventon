package de.berlios.sventon.diff;

import junit.framework.TestCase;

public class DiffCreatorTest extends TestCase {

  public void testDiff() throws Exception {

    final String leftString =
        "[.ShellClassInfo]\n" +
            "IconIndex=-238\n" +
            "[DeleteOnCopy]\n" +
            "Owner=Jesper\n" +
            "Owner=Patrik&Jesper\n" +
            "Personalized=14\n" +
            "PersonalizedName=Mina videoklipp\n";

    final String rightString =
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

    final DiffCreator diffCreator = new DiffCreator(leftString, null, rightString, null);
    assertEquals(diffCreator.getLeft().size(), diffCreator.getRight().size());
  }

  public void testDiffII() throws Exception {

    final String leftString =
        "/**\n" +
            " * $Author$\n" +
            " * $Revision$\n" +
            " * $Date:$\n" +
            " */\n" +
            "Test1\n" +
            "Another test!\n" +
            "More!\n" +
            "Even more!\n";

    final String rightString =
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

    final String leftResult =
        "u<span class=\"sventonLineNo\">    1:&nbsp;</span>/**\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "u<span class=\"sventonLineNo\">    2:&nbsp;</span> * $Author$\n" +
            "c<span class=\"sventonLineNo\">    3:&nbsp;</span> * $Revision$\n" +
            "c<span class=\"sventonLineNo\">    4:&nbsp;</span> * $Date:$\n" +
            "c\n" +
            "u<span class=\"sventonLineNo\">    5:&nbsp;</span> */\n" +
            "u<span class=\"sventonLineNo\">    6:&nbsp;</span>Test1\n" +
            "u<span class=\"sventonLineNo\">    7:&nbsp;</span>Another test!\n" +
            "u<span class=\"sventonLineNo\">    8:&nbsp;</span>More!\n" +
            "u<span class=\"sventonLineNo\">    9:&nbsp;</span>Even more!\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n";

    final String rightResult =
        "u<span class=\"sventonLineNo\">    1:&nbsp;</span>/**\n" +
            "a<span class=\"sventonLineNo\">    2:&nbsp;</span> * $Id$\n" +
            "a<span class=\"sventonLineNo\">    3:&nbsp;</span> * $LastChangedDate$\n" +
            "a<span class=\"sventonLineNo\">    4:&nbsp;</span> * $Date$\n" +
            "a<span class=\"sventonLineNo\">    5:&nbsp;</span> * $LastChangedRevision$\n" +
            "a<span class=\"sventonLineNo\">    6:&nbsp;</span> * $Revision$\n" +
            "a<span class=\"sventonLineNo\">    7:&nbsp;</span> * $Rev$\n" +
            "a<span class=\"sventonLineNo\">    8:&nbsp;</span> * $LastChangedBy$\n" +
            "u<span class=\"sventonLineNo\">    9:&nbsp;</span> * $Author$\n" +
            "c<span class=\"sventonLineNo\">   10:&nbsp;</span> * $HeadURL$\n" +
            "c<span class=\"sventonLineNo\">   11:&nbsp;</span> * $URL$\n" +
            "c<span class=\"sventonLineNo\">   12:&nbsp;</span> * $Id$\n" +
            "u<span class=\"sventonLineNo\">   13:&nbsp;</span> */\n" +
            "u<span class=\"sventonLineNo\">   14:&nbsp;</span>Test1\n" +
            "u<span class=\"sventonLineNo\">   15:&nbsp;</span>Another test!\n" +
            "u<span class=\"sventonLineNo\">   16:&nbsp;</span>More!\n" +
            "u<span class=\"sventonLineNo\">   17:&nbsp;</span>Even more!\n" +
            "a<span class=\"sventonLineNo\">   18:&nbsp;</span>\n" +
            "a<span class=\"sventonLineNo\">   19:&nbsp;</span>public String getRev {\n" +
            "a<span class=\"sventonLineNo\">   20:&nbsp;</span> return \"$Rev$\";\n" +
            "a<span class=\"sventonLineNo\">   21:&nbsp;</span>\n" +
            "a<span class=\"sventonLineNo\">   22:&nbsp;</span>}\n";

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
    final DiffCreator diffCreator = new DiffCreator(leftString, null, rightString, null);
    assertEquals(diffCreator.getLeft().size(), diffCreator.getRight().size());

    StringBuilder sb = new StringBuilder();
    for (SourceLine tempStr : diffCreator.getLeft()) {
      sb.append(tempStr.getAction().getCode());
      sb.append(tempStr.getLine());
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuilder();
    for (SourceLine tempStr : diffCreator.getRight()) {
      sb.append(tempStr.getAction().getCode());
      sb.append(tempStr.getLine());
      sb.append("\n");
    }
    assertEquals(rightResult, sb.toString());

  }

  public void testDiffIII() throws Exception {

    final String leftString =
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

    final String rightString =
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
    final DiffCreator diffCreator = new DiffCreator(leftString, null, rightString, null);
//    System.out.println("diff.getLeft() = " + diff.getLeft());
//    System.out.println("diff.getRight() = " + diff.getRight());

    assertEquals(diffCreator.getLeft().size(), diffCreator.getRight().size());
  }


  public void testDiffIV() throws Exception {

    final String leftString =
        "[.ShellClassInfo]\n" +
            "InfoTip=@Shell32.dll,-12690\n" +
            "IconFile=%SystemRoot%\\system32\\SHELL32.dll\n" +
            "IconIndex=-238\n" +
            "[DeleteOnCopy]\n" +
            "Owner=Jesper\n" +
            "Personalized=14\n" +
            "PersonalizedName=Mina videoklipp\n";

    final String rightString =
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

    final String leftResult =
        "u<span class=\"sventonLineNo\">    1:&nbsp;</span>[.ShellClassInfo]\n" +
            "c<span class=\"sventonLineNo\">    2:&nbsp;</span>InfoTip=@Shell32.dll,-12690\n" +
            "c<span class=\"sventonLineNo\">    3:&nbsp;</span>IconFile=%SystemRoot%\\system32\\SHELL32.dll\n" +
            "c<span class=\"sventonLineNo\">    4:&nbsp;</span>IconIndex=-238\n" +
            "u<span class=\"sventonLineNo\">    5:&nbsp;</span>[DeleteOnCopy]\n" +
            "u<span class=\"sventonLineNo\">    6:&nbsp;</span>Owner=Jesper\n" +
            "a\n" +
            "u<span class=\"sventonLineNo\">    7:&nbsp;</span>Personalized=14\n" +
            "u<span class=\"sventonLineNo\">    8:&nbsp;</span>PersonalizedName=Mina videoklipp\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n";

    final String rightResult =
        "u<span class=\"sventonLineNo\">    1:&nbsp;</span>[.ShellClassInfo]\n" +
            "c<span class=\"sventonLineNo\">    2:&nbsp;</span>IconIndex=-2388\n" +
            "c\n" +
            "c\n" +
            "u<span class=\"sventonLineNo\">    3:&nbsp;</span>[DeleteOnCopy]\n" +
            "u<span class=\"sventonLineNo\">    4:&nbsp;</span>Owner=Jesper\n" +
            "a<span class=\"sventonLineNo\">    5:&nbsp;</span>Owner=Patrik&Jesper\n" +
            "u<span class=\"sventonLineNo\">    6:&nbsp;</span>Personalized=14\n" +
            "u<span class=\"sventonLineNo\">    7:&nbsp;</span>PersonalizedName=Mina videoklipp\n" +
            "a<span class=\"sventonLineNo\">    8:&nbsp;</span>OneMore=true\n" +
            "a<span class=\"sventonLineNo\">    9:&nbsp;</span>OneMore=4\n" +
            "a<span class=\"sventonLineNo\">   10:&nbsp;</span>OneMore=5\n" +
            "a<span class=\"sventonLineNo\">   11:&nbsp;</span>OneMore=6\n" +
            "a<span class=\"sventonLineNo\">   12:&nbsp;</span>OneMore=9\n";

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
    final DiffCreator diffCreator = new DiffCreator(leftString, null, rightString, null);

//    System.out.println("diff.getDiffResultString() = " + diff.getDiffResultString());

    assertEquals(diffCreator.getLeft().size(), diffCreator.getRight().size());

    StringBuilder sb = new StringBuilder();
    for (SourceLine tempStr : diffCreator.getLeft()) {
      sb.append(tempStr.getAction().getCode());
      sb.append(tempStr.getLine());
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuilder();
    for (SourceLine tempStr : diffCreator.getRight()) {
      sb.append(tempStr.getAction().getCode());
      sb.append(tempStr.getLine());
      sb.append("\n");
    }
    assertEquals(rightResult, sb.toString());
  }

  public void testDiffNoDiff() throws Exception {

    final String leftString = "[.ShellClassInfo]\n";
    final String rightString = "[.ShellClassInfo]\n";

    try {
      new DiffCreator(leftString, null, rightString, null);
      fail("Should raise DiffException");
    } catch (DiffException de) {
      // expected
    }

  }
}
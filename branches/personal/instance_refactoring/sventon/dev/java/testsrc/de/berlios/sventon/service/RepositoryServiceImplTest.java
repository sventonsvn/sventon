package de.berlios.sventon.service;

import de.berlios.sventon.appl.InstanceConfiguration;
import de.berlios.sventon.diff.IdenticalFilesException;
import de.berlios.sventon.diff.IllegalFileFormatException;
import de.berlios.sventon.diff.SourceLine;
import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.web.command.DiffCommand;
import de.berlios.sventon.web.model.SideBySideDiffRow;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNSession;

import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class RepositoryServiceImplTest extends TestCase {

  private static final String ENCODING = "UTF-8";

  private static final String BR = System.getProperty("line.separator");

  public void testDiffUnifiedBinaryFile() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub(null, null) {
      public long getFile(String path, long revision, Map properties, OutputStream contents) {
        properties.put(SVNProperty.MIME_TYPE, "application/octet-stream");
        return 0;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final InstanceConfiguration configuration = new InstanceConfiguration();

    final DiffCommand diffCommand = new DiffCommand(new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"});

    try {
      service.diffUnified(repository, diffCommand, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IllegalFileFormatException e) {
      // expected
    }
  }

  public void testDiffUnifiedIdenticalFiles1() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub(null, null) {
      public long getFile(String path, long revision, Map properties, OutputStream contents) {
        return 0;
      }

      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final InstanceConfiguration configuration = new InstanceConfiguration();

    final DiffCommand diffCommand = new DiffCommand(new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"});

    try {
      service.diffUnified(repository, diffCommand, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffUnifiedIdenticalFiles2() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub(null, null) {
      public long getFile(String path, long revision, Map properties, OutputStream contents) {
        final String fileContents = "test file contents";
        if (contents != null) {
          try {
            contents.write(fileContents.getBytes());
          } catch (IOException e) {
            throw new RuntimeException("FAILED!");
          }
        }
        return 0;
      }

      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final InstanceConfiguration configuration = new InstanceConfiguration();

    final DiffCommand diffCommand = new DiffCommand(new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"});

    try {
      service.diffUnified(repository, diffCommand, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffUnified() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub(null, null) {
      private boolean firstTime = true;

      public long getFile(String path, long revision, Map properties, OutputStream contents) {
        final String leftFileContents = "test left file contents" + BR;
        final String rightFileContents = "test right file contents" + BR;
        if (contents != null) {
          try {
            if (firstTime) {
              contents.write(leftFileContents.getBytes());
              firstTime = false;
            } else {
              contents.write(rightFileContents.getBytes());
            }
          } catch (IOException e) {
            throw new RuntimeException("FAILED!");
          }
        }
        return 0;
      }

      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final InstanceConfiguration configuration = new InstanceConfiguration();

    final DiffCommand diffCommand = new DiffCommand(new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"});

    final String s = service.diffUnified(repository, diffCommand, ENCODING, configuration);
    assertEquals("@@ -1 +1 @@" + BR + "-test left file contents" + BR + "+test right file contents", s.trim());
  }

  public void testDiffSideBySideBinaryFile() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub(null, null) {
      public long getFile(String path, long revision, Map properties, OutputStream contents) {
        properties.put(SVNProperty.MIME_TYPE, "application/octet-stream");
        return 0;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final InstanceConfiguration configuration = new InstanceConfiguration();

    final DiffCommand diffCommand = new DiffCommand(new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"});

    try {
      service.diffSideBySide(repository, diffCommand, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IllegalFileFormatException e) {
      // expected
    }
  }

  public void testDiffSideBySideIdenticalFiles1() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub(null, null) {
      public long getFile(String path, long revision, Map properties, OutputStream contents) {
        return 0;
      }

      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final InstanceConfiguration configuration = new InstanceConfiguration();

    final DiffCommand diffCommand = new DiffCommand(new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"});

    try {
      service.diffSideBySide(repository, diffCommand, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffSideBySideDirectories() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub(null, null) {
      public long getFile(String path, long revision, Map properties, OutputStream contents) {
        final String fileContents = "test file contents";
        if (contents != null) {
          try {
            contents.write(fileContents.getBytes());
          } catch (IOException e) {
            throw new RuntimeException("FAILED!");
          }
        }
        return 0;
      }

      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.DIR;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final InstanceConfiguration configuration = new InstanceConfiguration();

    final DiffCommand diffCommand = new DiffCommand(new String[]{
        "/bug/code/try1;;91",
        "/bug/code/try2;;90"});

    try {
      service.diffSideBySide(repository, diffCommand, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (DiffException e) {
      // expected
    }
  }

  public void testDiffSideBySideIdenticalFiles2() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub(null, null) {
      public long getFile(String path, long revision, Map properties, OutputStream contents) {
        final String fileContents = "test file contents";
        if (contents != null) {
          try {
            contents.write(fileContents.getBytes());
          } catch (IOException e) {
            throw new RuntimeException("FAILED!");
          }
        }
        return 0;
      }

      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final InstanceConfiguration configuration = new InstanceConfiguration();

    final DiffCommand diffCommand = new DiffCommand(new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"});

    try {
      service.diffSideBySide(repository, diffCommand, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffSideBySide() throws Exception {
    final TestSVNRepositoryStub repository = new TestSVNRepositoryStub(null, null);

    final RepositoryService service = new RepositoryServiceImpl();
    final InstanceConfiguration configuration = new InstanceConfiguration();

    final DiffCommand diffCommand = new DiffCommand(new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"});

    List<SideBySideDiffRow> diff = service.diffSideBySide(repository, diffCommand, ENCODING, configuration);
    assertEquals(1, diff.size());

    repository.leftFileContents = "/**\n" +
        " * $Author$\n" +
        " * $Revision$\n" +
        " * $Date:$\n" +
        " */\n" +
        "Test1\n" +
        "Another test!\n" +
        "More!\n" +
        "Even more!\n";

    repository.rightFileContents = "/**\n" +
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
        "u<span class=\"sventonLineNo\">    1:&nbsp;</span>/**\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "u<span class=\"sventonLineNo\">    2:&nbsp;</span>&nbsp;* $Author$\n" +
            "c<span class=\"sventonLineNo\">    3:&nbsp;</span>&nbsp;* $Revision$\n" +
            "c<span class=\"sventonLineNo\">    4:&nbsp;</span>&nbsp;* $Date:$\n" +
            "c\n" +
            "u<span class=\"sventonLineNo\">    5:&nbsp;</span>&nbsp;*/\n" +
            "u<span class=\"sventonLineNo\">    6:&nbsp;</span>Test1\n" +
            "u<span class=\"sventonLineNo\">    7:&nbsp;</span>Another test!\n" +
            "u<span class=\"sventonLineNo\">    8:&nbsp;</span>More!\n" +
            "u<span class=\"sventonLineNo\">    9:&nbsp;</span>Even more!\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n";

    String rightResult =
        "u<span class=\"sventonLineNo\">    1:&nbsp;</span>/**\n" +
            "a<span class=\"sventonLineNo\">    2:&nbsp;</span>&nbsp;* $Id$\n" +
            "a<span class=\"sventonLineNo\">    3:&nbsp;</span>&nbsp;* $LastChangedDate$\n" +
            "a<span class=\"sventonLineNo\">    4:&nbsp;</span>&nbsp;* $Date$\n" +
            "a<span class=\"sventonLineNo\">    5:&nbsp;</span>&nbsp;* $LastChangedRevision$\n" +
            "a<span class=\"sventonLineNo\">    6:&nbsp;</span>&nbsp;* $Revision$\n" +
            "a<span class=\"sventonLineNo\">    7:&nbsp;</span>&nbsp;* $Rev$\n" +
            "a<span class=\"sventonLineNo\">    8:&nbsp;</span>&nbsp;* $LastChangedBy$\n" +
            "u<span class=\"sventonLineNo\">    9:&nbsp;</span>&nbsp;* $Author$\n" +
            "c<span class=\"sventonLineNo\">   10:&nbsp;</span>&nbsp;* $HeadURL$\n" +
            "c<span class=\"sventonLineNo\">   11:&nbsp;</span>&nbsp;* $URL$\n" +
            "c<span class=\"sventonLineNo\">   12:&nbsp;</span>&nbsp;* $Id$\n" +
            "u<span class=\"sventonLineNo\">   13:&nbsp;</span>&nbsp;*/\n" +
            "u<span class=\"sventonLineNo\">   14:&nbsp;</span>Test1\n" +
            "u<span class=\"sventonLineNo\">   15:&nbsp;</span>Another test!\n" +
            "u<span class=\"sventonLineNo\">   16:&nbsp;</span>More!\n" +
            "u<span class=\"sventonLineNo\">   17:&nbsp;</span>Even more!\n" +
            "a<span class=\"sventonLineNo\">   18:&nbsp;</span>\n" +
            "a<span class=\"sventonLineNo\">   19:&nbsp;</span>public String getRev {\n" +
            "a<span class=\"sventonLineNo\">   20:&nbsp;</span>&nbsp;return &quot;$Rev$&quot;;\n" +
            "a<span class=\"sventonLineNo\">   21:&nbsp;</span>\n" +
            "a<span class=\"sventonLineNo\">   22:&nbsp;</span>}\n";

    diff = service.diffSideBySide(repository, diffCommand, ENCODING, configuration);

    StringBuilder sb = new StringBuilder();
    for (final SideBySideDiffRow row : diff) {
      final SourceLine line = row.getSide(SideBySideDiffRow.Side.LEFT);
      sb.append(line.getAction().getCode());
      sb.append(line.getLine());
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuilder();
    for (final SideBySideDiffRow row : diff) {
      final SourceLine line = row.getSide(SideBySideDiffRow.Side.RIGHT);
      sb.append(line.getAction().getCode());
      sb.append(line.getLine());
      sb.append("\n");
    }
    assertEquals(rightResult, sb.toString());

    repository.leftFileContents =
        "[.ShellClassInfo]\n" +
            "InfoTip=@Shell32.dll,-12690\n" +
            "IconFile=%SystemRoot%\\system32\\SHELL32.dll\n" +
            "IconIndex=-238\n" +
            "[DeleteOnCopy]\n" +
            "Owner=Jesper\n" +
            "Personalized=14\n" +
            "PersonalizedName=Mina videoklipp\n";

    repository.rightFileContents =
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

    leftResult =
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

    rightResult =
        "u<span class=\"sventonLineNo\">    1:&nbsp;</span>[.ShellClassInfo]\n" +
            "c<span class=\"sventonLineNo\">    2:&nbsp;</span>IconIndex=-2388\n" +
            "c\n" +
            "c\n" +
            "u<span class=\"sventonLineNo\">    3:&nbsp;</span>[DeleteOnCopy]\n" +
            "u<span class=\"sventonLineNo\">    4:&nbsp;</span>Owner=Jesper\n" +
            "a<span class=\"sventonLineNo\">    5:&nbsp;</span>Owner=Patrik&amp;Jesper\n" +
            "u<span class=\"sventonLineNo\">    6:&nbsp;</span>Personalized=14\n" +
            "u<span class=\"sventonLineNo\">    7:&nbsp;</span>PersonalizedName=Mina videoklipp\n" +
            "a<span class=\"sventonLineNo\">    8:&nbsp;</span>OneMore=true\n" +
            "a<span class=\"sventonLineNo\">    9:&nbsp;</span>OneMore=4\n" +
            "a<span class=\"sventonLineNo\">   10:&nbsp;</span>OneMore=5\n" +
            "a<span class=\"sventonLineNo\">   11:&nbsp;</span>OneMore=6\n" +
            "a<span class=\"sventonLineNo\">   12:&nbsp;</span>OneMore=9\n";

    diff = service.diffSideBySide(repository, diffCommand, ENCODING, configuration);

    sb = new StringBuilder();
    for (final SideBySideDiffRow row : diff) {
      final SourceLine line = row.getSide(SideBySideDiffRow.Side.LEFT);
      sb.append(line.getAction().getCode());
      sb.append(line.getLine());
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuilder();
    for (final SideBySideDiffRow row : diff) {
      final SourceLine line = row.getSide(SideBySideDiffRow.Side.RIGHT);
      sb.append(line.getAction().getCode());
      sb.append(line.getLine());
      sb.append("\n");
    }
    assertEquals(rightResult, sb.toString());

  }


  public static class TestSVNRepositoryStub extends SVNRepositoryStub {

    private boolean first = true;
    public String leftFileContents = "test left file contents" + BR;
    public String rightFileContents = "test right file contents" + BR;

    public TestSVNRepositoryStub(SVNURL location, ISVNSession options) {
      super(location, options);
    }

    public long getFile(String path, long revision, Map properties, OutputStream contents) {
      if (contents != null) {
        try {
          if (first) {
            contents.write(leftFileContents.getBytes());
            first = false;
          } else {
            contents.write(rightFileContents.getBytes());
            first = true;
          }
        } catch (IOException e) {
          throw new RuntimeException("FAILED!");
        }
      }
      return 0;
    }

    public SVNNodeKind checkPath(String path, long revision) throws SVNException {
      return SVNNodeKind.FILE;
    }

  }

}

package org.sventon.service;

import junit.framework.TestCase;
import org.sventon.SVNRepositoryStub;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.diff.DiffException;
import org.sventon.diff.IdenticalFilesException;
import org.sventon.diff.IllegalFileFormatException;
import org.sventon.model.InlineDiffRow;
import org.sventon.model.SideBySideDiffRow;
import org.sventon.model.SourceLine;
import org.sventon.util.SVNFileRevisionEditor;
import org.sventon.util.WebUtils;
import org.sventon.web.command.DiffCommand;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class RepositoryServiceImplTest extends TestCase {

  private static final String ENCODING = "UTF-8";

  private static final String BR = System.getProperty("line.separator");

  private SVNFileRevisionEditor editor = new SVNFileRevisionEditor();

  public void testDiffUnifiedBinaryFile() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub() {

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
        properties.put(SVNProperty.MIME_TYPE, WebUtils.APPLICATION_OCTET_STREAM);
        return 0;
      }

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.diffUnified(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IllegalFileFormatException e) {
      // expected
    }
  }

  public void testDiffUnifiedIdenticalFiles1() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub() {

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
        return 0;
      }

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.diffUnified(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffUnifiedIdenticalFiles2() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub() {

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
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

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.diffUnified(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffUnified() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub() {
      private boolean firstTime = true;

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
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

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    final String s = service.diffUnified(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);
    assertEquals("@@ -1 +1 @@" + BR + "-test left file contents" + BR + "+test right file contents", s.trim());
  }

  public void testDiffInline() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub() {
      private boolean firstTime = true;

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
        final String leftFileContents =
            "row one" + BR +
                "row two" + BR +
                "test left file contents" + BR +
                "last row" + BR;

        final String rightFileContents =
            "row one" + BR +
                "test right file contents" + BR +
                "last row" + BR;

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

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    final List<InlineDiffRow> list = service.diffInline(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);

    assertEquals("InlineDiffRow[line=row one,rowNumberLeft=1,rowNumberRight=1,action=UNCHANGED]", list.get(0).toString());
    assertEquals("InlineDiffRow[line=row two,rowNumberLeft=2,rowNumberRight=<null>,action=DELETED]", list.get(1).toString());
    assertEquals("InlineDiffRow[line=test left file contents,rowNumberLeft=3,rowNumberRight=<null>,action=DELETED]", list.get(2).toString());
    assertEquals("InlineDiffRow[line=test right file contents,rowNumberLeft=<null>,rowNumberRight=2,action=ADDED]", list.get(3).toString());
    assertEquals("InlineDiffRow[line=last row,rowNumberLeft=4,rowNumberRight=3,action=UNCHANGED]", list.get(4).toString());
    assertEquals(5, list.size());
  }

  public void testDiffSideBySideBinaryFile() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub() {

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
        properties.put(SVNProperty.MIME_TYPE, WebUtils.APPLICATION_OCTET_STREAM);
        return 0;
      }

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.diffSideBySide(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IllegalFileFormatException e) {
      // expected
    }
  }

  public void testDiffSideBySideIdenticalFiles1() throws Exception {

    final SVNRepositoryStub repository = new SVNRepositoryStub() {

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
        return 0;
      }

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.diffSideBySide(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffSideBySideDirectories() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub() {

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
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

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.DIR;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try1@91",
        "/bug/code/try2@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.diffSideBySide(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (DiffException e) {
      // expected
    }
  }

  public void testDiffSideBySideIdenticalFiles2() throws Exception {
    final SVNRepositoryStub repository = new SVNRepositoryStub() {

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
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

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    };

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.diffSideBySide(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);
      fail("Binary files cannot be diffed");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffSideBySide() throws Exception {
    final TestSVNRepositoryStub repository = new TestSVNRepositoryStub();

    final RepositoryService service = new RepositoryServiceImpl();
    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    List<SideBySideDiffRow> diff = service.diffSideBySide(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);
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
        "1u/**\n" +
            "2c&nbsp;* $Author$\n" +
            "c\n" +
            "c\n" +
            "c\n" +
            "3u&nbsp;* $Revision$\n" +
            "4c&nbsp;* $Date:$\n" +
            "c\n" +
            "c\n" +
            "c\n" +
            "c\n" +
            "c\n" +
            "5u&nbsp;*/\n" +
            "6uTest1\n" +
            "7uAnother test!\n" +
            "8uMore!\n" +
            "9uEven more!\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n";

    String rightResult =
        "1u/**\n" +
            "2c&nbsp;* $Id$\n" +
            "3c&nbsp;* $LastChangedDate$\n" +
            "4c&nbsp;* $Date$\n" +
            "5c&nbsp;* $LastChangedRevision$\n" +
            "6u&nbsp;* $Revision$\n" +
            "7c&nbsp;* $Rev$\n" +
            "8c&nbsp;* $LastChangedBy$\n" +
            "9c&nbsp;* $Author$\n" +
            "10c&nbsp;* $HeadURL$\n" +
            "11c&nbsp;* $URL$\n" +
            "12c&nbsp;* $Id$\n" +
            "13u&nbsp;*/\n" +
            "14uTest1\n" +
            "15uAnother test!\n" +
            "16uMore!\n" +
            "17uEven more!\n" +
            "18a\n" +
            "19apublic String getRev {\n" +
            "20a&nbsp;return &quot;$Rev$&quot;;\n" +
            "21a\n" +
            "22a}\n";

    diff = service.diffSideBySide(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);

    StringBuilder sb = new StringBuilder();
    for (final SideBySideDiffRow row : diff) {
      final SourceLine line = row.getLeft();
      sb.append(line.getRowNumberAsString());
      sb.append(line.getAction().getCode());
      sb.append(line.getLine());
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuilder();
    for (final SideBySideDiffRow row : diff) {
      final SourceLine line = row.getRight();
      sb.append(line.getRowNumberAsString());
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
        "1u[.ShellClassInfo]\n" +
            "2cInfoTip=@Shell32.dll,-12690\n" +
            "3cIconFile=%SystemRoot%\\system32\\SHELL32.dll\n" +
            "4cIconIndex=-238\n" +
            "5u[DeleteOnCopy]\n" +
            "6uOwner=Jesper\n" +
            "a\n" +
            "7uPersonalized=14\n" +
            "8uPersonalizedName=Mina videoklipp\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n" +
            "a\n";

    rightResult =
        "1u[.ShellClassInfo]\n" +
            "2cIconIndex=-2388\n" +
            "c\n" +
            "c\n" +
            "3u[DeleteOnCopy]\n" +
            "4uOwner=Jesper\n" +
            "5aOwner=Patrik&amp;Jesper\n" +
            "6uPersonalized=14\n" +
            "7uPersonalizedName=Mina videoklipp\n" +
            "8aOneMore=true\n" +
            "9aOneMore=4\n" +
            "10aOneMore=5\n" +
            "11aOneMore=6\n" +
            "12aOneMore=9\n";

    diff = service.diffSideBySide(repository, command, SVNRevision.UNDEFINED, ENCODING, configuration);

    sb = new StringBuilder();
    for (final SideBySideDiffRow row : diff) {
      final SourceLine line = row.getLeft();
      sb.append(line.getRowNumberAsString());
      sb.append(line.getAction().getCode());
      sb.append(line.getLine());
      sb.append("\n");
    }
    assertEquals(leftResult, sb.toString());

    sb = new StringBuilder();
    for (final SideBySideDiffRow row : diff) {
      final SourceLine line = row.getRight();
      sb.append(line.getRowNumberAsString());
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

    @Override
    public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
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

    @Override
    public SVNNodeKind checkPath(String path, long revision) throws SVNException {
      return SVNNodeKind.FILE;
    }

  }

}

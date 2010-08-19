package org.sventon.service.svnkit;

import junit.framework.TestCase;
import org.sventon.SVNConnection;
import org.sventon.SVNRepositoryStub;
import org.sventon.diff.IdenticalFilesException;
import org.sventon.diff.IllegalFileFormatException;
import org.sventon.model.*;
import org.sventon.service.RepositoryService;
import org.sventon.util.WebUtils;
import org.sventon.web.command.BaseCommand;
import org.sventon.web.command.DiffCommand;
import org.sventon.web.command.editor.PathRevisionEditor;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class SVNKitRepositoryServiceTest extends TestCase {

  private static final String ENCODING = "UTF-8";

  private static final String NL = System.getProperty("line.separator");

  private PathRevisionEditor editor = new PathRevisionEditor();

  public void testDiffUnifiedBinaryFile() throws Exception {
    final SVNConnection connection = new SVNKitConnection(new SVNRepositoryStub() {

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
        properties.put(SVNProperty.MIME_TYPE, WebUtils.APPLICATION_OCTET_STREAM);
        return 0;
      }

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    });

    final RepositoryService service = new SVNKitRepositoryService();

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.diffUnified(connection, command, Revision.UNDEFINED, ENCODING);
      fail("Binary files cannot be diffed");
    } catch (IllegalFileFormatException e) {
      // expected
    }
  }

  public void testDiffUnifiedIdenticalEmptyFiles() throws Exception {
    final SVNKitRepositoryService service = new SVNKitRepositoryService();

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.createUnifiedDiff(command, ENCODING, new TextFile(""), new TextFile(""));
      fail("Exception expected");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffUnifiedIdenticalFiles() throws Exception {
    final SVNKitRepositoryService service = new SVNKitRepositoryService();

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      final String contents = "test file contents";
      service.createUnifiedDiff(command, ENCODING, new TextFile(contents), new TextFile(contents));
      fail("No result should be produced for identical files");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffUnified() throws Exception {

    final TextFile leftFile = new TextFile("test left file contents" + NL);
    final TextFile rightFile = new TextFile("test right file contents" + NL);

    final SVNKitRepositoryService service = new SVNKitRepositoryService();

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    final String s = service.createUnifiedDiff(command, ENCODING, leftFile, rightFile);
    assertEquals("@@ -1 +1 @@" + NL + "-test left file contents" + NL + "+test right file contents", s.trim());
  }

  public void testDiffInline() throws Exception {
    final TextFile leftFile = new TextFile(
        "row one" + NL +
            "row two" + NL +
            "test left file contents" + NL +
            "last row" + NL);

    final TextFile rightFile = new TextFile(
        "row one" + NL +
            "test right file contents" + NL +
            "last row" + NL);

    final SVNKitRepositoryService service = new SVNKitRepositoryService();

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    final List<InlineDiffRow> list = service.createInlineDiff(command, ENCODING, leftFile, rightFile);

    assertEquals("InlineDiffRow[line=row one,rowNumberLeft=1,rowNumberRight=1,action=UNCHANGED]", list.get(0).toString());
    assertEquals("InlineDiffRow[line=row two,rowNumberLeft=2,rowNumberRight=<null>,action=DELETED]", list.get(1).toString());
    assertEquals("InlineDiffRow[line=test left file contents,rowNumberLeft=3,rowNumberRight=<null>,action=DELETED]", list.get(2).toString());
    assertEquals("InlineDiffRow[line=test right file contents,rowNumberLeft=<null>,rowNumberRight=2,action=ADDED]", list.get(3).toString());
    assertEquals("InlineDiffRow[line=last row,rowNumberLeft=4,rowNumberRight=3,action=UNCHANGED]", list.get(4).toString());
    assertEquals(5, list.size());
  }

  public void testDiffSideBySideBinaryFile() throws Exception {
    final SVNConnection connection = new SVNKitConnection(new SVNRepositoryStub() {

      @Override
      public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
        properties.put(SVNProperty.MIME_TYPE, WebUtils.APPLICATION_OCTET_STREAM);
        return 0;
      }

      @Override
      public SVNNodeKind checkPath(String path, long revision) throws SVNException {
        return SVNNodeKind.FILE;
      }
    });

    final RepositoryService service = new SVNKitRepositoryService();

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.diffSideBySide(connection, command, Revision.UNDEFINED, ENCODING);
      fail("Binary files cannot be diffed");
    } catch (IllegalFileFormatException e) {
      // expected
    }
  }

  public void testDiffSideBySideIdenticalEmptyFiles() throws Exception {
    final SVNKitRepositoryService service = new SVNKitRepositoryService();

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.createSideBySideDiff(command, ENCODING, new TextFile(""), new TextFile(""));
      fail("Expected exception!");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffSideBySideIdenticalFiles() throws Exception {
    final SVNKitRepositoryService service = new SVNKitRepositoryService();

    final TextFile leftFile = new TextFile("test file contents");
    final TextFile rightFile = new TextFile("test file contents");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    try {
      service.createSideBySideDiff(command, ENCODING, leftFile, rightFile);
      fail("Expected exception");
    } catch (IdenticalFilesException e) {
      // expected
    }
  }

  public void testDiffSideBySide() throws Exception {
    final SVNKitRepositoryService service = new SVNKitRepositoryService();

    final TextFile leftFile = new TextFile("left file");
    final TextFile rightFile = new TextFile("right file");

    final String[] revisions = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};
    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(revisions));

    List<SideBySideDiffRow> diff = service.createSideBySideDiff(command, ENCODING, leftFile, rightFile);
    assertEquals(1, diff.size());

    final TextFile leftFile2 = new TextFile("/**\n" +
        " * $Author$\n" +
        " * $Revision$\n" +
        " * $Date:$\n" +
        " */\n" +
        "Test1\n" +
        "Another test!\n" +
        "More!\n" +
        "Even more!\n");

    final TextFile rightFile2 = new TextFile("/**\n" +
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
        "}\n");

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

    diff = service.createSideBySideDiff(command, ENCODING, leftFile2, rightFile2);

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

    final TextFile leftFile3 = new TextFile(
        "[.ShellClassInfo]\n" +
            "InfoTip=@Shell32.dll,-12690\n" +
            "IconFile=%SystemRoot%\\system32\\SHELL32.dll\n" +
            "IconIndex=-238\n" +
            "[DeleteOnCopy]\n" +
            "Owner=Jesper\n" +
            "Personalized=14\n" +
            "PersonalizedName=Mina videoklipp\n");

    final TextFile rightFile3 = new TextFile(
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
            "OneMore=9\n");

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

    diff = service.createSideBySideDiff(command, ENCODING, leftFile3, rightFile3);

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

  public void testTranslateRevision() throws Exception {
    final BaseCommand command = new BaseCommand();
    final SVNKitRepositoryService service = new SVNKitRepositoryService();

    command.setRevision(Revision.parse("head"));
    assertEquals(100, service.translateRevision(command.getRevision(), 100, null).longValue());
    assertEquals(Revision.HEAD, command.getRevision());

    command.setRevision(Revision.parse(""));
    assertEquals(100, service.translateRevision(command.getRevision(), 100, null).longValue());
    assertEquals(Revision.UNDEFINED, command.getRevision());

    command.setRevision(Revision.parse("123"));
    service.translateRevision(command.getRevision(), 200, null);
    assertEquals(Revision.create(123), command.getRevision());

    final SVNRepositoryStub repositoryStub = new SVNRepositoryStub() {
      @Override
      public long getDatedRevision(Date date) throws SVNException {
        return 123;
      }
    };
    command.setRevision(Revision.parse("{2007-01-01}"));
    final SVNKitConnection connection = new SVNKitConnection(repositoryStub);
    assertEquals(123, service.translateRevision(command.getRevision(), 200, connection).longValue());
  }

}
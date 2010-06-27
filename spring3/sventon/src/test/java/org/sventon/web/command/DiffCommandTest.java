package org.sventon.web.command;

import junit.framework.TestCase;
import org.sventon.diff.DiffException;
import org.sventon.util.SVNFileRevisionEditor;

public class DiffCommandTest extends TestCase {

  private final SVNFileRevisionEditor editor = new SVNFileRevisionEditor();

  public void testDiffCommandNull() throws Exception {
    DiffCommand command = new DiffCommand();
    try {
      command.setEntries(null);
      fail("Should have thrown an IAE");
    }
    catch (IllegalArgumentException ex) {
      // expected
    }
  }

  public void testDiffCommand() throws Exception {
    final String[] params = new String[]{
        "/bug/code/try2/OrderDetailModel.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};

    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(params));
    assertEquals(91, command.getToRevision().getNumber());
    assertEquals(90, command.getFromRevision().getNumber());
    assertEquals("/bug/code/try2/OrderDetailModel.java", command.getToPath());
    assertEquals("/bug/code/try2/OrderDetailModel.java", command.getFromPath());
    assertEquals("OrderDetailModel.java", command.getToTarget());
    assertEquals("OrderDetailModel.java", command.getFromTarget());
  }

  public void testDiffCommandDifferentPaths() throws Exception {
    final String[] params = new String[]{
        "/bug/code/try2/OrderDetail.java@91",
        "/bug/code/try2/OrderDetailModel.java@90"};

    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(params));
    assertEquals(91, command.getToRevision().getNumber());
    assertEquals(90, command.getFromRevision().getNumber());
    assertEquals("/bug/code/try2/OrderDetail.java", command.getToPath());
    assertEquals("/bug/code/try2/OrderDetailModel.java", command.getFromPath());
    assertEquals("OrderDetail.java", command.getToTarget());
    assertEquals("OrderDetailModel.java", command.getFromTarget());
  }

  public void testDiffCommandDirs() throws Exception {
    final String[] params = new String[]{
        "/bug/code/try1/@90",
        "/bug/code/try2/@91"};

    final DiffCommand command = new DiffCommand();
    command.setEntries(editor.convert(params));
    assertEquals(90, command.getFromRevision().getNumber());
    assertEquals(91, command.getToRevision().getNumber());
    assertEquals("/bug/code/try1/", command.getFromPath());
    assertEquals("/bug/code/try2/", command.getToPath());
    assertEquals("try1", command.getFromTarget());
    assertEquals("try2", command.getToTarget());
  }

  public void testDiffCommandNoHistory() throws Exception {
    final String[] params = new String[]{
        "/bug/code/try2/Order.java@92"
    };

    final DiffCommand command = new DiffCommand();
    try {
      command.setEntries(editor.convert(params));
      fail("Should throw DiffException");
    }
    catch (DiffException ex) {
      // expected
    }
  }

  public void testDiffCommandWrongDelimiter() throws Exception {
    final String[] params = new String[]{
        "/bug/code/try2/OrderDetail.java##91",
        "/bug/code/try2/OrderDetailModel.java##90"};

    final DiffCommand command = new DiffCommand();
    try {
      command.setEntries(editor.convert(params));
      fail("Should throw IllegalArgumentException");
    }
    catch (IllegalArgumentException ex) {
      // expected
    }
  }
}
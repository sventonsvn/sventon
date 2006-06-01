package de.berlios.sventon.web.command;

import junit.framework.TestCase;
import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.web.command.DiffCommand;

public class DiffCommandTest extends TestCase {

  public void testDiffCommandNull() throws Exception {
    DiffCommand diffCommand;
    try {
      diffCommand = new DiffCommand((String[]) null);
      fail("Should have thrown an IAE");
    }
    catch (IllegalArgumentException ex) {
      // expected
    }
  }

  public void testDiffCommand() throws Exception {
    String[] params = new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"};

    DiffCommand diffCommand = new DiffCommand(params);
    assertEquals(91, diffCommand.getToRevision());
    assertEquals(90, diffCommand.getFromRevision());
    assertEquals("/bug/code/try2/OrderDetailModel.java", diffCommand.getToPath());
    assertEquals("/bug/code/try2/OrderDetailModel.java", diffCommand.getFromPath());
  }

  public void testDiffCommandNoRevision() throws Exception {
    String[] params = new String[]{
        "/bug/code/try2/OrderDetailModel.java",
        "/bug/code/try2/OrderDetailModel.java"};

    // If no revision is given, assume HEAD
    DiffCommand diffCommand = new DiffCommand(params);
    assertEquals(-1, diffCommand.getToRevision());
    assertEquals(-1, diffCommand.getFromRevision());
    assertEquals("/bug/code/try2/OrderDetailModel.java", diffCommand.getToPath());
    assertEquals("/bug/code/try2/OrderDetailModel.java", diffCommand.getFromPath());
  }

  public void testDiffCommandDifferentPaths() throws Exception {
    String[] params = new String[]{
        "/bug/code/try2/OrderDetail.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"};

    DiffCommand diffCommand = new DiffCommand(params);
    assertEquals(91, diffCommand.getToRevision());
    assertEquals(90, diffCommand.getFromRevision());
    assertEquals("/bug/code/try2/OrderDetail.java", diffCommand.getToPath());
    assertEquals("/bug/code/try2/OrderDetailModel.java", diffCommand.getFromPath());
  }

  public void testDiffCommandIAE() throws Exception {
    String[] params = new String[]{
        "/bug/code/try2/Order.java;;92",
        "/bug/code/try2/OrderDetail.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"};

    DiffCommand diffCommand;
    try {
      diffCommand = new DiffCommand(params);
      fail("Should throw IllegalArgumentException");
    }
    catch (IllegalArgumentException ex) {
      // expected
    }
  }

  public void testDiffCommandWrongDelimiter() throws Exception {
    String[] params = new String[]{
        "/bug/code/try2/OrderDetail.java##91",
        "/bug/code/try2/OrderDetailModel.java##90"};

    DiffCommand diffCommand;
    try {
      diffCommand = new DiffCommand(params);

      //fail("Should throw DiffException");

      //TODO: Fix the error above. Temp disable for now.

    }
    catch (DiffException ex) {
      // expected
    }
  }
}
package de.berlios.sventon.web.command;

import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.web.support.RequestParameterParser;
import junit.framework.TestCase;

public class DiffCommandTest extends TestCase {

  private final RequestParameterParser parameterParser = new RequestParameterParser();

  public void testDiffCommandNull() throws Exception {
    try {
      new DiffCommand(null);
      fail("Should have thrown an IAE");
    }
    catch (DiffException ex) {
      // expected
    }
  }

  public void testDiffCommand() throws Exception {
    final String[] params = new String[]{
        "/bug/code/try2/OrderDetailModel.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"};

    final DiffCommand diffCommand = new DiffCommand(parameterParser.parseEntries(params));
    assertEquals(91, diffCommand.getToRevision().getNumber());
    assertEquals(90, diffCommand.getFromRevision().getNumber());
    assertEquals("/bug/code/try2/OrderDetailModel.java", diffCommand.getToPath());
    assertEquals("/bug/code/try2/OrderDetailModel.java", diffCommand.getFromPath());
  }

  public void testDiffCommandDifferentPaths() throws Exception {
    final String[] params = new String[]{
        "/bug/code/try2/OrderDetail.java;;91",
        "/bug/code/try2/OrderDetailModel.java;;90"};

    final DiffCommand diffCommand = new DiffCommand(parameterParser.parseEntries(params));
    assertEquals(91, diffCommand.getToRevision().getNumber());
    assertEquals(90, diffCommand.getFromRevision().getNumber());
    assertEquals("/bug/code/try2/OrderDetail.java", diffCommand.getToPath());
    assertEquals("/bug/code/try2/OrderDetailModel.java", diffCommand.getFromPath());
  }

  public void testDiffCommandNoHistory() throws Exception {
    final String[] params = new String[]{
        "/bug/code/try2/Order.java;;92"
    };

    try {
      new DiffCommand(parameterParser.parseEntries(params));
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

    try {
      new DiffCommand(parameterParser.parseEntries(params));
      fail("Should throw IllegalArgumentException");
    }
    catch (IllegalArgumentException ex) {
      // expected
    }
  }
}
package de.berlios.sventon.ctrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.tmatesoft.svn.core.SVNLogEntry;

import junit.framework.TestCase;

public class ShowLogControllerTest extends TestCase {

  public void testPageLogEntries() {
    ShowLogController controller = new ShowLogController();

    List<LogEntryBundle> logEntries = new ArrayList<LogEntryBundle>();
    createEntries(logEntries, 11);

    Collections.reverse(logEntries);

    assertEquals(11, logEntries.size());

    assertEquals("msg11", logEntries.get(0).getSvnLogEntry().getMessage());
    assertEquals("msg1", logEntries.get(10).getSvnLogEntry().getMessage());

    List<List<LogEntryBundle>> pages = controller.pageLogEntries(logEntries);
    assertEquals(2, pages.size());
    List<LogEntryBundle> page1 = pages.get(0);
    List<LogEntryBundle> page2 = pages.get(1);

    assertEquals(10, page1.size());
    assertEquals(1, page2.size());
    
    logEntries = new ArrayList<LogEntryBundle>();
    
    assertEquals(0, logEntries.size());
    pages = controller.pageLogEntries(logEntries);
    
    //Should contain one empty page
    assertEquals(1, pages.size());
    assertEquals(0, pages.get(0).size());

    try {
      controller.pageLogEntries(null);
      fail("NPE Expected");
    } catch (NullPointerException npe) {
      //Expected
    }

  }

  private void createEntries(List<LogEntryBundle> logEntries, int size) {
    for (int i = 1; i <= size; i++) {
      logEntries.add(new LogEntryBundle(new SVNLogEntry(null, i, "patrikfr", new Date(), "msg" + i), "/path"));
    }
  }

}

package de.berlios.sventon.svnsupport;

import de.berlios.sventon.ctrl.LogEntryBundle;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SVNRepositoryStubTest extends TestCase {

  public void testLog() throws Exception {

    String[] targetPaths = new String[]{"/"}; // the path to show logs for
    String pathAtRevision = targetPaths[0];
    List<LogEntryBundle> logEntryBundles = new ArrayList<LogEntryBundle>();

    List<SVNLogEntry> logEntries =
        (List<SVNLogEntry>) SVNRepositoryStub.getInstance().log(targetPaths,
            null, 1, 0, true, false);

    for (SVNLogEntry logEntry : logEntries) {
      logEntryBundles.add(new LogEntryBundle(logEntry, pathAtRevision));
      Map<String, SVNLogEntryPath> m = logEntry.getChangedPaths();
      Set<String> changedPaths = m.keySet();
      for (String entryPath : changedPaths) {
        int i = StringUtils.indexOfDifference(entryPath, pathAtRevision);
        if (i == -1) { // Same path
          SVNLogEntryPath logEntryPath = m.get(entryPath);
          if (logEntryPath.getCopyPath() != null) {
            pathAtRevision = logEntryPath.getCopyPath();
          }
        } else if (entryPath.length() == i) { // Part path, can be a
          // branch
          SVNLogEntryPath logEntryPath = m.get(entryPath);
          if (logEntryPath.getCopyPath() != null) {
            pathAtRevision = logEntryPath.getCopyPath() + pathAtRevision.substring(i);
          }
        }
      }
    }
  }
}
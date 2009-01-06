package org.sventon.util;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletResponse;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HTMLCreatorTest extends TestCase {

  public void testCreateChangedPathsTable() throws Exception {
    final String result = "<table class=\"changedPathsTable\">\n" +
        "  <tr>\n" +
        "    <th align=\"left\">Action</th>\n" +
        "    <th align=\"left\">Path</th>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Modified</i></td>\n" +
        "    <td><a href=\"repos/sandbox/diff/file1.java?revision=1&entries=/file1.java@1&entries=/file1.java@0\" title=\"Diff with previous version\"><i>/file1.java</i></a></td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Deleted</i></td>\n" +
        "    <td><a href=\"repos/sandbox/goto/file2.html?revision=0\" title=\"Show previous revision\"><del>/file2.html</del></a></td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Added</i></td>\n" +
        "    <td><a href=\"repos/sandbox/goto/file3.abc?revision=1\" title=\"Show\">/file3.abc</a><br><b>Copy from</b> <a href=\"repos/sandbox/goto/branch/file3.abc?revision=1\" title=\"Show\">/branch/file3.abc</a> @ <a href=\"repos/sandbox/revinfo?revision=1\">1</a></td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Replaced</i></td>\n" +
        "    <td><a href=\"repos/sandbox/goto/file4.def?revision=1\" title=\"Show\">/file4.def</a></td>\n" +
        "  </tr>\n" +
        "</table>";

    final Map<String, SVNLogEntryPath> changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    changedPaths.put("/file2.html", new SVNLogEntryPath("/file2.html", 'D', null, 1));
    changedPaths.put("/file3.abc", new SVNLogEntryPath("/file3.abc", 'A', "/branch/file3.abc", 1));
    changedPaths.put("/file4.def", new SVNLogEntryPath("/file4.def", 'R', null, 1));

    final SVNLogEntry logEntry = new SVNLogEntry(changedPaths, 1, "jesper", new Date(), "Testing");

    assertEquals(result, HTMLCreator.createChangedPathsTable(logEntry.getChangedPaths(), logEntry.getRevision(),
        "/file1.java", "", new RepositoryName("sandbox"), false, false, new MockHttpServletResponse()));
  }

  public void testCreateGoToUrl() throws Exception {
    assertEquals("http://localhost/repos/sandbox/goto/trunk/a.txt?revision=1",
        HTMLCreator.createGoToUrl("http://localhost/", "/trunk/a.txt", 1, new RepositoryName("sandbox"), false));
    assertEquals("http://localhost/repos/sandbox/goto/trunk/a.txt?revision=HEAD",
        HTMLCreator.createGoToUrl("http://localhost/", "/trunk/a.txt", 1, new RepositoryName("sandbox"), true));
  }

  public void testCreateRevInfoUrl() throws Exception {
    assertEquals("http://localhost/repos/sandbox/revinfo?revision=1",
        HTMLCreator.createRevInfoUrl("http://localhost/", 1, new RepositoryName("sandbox")));
  }

  public void testCreateDiffUrl() throws Exception {
    assertEquals("http://localhost/repos/sandbox/diff/a.txt?revision=2&entries=/a.txt@2&entries=/a.txt@1",
        HTMLCreator.createDiffUrl("http://localhost/", "/a.txt", 2, new RepositoryName("sandbox"), false));
  }
}
package de.berlios.sventon.util;

import de.berlios.sventon.appl.RepositoryName;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletResponse;
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
        "    <td><a href=\"diff.svn?path=/file1.java&revision=1&name=sandbox&entry=/file1.java;;1&entry=/file1.java;;0\" title=\"Diff with previous version\"><i>/file1.java</i></a></td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Deleted</i></td>\n" +
        "    <td><a href=\"goto.svn?path=/file2.html&revision=0&name=sandbox\" title=\"Show previous revision\"><del>/file2.html</del></a></td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Added</i></td>\n" +
        "    <td><a href=\"goto.svn?path=/file3.abc&revision=1&name=sandbox\" title=\"Show\">/file3.abc</a><br><b>Copy from</b> <a href=\"goto.svn?path=/branch/file3.abc&revision=1&name=sandbox\" title=\"Show\">/branch/file3.abc</a> @ <a href=\"revinfo.svn?revision=1&name=sandbox\">1</a></td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Replaced</i></td>\n" +
        "    <td><a href=\"goto.svn?path=/file4.def&revision=1&name=sandbox\" title=\"Show\">/file4.def</a></td>\n" +
        "  </tr>\n" +
        "</table>";

    final Map<String, SVNLogEntryPath> changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    changedPaths.put("/file2.html", new SVNLogEntryPath("/file2.html", 'D', null, 1));
    changedPaths.put("/file3.abc", new SVNLogEntryPath("/file3.abc", 'A', "/branch/file3.abc", 1));
    changedPaths.put("/file4.def", new SVNLogEntryPath("/file4.def", 'R', null, 1));
    final SVNLogEntry logEntry = new SVNLogEntry(changedPaths, 1, "jesper", new Date(), "Testing");

    assertEquals(result, HTMLCreator.createChangedPathsTable(logEntry, "/file1.java", "", new RepositoryName("sandbox"), false, false, new MockHttpServletResponse()));
  }

  public void testCreateGoToUrl() throws Exception {
    assertEquals("http://localhost/goto.svn?path=/trunk/a.txt&revision=1&name=sandbox",
        HTMLCreator.createGoToUrl("http://localhost/", "/trunk/a.txt", 1, new RepositoryName("sandbox"), false));
    assertEquals("http://localhost/goto.svn?path=/trunk/a.txt&revision=HEAD&name=sandbox",
        HTMLCreator.createGoToUrl("http://localhost/", "/trunk/a.txt", 1, new RepositoryName("sandbox"), true));
  }

  public void testCreateRevInfoUrl() throws Exception {
    assertEquals("http://localhost/revinfo.svn?revision=1&name=sandbox",
        HTMLCreator.createRevInfoUrl("http://localhost/", 1, new RepositoryName("sandbox")));
  }

  public void testCreateDiffUrl() throws Exception {
    assertEquals("http://localhost/diff.svn?path=/a.txt&revision=2&name=sandbox&entry=/a.txt;;2&entry=/a.txt;;1",
        HTMLCreator.createDiffUrl("http://localhost/", "/a.txt", 2, new RepositoryName("sandbox"), false));
  }
}
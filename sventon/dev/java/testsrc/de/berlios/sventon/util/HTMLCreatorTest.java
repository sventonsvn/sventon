package de.berlios.sventon.util;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletResponse;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HTMLCreatorTest extends TestCase {

  public void testCreateChangedPathsTable() throws Exception {
    final String result = "<table border=\"0\">\n" +
        "  <tr>\n" +
        "    <th>Action</th>\n" +
        "    <th>Path</th>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Modified</i></td>\n" +
        "    <td><a href=\"diff.svn?path=%2Ffile1.java&revision=1&name=sandbox&entry=%2Ffile1.java%3B%3B1&entry=%2Ffile1.java%3B%3B0\" title=\"Diff with previous version\">/file1.java</a></td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Deleted</i></td>\n" +
        "    <td><strike>/file2.html</strike></td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Added</i></td>\n" +
        "    <td><a href=\"goto.svn?path=%2Ffile3.abc&revision=1&name=sandbox\" title=\"Show\">/file3.abc</a><br/><b>Copy from</b> <a href=\"goto.svn?path=%2Fbranch%2Ffile3.abc&revision=1&name=sandbox\" title=\"Show\">/branch/file3.abc</a> @ <a href=\"revinfo.svn?revision=1&name=sandbox\">1</a></td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Replaced</i></td>\n" +
        "    <td><a href=\"goto.svn?path=%2Ffile4.def&revision=1&name=sandbox\" title=\"Show\">/file4.def</a></td>\n" +
        "  </tr>\n" +
        "</table>";

    final Map<String, SVNLogEntryPath> changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    changedPaths.put("/file2.html", new SVNLogEntryPath("/file2.html", 'D', null, 1));
    changedPaths.put("/file3.abc", new SVNLogEntryPath("/file3.abc", 'A', "/branch/file3.abc", 1));
    changedPaths.put("/file4.def", new SVNLogEntryPath("/file4.def", 'R', null, 1));
    final SVNLogEntry logEntry = new SVNLogEntry(changedPaths, 1, "jesper", new Date(), "Testing");

    assertEquals(result, HTMLCreator.createChangedPathsTable(logEntry, "", "sandbox", false, false, new MockHttpServletResponse()));
  }

  public void testEncode() throws Exception {
    assertEquals("%2F", HTMLCreator.encode("/"));
    assertEquals("+", HTMLCreator.encode(" "));
  }

  public void testCreateGoToUrl() throws Exception {
    assertEquals("http://localhost/goto.svn?path=%2Ftrunk%2Fa.txt&revision=1&name=sandbox",
        HTMLCreator.createGoToUrl("http://localhost/", "/trunk/a.txt", 1, "sandbox", false));
    assertEquals("http://localhost/goto.svn?path=%2Ftrunk%2Fa.txt&revision=head&name=sandbox",
        HTMLCreator.createGoToUrl("http://localhost/", "/trunk/a.txt", 1, "sandbox", true));
  }

  public void testCreateRevInfoUrl() throws Exception {
    assertEquals("http://localhost/revinfo.svn?revision=1&name=sandbox",
        HTMLCreator.createRevInfoUrl("http://localhost/", 1, "sandbox"));
  }

  public void testCreateDiffUrl() throws Exception {
    assertEquals("http://localhost/diff.svn?path=%2Fa.txt&revision=2&name=sandbox&entry=%2Fa.txt%3B%3B2&entry=%2Fa.txt%3B%3B1",
        HTMLCreator.createDiffUrl("http://localhost/", "/a.txt", 2, "sandbox", false));
  }
}
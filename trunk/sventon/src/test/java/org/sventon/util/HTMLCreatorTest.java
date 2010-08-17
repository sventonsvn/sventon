package org.sventon.util;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletResponse;
import org.sventon.TestUtils;
import org.sventon.model.ChangeType;
import org.sventon.model.ChangedPath;
import org.sventon.model.LogEntry;
import org.sventon.model.RepositoryName;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

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
        "    <td><a href=\"repos/sandbox/goto/file3.abc?revision=1\" title=\"Show\">/file3.abc</a><br>(<i>Copy from</i> <a href=\"repos/sandbox/goto/branch/file3.abc?revision=1\" title=\"Show\">/branch/file3.abc</a> @ <a href=\"repos/sandbox/info?revision=1\">1</a>)</td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Replaced</i></td>\n" +
        "    <td><a href=\"repos/sandbox/goto/file4.def?revision=1\" title=\"Show\">/file4.def</a></td>\n" +
        "  </tr>\n" +
        "</table>";

    final Set<ChangedPath> changedPaths = new TreeSet<ChangedPath>();
    changedPaths.add(new ChangedPath("/file1.java", null, 1, ChangeType.MODIFIED));
    changedPaths.add(new ChangedPath("/file2.html", null, 1, ChangeType.DELETED));
    changedPaths.add(new ChangedPath("/file3.abc", "/branch/file3.abc", 1, ChangeType.ADDED));
    changedPaths.add(new ChangedPath("/file4.def", null, 1, ChangeType.REPLACED));

    final LogEntry logEntry = TestUtils.createLogEntry(1, "jesper", new Date(), "Testing", changedPaths);

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
    assertEquals("http://localhost/repos/sandbox/info?revision=1",
        HTMLCreator.createRevisionInfoUrl("http://localhost/", 1, new RepositoryName("sandbox")));
  }

  public void testCreateDiffUrl() throws Exception {
    assertEquals("http://localhost/repos/sandbox/diff/a.txt?revision=2&entries=/a.txt@2&entries=/a.txt@1",
        HTMLCreator.createDiffUrl("http://localhost/", "/a.txt", 2, new RepositoryName("sandbox"), false));
  }
}
package de.berlios.sventon.web.support;

import de.berlios.sventon.repository.RepositoryEntry;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;

import java.util.ArrayList;
import java.util.List;

public class FileExtensionFilterTest extends TestCase {

  public void testFilter() throws Exception {

    final List<RepositoryEntry> list = new ArrayList<RepositoryEntry>();
    SVNURL url = SVNURL.parseURIDecoded("http://localhost/");

    list.add(new RepositoryEntry(new SVNDirEntry(null, url, "test.abC", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, url, "test.jpg", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    list.add(new RepositoryEntry(new SVNDirEntry(null, url, "test.GIF", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));

    List<RepositoryEntry> filteredList;
    filteredList = new FileExtensionFilter("jpg").filter(list);
    assertEquals(1, filteredList.size());

    filteredList = new FileExtensionFilter("gif").filter(list);
    assertEquals(1, filteredList.size());

    try {
      new FileExtensionFilter(null).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new FileExtensionFilter("").filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

}

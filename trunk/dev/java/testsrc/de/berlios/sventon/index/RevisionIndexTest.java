package de.berlios.sventon.index;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import de.berlios.sventon.svnsupport.SVNRepositoryStub;

public class RevisionIndexTest extends TestCase {

  private SVNRepositoryStub repository = null;
  private RevisionIndex index = null;

  public void setUp() throws Exception {
    // Set up the repository stub
    repository = new SVNRepositoryStub(null, false);
    repository.setLatestRevision(123);

    List entries1 = new ArrayList();
    entries1.add(new SVNDirEntry("file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"));
    entries1.add(new SVNDirEntry("file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"));
    entries1.add(new SVNDirEntry("dir1", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
    entries1.add(new SVNDirEntry("file3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"));
    List entries2 = new ArrayList();
    entries2.add(new SVNDirEntry("dir2", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
    entries2.add(new SVNDirEntry("dirfile1.java", SVNNodeKind.FILE, 6400, false, 1, new Date(), "jesper"));
    entries2.add(new SVNDirEntry("dirfile2.html", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"));
    entries2.add(new SVNDirEntry("dirfile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"));

    repository.addDir("/", entries1);
    repository.addDir("/dir1/", entries2);
    repository.addDir("/dir1/dir2/", new ArrayList());

    index = new RevisionIndex();
    index.setRepository(repository);
    index.setStartPath("/");
    index.index();
    assertEquals(8, index.getIndexCount());
    //printIndex();
  }

  public void testFind() throws Exception {
    assertEquals(2, index.find("html").size());
  }

  public void testFindPattern() throws Exception {
    assertEquals(7, index.findPattern(".*[12].*").size());
  }

  public void testGetDirectories() throws Exception {
    assertEquals(2, index.getDirectories("/").size());
    assertEquals(1, index.getDirectories("/dir1/").size());
  }

  private void printIndex() {
    Iterator i = index.getEntries();
    while (i.hasNext()) {
      System.out.println(i.next());
    }
  }
}
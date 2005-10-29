package de.berlios.sventon.ctrl;

import junit.framework.TestCase;
import de.berlios.sventon.svnsupport.SVNRepositoryStub;
import de.berlios.sventon.command.SVNBaseCommand;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class FlattenControllerTest extends TestCase {

  SVNRepositoryStub repository;

  public void setUp() throws Exception {
    // Set up the repository stub
    repository = new SVNRepositoryStub(SVNURL.parseURIDecoded("http://localhost"), null);
    repository.setLatestRevision(123);

    List<SVNDirEntry> entries1 = new ArrayList<SVNDirEntry>();
    entries1.add(new SVNDirEntry("file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"));
    entries1.add(new SVNDirEntry("file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"));
    entries1.add(new SVNDirEntry("dir1", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
    entries1.add(new SVNDirEntry("file3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"));
    List<SVNDirEntry> entries2 = new ArrayList<SVNDirEntry>();
    entries2.add(new SVNDirEntry("dir2", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
    entries2.add(new SVNDirEntry("dirfile1.java", SVNNodeKind.FILE, 6400, false, 1, new Date(), "jesper"));
    entries2.add(new SVNDirEntry("dirfile2.html", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"));
    entries2.add(new SVNDirEntry("dirfile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"));

    repository.addDir("/", entries1);
    repository.addDir("/dir1/", entries2);
    repository.addDir("/dir1/dir2/", new ArrayList());

  }

  public void testSvnHandle() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    FlattenController controller = new FlattenController();
    ModelAndView model;
    try {
      model = controller.svnHandle(repository, command, SVNRevision.HEAD, null, null);
    } catch (SVNException ex) {
      throw new Exception(ex);
    }

  }

}

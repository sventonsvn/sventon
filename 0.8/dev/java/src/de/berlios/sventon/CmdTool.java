package de.berlios.sventon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;


public class CmdTool {

  /**
   * @param args
   */
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    SVNRepositoryFactoryImpl.setup();
    String URL = "svn://localhost/servletsvn/";
    try {
      SVNURL location = SVNURL.parseURIDecoded(URL);
      SVNRepository repository = SVNRepositoryFactory.create(location);

      // get latest revisions
      long latestRevision = repository.getLatestRevision();
      System.out.println("[" + location.toString() + "] latest revision: " + latestRevision);

      Collection<SVNDirEntry> dir = repository.getDir(".", repository.getLatestRevision(), new HashMap(),
          new ArrayList());
      for (SVNDirEntry entry : dir) {
        System.out.println(entry.getName() + entry.getRevision() + entry.getKind());
      }
 
    } catch (SVNException e) {
      e.printStackTrace();
    }

  }

}

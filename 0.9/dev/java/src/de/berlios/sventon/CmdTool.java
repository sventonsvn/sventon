package de.berlios.sventon;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.io.File;


public class CmdTool {

  /**
   * @param args
   */
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    String URL = "http://localhost:8080/svnsandbox/";
    try {
      SVNURL location = SVNURL.parseURIDecoded(URL);
      SVNRepository repository = SVNRepositoryFactory.create(location);
      ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(new File("."), "jesper", "jesper", false);
      repository.setAuthenticationManager(authManager);

      // get latest revisions
      long latestRevision = repository.getLatestRevision();
      System.out.println("[" + location.toString() + "] latest revision: " + latestRevision);

      Collection<SVNDirEntry> dir = repository.getDir("/bins/", repository.getLatestRevision(), new HashMap(),
          new ArrayList());
      for (SVNDirEntry entry : dir) {
        //System.out.println(entry.getName() + entry.getRevision() + entry.getKind());
        System.out.println(entry.getName() + " - " + entry.getURL());
      }

    } catch (SVNException e) {
      e.printStackTrace();
    }

  }

}

package de.berlios.sventon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.io.SVNDirEntry;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;

public class CmdTool {

  /**
   * @param args
   */
  public static void main(String[] args) {
    SVNRepositoryFactoryImpl.setup();
    String URL = "svn://localhost/servletsvn/";
    try {
      SVNRepositoryLocation location = SVNRepositoryLocation.parseURL(URL);
      SVNRepository repository = SVNRepositoryFactory.create(location);

      // get latest revisions
      long latestRevision = repository.getLatestRevision();
      System.out.println("[" + location.toString() + "] latest revision: " + latestRevision);

      Collection<SVNDirEntry> dir = repository.getDir(".", repository.getLatestRevision(), new HashMap(),
          new ArrayList());
      for (SVNDirEntry entry : dir) {
        System.out.println(entry.getName() + entry.getRevision() + entry.getKind());
      }

      
       // log messages String[] targetPaths = new String[] {""};
//        repository.log(targetPaths, 0, latestRevision, true, true, new
//        ISVNLogEntryHandler() { public void handleLogEntry(SVNLogEntry
//        logEntry) { System.out.println(logEntry.getRevision() + " : " +
//        logEntry.getMessage()); } });
       
    } catch (SVNException e) {
      e.printStackTrace();
    }

  }

}

package de.berlios.sventon;

import static org.tmatesoft.svn.core.SVNNodeKind.FILE;
import static org.tmatesoft.svn.core.SVNNodeKind.NONE;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class CheckPathExample {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {

    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();

    String svnURL = "svn://svn.berlios.de/sventon/";
    String davURL = "http://svn.berlios.de/svnroot/repos/sventon/";

    String correctPath = "trunk/doc/readme.txt";
    String incorrectPath = "trunk/doc/readme.txt/fail";

    try {

      SVNURL svnLocation = SVNURL.parseURIDecoded(svnURL);
      SVNRepository svnRepository = SVNRepositoryFactory.create(svnLocation);

      SVNURL davLocation = SVNURL.parseURIDecoded(davURL);
      SVNRepository davRepository = SVNRepositoryFactory.create(davLocation);

      long latestRevision = svnRepository.getLatestRevision();

      SVNNodeKind kind = davRepository.checkPath(correctPath, latestRevision);
      System.out.println("DAV: (" + correctPath + ") Kind is FILE? " + (kind == FILE));

      kind = davRepository.checkPath(incorrectPath, latestRevision);
      System.out.println("DAV: (" + incorrectPath + ") Kind is NONE? " + (kind == NONE));

      kind = svnRepository.checkPath(correctPath, latestRevision);
      System.out.println("SVN: (" + correctPath + ") Kind is FILE? " + (kind == FILE));

      //THIS FAILS WITH EXCEPTION
      kind = svnRepository.checkPath(incorrectPath, latestRevision);
      System.out.println("SVN: (" + incorrectPath + ") Kind is NONE? " + (kind == NONE));

    } catch (SVNException e) {
      e.printStackTrace();
    }
  }
}

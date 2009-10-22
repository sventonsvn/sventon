package org.sventon;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.*;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;
import org.tmatesoft.svn.core.io.diff.SVNDeltaProcessor;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test class.
 * <p/>
 * For testing svn+ssh, supply the following JVM parameters:
 * <ul>
 * <li>-Dsvnkit.ssh2.username=you</li>
 * <li>-Dsvnkit.ssh2.passphrase=your_passphrase</li>
 * <li>-Dsvnkit.ssh2.key=/path/to/your_dsa_or_rsa_key</li>
 * </ul>
 */
public class CmdTool {

  /**
   * @param args
   */
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    FSRepositoryFactory.setup();

    final String url = "svn://svn.berlios.de/sventon/";
    final String uid = null; // overridden by JVM parameter
    final String pwd = null; // overridden by JVM parameter

    try {
      final SVNURL location = SVNURL.parseURIDecoded(url);
      final SVNRepository repository = SVNRepositoryFactory.create(location);
      final File currentDir = new File(".");
      System.out.println("Current dir is: " + currentDir.getAbsolutePath());
      ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(currentDir, uid, pwd);
      repository.setAuthenticationManager(authManager);
      repository.setTunnelProvider(SVNWCUtil.createDefaultOptions(true));

      repository.testConnection();

      long latestRevision = repository.getLatestRevision();
      System.out.println("[" + location.toString() + "] latest revision: " + latestRevision);

      long reportRevision = 7;

      MyReporter reporter = new MyReporter(reportRevision);
      MyEditor editor = new MyEditor();
      repository.update(reportRevision, null, true, reporter, editor);

    } catch (SVNException e) {
      e.printStackTrace();
    }
  }

  static class MyReporter implements ISVNReporterBaton {
    private long r;

    MyReporter(long r) {
      this.r = r;
    }

    public void report(ISVNReporter isvnReporter) throws SVNException {
      isvnReporter.setPath("", null, r-1, SVNDepth.INFINITY, false);
      isvnReporter.finishReport();
    }
  }

  static class MyEditor implements ISVNEditor {

    SVNDeltaProcessor myDeltaProcessor = new SVNDeltaProcessor();

    public void targetRevision(long l) throws SVNException {
      System.out.println("rev: " + l);
    }

    public void openRoot(long l) throws SVNException {
      System.out.println("openRoot");
    }

    public void deleteEntry(String s, long l) throws SVNException {
      System.out.println("deleteEntry");
    }

    public void absentDir(String s) throws SVNException {
    }

    public void absentFile(String s) throws SVNException {
    }

    public void addDir(String s, String s1, long l) throws SVNException {
    }

    public void openDir(String s, long l) throws SVNException {
    }

    public void changeDirProperty(String s, SVNPropertyValue svnPropertyValue) throws SVNException {
    }

    public void closeDir() throws SVNException {
    }

    public void addFile(String s, String s1, long l) throws SVNException {
    }

    public void openFile(String s, long l) throws SVNException {
    }

    public void changeFileProperty(String s, String s1, SVNPropertyValue svnPropertyValue) throws SVNException {
    }

    public void closeFile(String s, String s1) throws SVNException {
    }

    public SVNCommitInfo closeEdit() throws SVNException {
      return null;
    }

    public void abortEdit() throws SVNException {
      System.out.println("abortEdit");
    }

    public void applyTextDelta(String s, String s1) throws SVNException {
      System.out.println("applyTextDelta: " + s + " - " + s1);
    }

    public OutputStream textDeltaChunk(String s, SVNDiffWindow svnDiffWindow) throws SVNException {
      System.out.println("textDeltaChunk: " + svnDiffWindow);
      return null;
    }

    public void textDeltaEnd(String s) throws SVNException {
      System.out.println("textDeltaEnd");
    }
  }

}

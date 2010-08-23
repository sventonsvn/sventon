package org.sventon.service.svnkit;

import org.sventon.model.LogEntry;
import org.sventon.service.RepositoryService;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.util.List;

//TODO: This test class is heavily dependent on SVNKit. It must remain in the deepest SVNKit-cellar until fixed....

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

      RepositoryService service = new SVNKitRepositoryService();

      final List<LogEntry> logEntries = service.getLatestRevisions(null, new SVNKitConnection(repository), 2);
      for (LogEntry logEntry : logEntries) {
        System.out.println("logEntry = " + logEntry);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

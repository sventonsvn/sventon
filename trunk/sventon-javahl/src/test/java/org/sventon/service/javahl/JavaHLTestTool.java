package org.sventon.service.javahl;

import org.sventon.SVNConnection;
import org.sventon.model.SVNURL;
import org.sventon.service.RepositoryService;
import org.tigris.subversion.javahl.SVNClient;

import java.io.File;

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
public class JavaHLTestTool {

  /**
   * @param args
   */
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {

    final String url = "svn://svn.berlios.de/sventon/";
    final String uid = null; // overridden by JVM parameter
    final String pwd = null; // overridden by JVM parameter

    try {
      final File currentDir = new File(".");
      System.out.println("Current dir is: " + currentDir.getAbsolutePath());

      final RepositoryService service = new JavaHLRepositoryService();
      final SVNClient client = new SVNClient();
      final SVNURL svnUrl = SVNURL.parse(url);
      final SVNConnection connection = new JavaHLConnection(client, svnUrl, null);
      final long latestRevision = service.getLatestRevision(connection);
      System.out.println(client.getVersion());
      System.out.println("[" + url + "] latest revision: " + latestRevision);

      //final List<LogEntry> logEntries = service.getLatestRevisions(null, connection, 2);
      //for (LogEntry logEntry : logEntries) {
      //  System.out.println("logEntry = " + logEntry);
      //}

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

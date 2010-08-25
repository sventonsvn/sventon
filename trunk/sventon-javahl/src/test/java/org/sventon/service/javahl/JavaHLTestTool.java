package org.sventon.service.javahl;

import org.sventon.SVNConnection;
import org.sventon.model.LogEntry;
import org.sventon.model.SVNURL;
import org.sventon.service.RepositoryService;
import org.tigris.subversion.javahl.*;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

//      final List<LogEntry> logEntries = service.getLatestRevisions(null, connection, 2);
//      for (LogEntry logEntry : logEntries) {
//        System.out.println("logEntry = " + logEntry);
//      }

      final List<LogEntry> logEntries2 = service.getLogEntriesFromRepositoryRoot(connection, 100, 110);
      for (LogEntry logEntry : logEntries2) {
        System.out.println("logEntry = " + logEntry);
      }

      final LogEntry logEntry = service.getLogEntry(null, connection, 1817);
      System.out.println(logEntry);

      //log(client, url);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static byte[] getFile(SVNClient client, String url, Revision revision) throws ClientException {
    return client.fileContent(url, revision, revision);
  }

  private static void diffPaths(SVNClient client, String url1, String url2) throws ClientException {
    DiffSummaryReceiver receiver = new DiffSummaryReceiver() {
      @Override
      public void onSummary(DiffSummary descriptor) {
        System.out.println(descriptor.getPath() + descriptor.getDiffKind());
      }
    };
    client.diffSummarize(url2, Revision.HEAD, url1, Revision.HEAD, Depth.infinity, null, true, receiver);
  }

  private static void status(SVNClient client, String url) {
    //client.status(url, )
  }

  private static void info(SVNClient client, String url) throws ClientException {
    Info info = client.info(url);
    System.out.println(url + " is of type " + NodeKind.getNodeKindName(info.getNodeKind()));
  }

  private static void blame(SVNClient client, String url) throws ClientException {
    BlameCallback callback = new BlameCallback() {
      @Override
      public void singleLine(Date changed, long revision, String author, String line) {
        System.out.println(revision + " " + author + " " + line);
      }
    };
    client.blame(url, Revision.getInstance(1), Revision.HEAD, callback);
  }

  private static void log(SVNClient client, String url) throws ClientException {
    LogMessageCallback callback = new LogMessageCallback() {
      @Override
      public void singleMessage(ChangePath[] changedPaths, long revision, Map revprops, boolean hasChildren) {
        System.out.print(revision + ": ");
        if (revprops != null){
          System.out.print(revprops.get(PropertyData.REV_LOG));
          System.out.print(revprops.get(PropertyData.REV_AUTHOR));
          System.out.println(revprops.get(PropertyData.REV_DATE));
        }
        for (ChangePath changedPath : changedPaths) {
          System.out.println(changedPath.getPath());
        }
        System.out.println("\n");
      }
    };
    int limit = 10;
    client.logMessages(url, Revision.HEAD, Revision.HEAD, Revision.getInstance(1), false, true, false, new String[]{PropertyData.REV_LOG, PropertyData.REV_AUTHOR, PropertyData.REV_DATE}, limit, callback);
  }

  private static void list(SVNClient client, String url) throws ClientException {
    final ListCallback callback = new ListCallback() {
      @Override
      public void doEntry(DirEntry dirent, Lock lock) {
        System.out.println(dirent.getPath());
      }
    };
    client.list(url, Revision.HEAD, Revision.HEAD, Depth.infinity, DirEntry.Fields.all, true, callback);
  }


}

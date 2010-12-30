/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service.svnkit;

import org.sventon.SVNConnection;
import org.sventon.model.*;
import org.sventon.service.RepositoryService;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import java.io.File;
import java.util.Calendar;
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
public class SVNKitTestTool {

  /**
   * @param args
   */
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    FSRepositoryFactory.setup();

    final String url = "svn://svn.berlios.de/sventon/";
//    final String uid = null; // overridden by JVM parameter
//    final String pwd = null; // overridden by JVM parameter

    try {
      final SVNURL location = SVNURL.parseURIDecoded(url);
      final SVNRepository repository = SVNRepositoryFactory.create(location);
      final File currentDir = new File(".");
      System.out.println("Current dir is: " + currentDir.getAbsolutePath());
//      ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(currentDir, uid, pwd);
//      repository.setAuthenticationManager(authManager);
//      repository.setTunnelProvider(SVNWCUtil.createDefaultOptions(true));

      final RepositoryService service = new SVNKitRepositoryService();
      final SVNConnection connection = new SVNKitConnection(repository);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////          Cut & Paste Zone         ////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      final long latestRevision = service.getLatestRevision(connection);
      System.out.println("\nLatest revision for " + url + " : " + latestRevision);

      
      System.out.println("\nLatest Revisions:");
      final List<LogEntry> logEntries = service.getLatestRevisions(null, connection, 2);
      for (LogEntry logEntry : logEntries) {
        System.out.println("logEntry = " + logEntry);
      }


      System.out.println("\nLogEntries from root:");
      final List<LogEntry> logEntries2 = service.getLogEntriesFromRepositoryRoot(connection, 100, 110);
      for (LogEntry logEntry : logEntries2) {
        System.out.println("logEntry = " + logEntry);
      }


      System.out.println("\nLogEntry for single revision:");
      final LogEntry logEntry = service.getLogEntry(null, connection, 1817);
      System.out.println(logEntry);


      System.out.println("\nGet LogEntries for /trunk/assembly-bin-svnkit.xml [1 .. 1817]");
      final List<LogEntry> entries = service.getLogEntries(null, connection, 1, 1817, "/trunk/assembly-bin-svnkit.xml", 10, false, false);
      for (LogEntry entry : entries) {
        System.out.println("Entry: " + entry.toString());
      }


      System.out.println("\nFile properties for /trunk/assembly-bin-svnkit.xml at revision 1817");
      final Properties fileProperties = service.listProperties(connection, "/trunk/assembly-bin-svnkit.xml", 1817);
      System.out.println(fileProperties.toString());


      System.out.println("\nGet Locks");
      final Map<String,DirEntryLock> map = service.getLocks(connection, "/branches/features/svn_facade/sventon/readme.txt", false);
      for (String s : map.keySet()) {
        System.out.println("Path: " + s);
        System.out.println("DirEntryLock: " + map.get(s).toString());
      }


      final Calendar calendar = Calendar.getInstance();
      calendar.set(2010, 7, 17, 12, 34, 56);
      final Revision revision = service.translateRevision(connection, Revision.create(calendar.getTime()), 0);
      System.out.println("\nTranslated revision: " + revision.toString());


      System.out.print("\nRevisions for path trunk/assembly-bin-svnkit.xml at [1 .. 1817]. Limit 10\n {");
      final List<Long> revisionsForPath = service.getRevisionsForPath(connection, "/trunk/assembly-bin-svnkit.xml", 1, 1817, false, 10);
      for (Long rev : revisionsForPath) {
        System.out.print(rev.toString() + " ");
      }
      System.out.println("}");


      System.out.print("\nGetFileRevisions for trunk/assembly-bin-svnkit.xml {\n");
      final List<FileRevision> revisionList = service.getFileRevisions(connection, "trunk/assembly-bin-svnkit.xml", 1817);
      for (FileRevision fileRevision : revisionList) {
        System.out.println("\t" + fileRevision.toString() + " ");
      }
      System.out.println("}");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////                 end               ////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

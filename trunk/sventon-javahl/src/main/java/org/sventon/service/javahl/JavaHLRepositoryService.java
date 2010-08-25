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
package org.sventon.service.javahl;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.mutable.MutableLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.colorer.Colorer;
import org.sventon.diff.DiffException;
import org.sventon.export.ExportDirectory;
import org.sventon.model.*;
import org.sventon.model.DirEntry;
import org.sventon.model.Properties;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.DiffCommand;
import org.tigris.subversion.javahl.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * JavaHLRepositoryService.
 *
 * @author jesper@sventon.org
 */
public class JavaHLRepositoryService implements RepositoryService {
  private static final String[] REV_PROP_NAMES = new String[]{PropertyData.REV_LOG, PropertyData.REV_AUTHOR, PropertyData.REV_DATE};

  /**
   * Logger for this class and subclasses.
   */
  final Log logger = LogFactory.getLog(getClass());

  @Override
  public LogEntry getLogEntry(RepositoryName repositoryName, SVNConnection connection, long revision) throws SventonException {
    final List<LogEntry> logEntries = getLogEntriesFromRepositoryRoot(connection, revision, revision);
    Validate.isTrue(logEntries.size() == 1, "Too many LogEntries for a given revision. One revision always relates to exactly one LogEntry");

    return logEntries.get(0);
  }

  @Override
  public List<LogEntry> getLogEntriesFromRepositoryRoot(SVNConnection connection, long fromRevision, long toRevision) throws SventonException {
    return getLogEntries(null, connection, fromRevision, toRevision, connection.getRepositoryRootUrl().getUrl(), 0, false, true);
  }

  @Override
  public List<LogEntry> getLogEntries(RepositoryName repositoryName, SVNConnection connection, long fromRevision, long toRevision, String path, long limit, boolean stopOnCopy, boolean includeChangedPaths) throws SventonException {
    final SVNClient client = (SVNClient) connection.getDelegate();

    final List<LogEntry> logEntries = new ArrayList<LogEntry>();

    try {
      client.logMessages(path, JavaHLConverter.convertRevision(toRevision), JavaHLConverter.getRevisionRange(fromRevision, toRevision),
          stopOnCopy, includeChangedPaths, false, REV_PROP_NAMES, (int) limit, new LogMessageCallback(){
            @Override
            public void singleMessage(ChangePath[] changePaths, long l, Map map, boolean b) {
              final LogEntry logEntry = new LogEntry(l, JavaHLConverter.convertRevisionPropertyMap(map), JavaHLConverter.convertChangedPaths(changePaths));

              logEntries.add(logEntry);
            }
          });
    } catch (ClientException e) {
      return translateException("Unable to get log entries for " + path + " at revision [" + fromRevision + " .. " + toRevision + "]", e);
    }

    return logEntries;
  }


  @Override
  public void export(SVNConnection connection, List<PathRevision> targets, long pegRevision, ExportDirectory exportDirectory) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClient client = conn.getDelegate();

    for (final PathRevision fileRevision : targets) {
      final String path = fileRevision.getPath();
      final long revision = fileRevision.getRevision().getNumber();
      final File revisionRootDir = new File(exportDirectory.getDirectory(), String.valueOf(revision));

      Validate.isTrue(revisionRootDir.exists() && revisionRootDir.mkdirs(), "Unable to create directory: " + revisionRootDir.getAbsolutePath());

      try {
        final File destination = new File(revisionRootDir, path);
        final String pathToExport = conn.getRepositoryRootUrl().getFullPath(path);

        logger.debug("Exporting file [" + pathToExport + "] revision [" + revision + "]");
        client.doExport(pathToExport, destination.getAbsolutePath(),
            org.tigris.subversion.javahl.Revision.getInstance(revision),
            org.tigris.subversion.javahl.Revision.getInstance(pegRevision), true, false, Depth.infinity, null);
      } catch (ClientException ex) {
        translateException("Error exporting [" + path + "@" + revision + "]", ex);
      }
    }
  }

  @Override
  public void getFileContents(SVNConnection connection, String path, long revision, OutputStream output) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClient client = conn.getDelegate();

    try {
      final byte[] bytes = client.fileContent(conn.getRepositoryRootUrl().getFullPath(path),
          org.tigris.subversion.javahl.Revision.getInstance(revision),
          org.tigris.subversion.javahl.Revision.getInstance(revision));
      output.write(bytes);
    } catch (IOException e) {
      throw new SventonException(e.getMessage());
    } catch (ClientException ce) {
      translateException("Unable to get file contents: " + path, ce);
    }
  }

  @Override
  public Properties getFileProperties(SVNConnection connection, String path, long revision) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getFileChecksum(SVNConnection connection, String path, long revision) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Long getLatestRevision(SVNConnection connection) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClient client = conn.getDelegate();

    try {
      final MutableLong revision = new MutableLong();
      client.info2(conn.getRepositoryRootUrl().toString(), org.tigris.subversion.javahl.Revision.HEAD,
          org.tigris.subversion.javahl.Revision.HEAD, Depth.empty, null, new InfoCallback() {
            @Override
            public void singleInfo(Info2 info2) {
              revision.setValue(info2.getLastChangedRev());
            }
          });
      return revision.toLong();

    } catch (ClientException ce) {
      return translateException("Unable to get latest revision", ce);
    }
  }

  @Override
  public DirEntry.Kind getNodeKind(SVNConnection connection, String path, long revision) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, DirEntryLock> getLocks(SVNConnection connection, String startPath) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DirList list(final SVNConnection connection, final String path, final long revision) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClient client = conn.getDelegate();

    final List<DirEntry> dirEntries = new ArrayList<DirEntry>();
    try {
      client.list(conn.getRepositoryRootUrl().getFullPath(path), org.tigris.subversion.javahl.Revision.getInstance(revision),
          org.tigris.subversion.javahl.Revision.getInstance(revision), Depth.immediates,
          org.tigris.subversion.javahl.DirEntry.Fields.all, false, new ListCallback() {
            @Override
            public void doEntry(org.tigris.subversion.javahl.DirEntry dirEntry, Lock lock) {
              dirEntries.add(new DirEntry(path, dirEntry.getPath(), dirEntry.getLastAuthor(),
                  dirEntry.getLastChanged(), DirEntry.Kind.valueOf(NodeKind.getNodeKindName(
                      dirEntry.getNodeKind()).trim().toUpperCase()), dirEntry.getLastChangedRevision().getNumber(),
                  dirEntry.getSize()));
            }
          });
      // Skip the first entry as that's the one we passed in 'path'.
      return new DirList(dirEntries.subList(1, dirEntries.size()), null);
    } catch (ClientException ce) {
      return translateException("Unable to list directory: " + path, ce);
    }

  }

  @Override
  public DirEntry getEntryInfo(SVNConnection connection, String path, long revision) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<FileRevision> getFileRevisions(SVNConnection connection, String path, long revision) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<SideBySideDiffRow> diffSideBySide(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String diffUnified(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<InlineDiffRow> diffInline(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<DiffStatus> diffPaths(SVNConnection connection, DiffCommand command) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public AnnotatedTextFile blame(SVNConnection connection, String path, long revision, String charset, Colorer colorer) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public DirEntry.Kind getNodeKindForDiff(SVNConnection connection, DiffCommand command) throws SventonException, DiffException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Revision translateRevision(Revision revision, long headRevision, SVNConnection connection) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<LogEntry> getLatestRevisions(RepositoryName repositoryName, SVNConnection connection, int revisionCount) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Long> getRevisionsForPath(SVNConnection connection, String path, long fromRevision, long toRevision, boolean stopOnCopy, long limit) throws SventonException {
    throw new UnsupportedOperationException();
  }

  private <T extends Object> T translateException(String errorMessage, ClientException exception) throws SventonException {
    // TODO: Filter exceptions here and translate to sventon specific versions of auth required etc.
    throw new SventonException(errorMessage, exception);
  }

}

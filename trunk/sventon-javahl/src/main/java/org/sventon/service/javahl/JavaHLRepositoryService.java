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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.mutable.MutableLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.AuthenticationException;
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.colorer.Colorer;
import org.sventon.diff.DiffException;
import org.sventon.export.ExportDirectory;
import org.sventon.model.*;
import org.sventon.model.DirEntry;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.DiffCommand;
import org.tigris.subversion.javahl.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
          stopOnCopy, includeChangedPaths, false, REV_PROP_NAMES, (int) limit, new LogMessageCallback() {
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
  public Properties listProperties(SVNConnection connection, String path, long revision) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClient client = conn.getDelegate();

    final Properties properties = new Properties();

    try {
      client.properties(conn.getRepositoryRootUrl().getFullPath(path), JavaHLConverter.convertRevision(revision), JavaHLConverter.convertRevision(revision), Depth.empty, null, new ProplistCallback() {
        @Override
        public void singlePath(String path, Map prop) {
        for (Object o : prop.keySet()) {
            properties.put(new Property((String) o), new PropertyValue((String) prop.get(o)));
          }
        }
      });

      for (Property entry : Property.COMMON_SVN_ENTRY_PROPERTIES) {
        final PropertyData data = client.propertyGet(conn.getRepositoryRootUrl().getFullPath(path), entry.getName(), JavaHLConverter.convertRevision(revision));
        if (data != null){
          properties.put(new Property(data.getName()), new PropertyValue(data.getValue()));
        }
      }

    } catch (ClientException e) {
      return translateException("Could not get properties for " + path + " at revision " + revision, e);
    }

    return properties;
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
                  dirEntry.getLastChanged(), JavaHLConverter.convertNodeKind(dirEntry.getNodeKind()),
                  dirEntry.getLastChangedRevision().getNumber(),
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
  public DirEntry getEntryInfo(SVNConnection connection, final String path, long revision) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClient client = conn.getDelegate();

    final List<DirEntry> dirEntries = new ArrayList<DirEntry>();
    try {
      client.info2(conn.getRepositoryRootUrl().getFullPath(path),
          org.tigris.subversion.javahl.Revision.getInstance(revision),
          org.tigris.subversion.javahl.Revision.getInstance(revision),
          Depth.infinity, null, new InfoCallback() {
            @Override
            public void singleInfo(Info2 info2) {
              final String name = info2.getPath();
              final String author = info2.getLastChangedAuthor();
              final long lastChangedRev = info2.getLastChangedRev();
              final Date date = info2.getLastChangedDate();
              dirEntries.add(new DirEntry(FilenameUtils.getFullPath(path), name, author, date,
                  JavaHLConverter.convertNodeKind(info2.getKind()), lastChangedRev, info2.getReposSize()));
            }
          });
      return dirEntries.get(0);
    } catch (ClientException ce) {
      return translateException("Cannot get info for [" + path + "@" + revision + "]", ce);
    }

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
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClient client = conn.getDelegate();

    try {
      final String blamePath = conn.getRepositoryRootUrl().getFullPath(path);
      logger.debug("Blaming file [" + blamePath + "] revision [" + revision + "]");

      final AnnotatedTextFile annotatedTextFile = new AnnotatedTextFile(path, charset, colorer);
      final org.tigris.subversion.javahl.Revision startRev = org.tigris.subversion.javahl.Revision.getInstance(0);
      final org.tigris.subversion.javahl.Revision endRev = org.tigris.subversion.javahl.Revision.getInstance(revision);
      client.blame(blamePath, endRev, startRev, endRev, false, false, new BlameCallback2() {
        @Override
        public void singleLine(Date date, long revision, String author, Date mergedDate, long mergedRevision,
                               String mergedAuthor, String mergedPath, String line) {
          annotatedTextFile.addRow(date, revision, author, line);
        }
      });

      try {
        annotatedTextFile.colorize();
      } catch (IOException ioex) {
        logger.warn("Unable to colorize [" + path + "]", ioex);
      }
      return annotatedTextFile;
    } catch (ClientException ce) {
      return translateException("Error blaming [" + path + "@" + revision + "]", ce);
    }
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
    return getLogEntries(repositoryName, connection, Revision.HEAD.getNumber(), Revision.FIRST.getNumber(), connection.getRepositoryRootUrl().getUrl(), revisionCount, false, true);
  }

  @Override
  public List<Long> getRevisionsForPath(SVNConnection connection, String path, long fromRevision, long toRevision, boolean stopOnCopy, long limit) throws SventonException {
    throw new UnsupportedOperationException();
  }

  private <T extends Object> T translateException(String errorMessage, ClientException exception) throws SventonException {
    // TODO: Filter exceptions here and translate to sventon specific versions of auth required etc.
    if (exception.getMessage().contains("Authorization failed")) {
      throw new AuthenticationException(exception.getMessage(), exception);
    }

    throw new SventonException(errorMessage, exception);
  }

}

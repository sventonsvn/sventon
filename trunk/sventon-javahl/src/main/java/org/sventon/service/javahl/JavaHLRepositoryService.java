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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.mutable.MutableLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.*;
import org.sventon.colorer.Colorer;
import org.sventon.diff.DiffException;
import org.sventon.diff.IdenticalFilesException;
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
    return getLogEntries(null, connection, fromRevision, toRevision, "/", 0, false, true);
  }

  @Override
  public List<LogEntry> getLogEntries(RepositoryName repositoryName, SVNConnection connection, long fromRevision, long toRevision, String path, long limit, boolean stopOnCopy, boolean includeChangedPaths) throws SventonException {
    final SVNClientInterface client = (SVNClientInterface) connection.getDelegate();

    final List<LogEntry> logEntries = new ArrayList<LogEntry>();

    try {
      client.logMessages(connection.getRepositoryRootUrl().getFullPath(path), JavaHLConverter.convertRevision(toRevision), JavaHLConverter.getRevisionRange(fromRevision, toRevision),
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
    final SVNClientInterface client = conn.getDelegate();

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
    final SVNClientInterface client = conn.getDelegate();

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
    final SVNClientInterface client = conn.getDelegate();

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
        if (data != null) {
          properties.put(new Property(data.getName()), new PropertyValue(data.getValue()));
        }
      }
    } catch (ClientException e) {
      return translateException("Could not get properties for [" + path + "] at revision: " + revision, e);
    }
    return properties;
  }

  @Override
  public String getFileChecksum(SVNConnection connection, String path, long revision) throws SventonException {
    return listProperties(connection, path, revision).getStringValue(Property.CHECKSUM);
  }

  @Override
  public Long getLatestRevision(SVNConnection connection) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClientInterface client = conn.getDelegate();

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
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClientInterface client = conn.getDelegate();

    final List<DirEntry.Kind> nodeKinds = new ArrayList<DirEntry.Kind>();
    try {
      client.info2(conn.getRepositoryRootUrl().getFullPath(path),
          org.tigris.subversion.javahl.Revision.getInstance(revision),
          org.tigris.subversion.javahl.Revision.getInstance(revision),
          Depth.empty, null, new InfoCallback() {
            @Override
            public void singleInfo(Info2 info2) {
              nodeKinds.add(JavaHLConverter.convertNodeKind(info2.getKind()));
            }
          });
      Validate.isTrue(nodeKinds.size() == 1, "Too many nodeKinds for a given entry. One entry always relates to exactly one nodeKind");
      return nodeKinds.get(0);
    } catch (ClientException ce) {
      return translateException("Cannot get nodeKind for [" + path + "@" + revision + "]", ce);
    }
  }

  @Override
  public Map<String, DirEntryLock> getLocks(SVNConnection connection, final String startPath) {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClientInterface client = conn.getDelegate();
    final HashMap<String, DirEntryLock> locks = new HashMap<String, DirEntryLock>();


    try {
      client.info2(conn.getRepositoryRootUrl().getFullPath(startPath),
          org.tigris.subversion.javahl.Revision.HEAD,
          org.tigris.subversion.javahl.Revision.HEAD,
          Depth.infinity, null, new InfoCallback() {
            @Override
            public void singleInfo(Info2 info2) {
              final Lock lock = info2.getLock();
              if (lock != null) {
                final DirEntryLock entryLock = new DirEntryLock(lock.getToken(), lock.getPath(), lock.getOwner(), lock.getComment(), lock.getCreationDate(), lock.getExpirationDate());
                locks.put(lock.getPath(), entryLock);
              } else {
                logger.debug("Unable to get locks for path [" + startPath + "]. Directory may not exist in HEAD");
              }
            }
          });
    } catch (ClientException e) {
      logger.debug("Unable to get locks for path [" + startPath + "]. Directory may not exist in HEAD", e);
    }

    return locks;
  }

  @Override
  public DirList list(final SVNConnection connection, final String path, final long revision) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClientInterface client = conn.getDelegate();

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
    final SVNClientInterface client = conn.getDelegate();

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
    final List<LogEntry> entries = getLogEntries(null, connection, revision, Revision.FIRST.getNumber(), path, 100, false, true);
    final List<FileRevision> revisions = new ArrayList<FileRevision>();

    LogEntry.setPathAtRevisionInLogEntries(entries, path);

    for (LogEntry entry : entries) {
      final FileRevision fileRevision = new FileRevision(entry.getPathAtRevision(), Revision.create(entry.getRevision()));
       revisions.add(fileRevision);
    }

    return revisions;
  }


  @Override
  public List<SideBySideDiffRow> diffSideBySide(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String diffUnified(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClientInterface client = conn.getDelegate();

    // TODO: Add call to: assertNotBinary(connection, command, pegRevision);

    try {
      final File outFile = createTempFileForDiff();

      try {
        final String fromPath = conn.getRepositoryRootUrl().getFullPath(command.getFromPath());
        final String toPath = conn.getRepositoryRootUrl().getFullPath(command.getToPath());
        final org.tigris.subversion.javahl.Revision fromRev =
            org.tigris.subversion.javahl.Revision.getInstance(command.getFromRevision().getNumber());
        final org.tigris.subversion.javahl.Revision toRev =
            org.tigris.subversion.javahl.Revision.getInstance(command.getToRevision().getNumber());

        client.diff(fromPath, fromRev, toPath, toRev, null, outFile.getAbsolutePath(), Depth.empty, null, false, false, true);
        final String diffResultString = FileUtils.readFileToString(outFile, charset);
        if ("".equals(diffResultString)) {
          throw new IdenticalFilesException(command.getFromPath() + ", " + command.getToPath());
        }
        return new TextFile(stripUnifiedDiffHeader(diffResultString)).getContent();
      } catch (ClientException ce) {
        return translateException("Unable to produce unified diff", ce);
      } finally {
        if (!outFile.delete()) {
          outFile.deleteOnExit();
        }
      }
    } catch (IOException ioex) {
      throw new DiffException("Unable to produce unified diff", ioex);
    }
  }

  private String stripUnifiedDiffHeader(final String diffResult) {
    final String startMarker = "@@";
    if (!diffResult.contains(startMarker)) return diffResult;
    return diffResult.substring(diffResult.indexOf(startMarker));
  }

  private File createTempFileForDiff() throws IOException {
    return File.createTempFile("sventon-temp", ".diff");
  }

  @Override
  public List<InlineDiffRow> diffInline(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<DiffStatus> diffPaths(SVNConnection connection, final DiffCommand command) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClientInterface client = conn.getDelegate();

    final List<DiffStatus> result = new ArrayList<DiffStatus>();

    try {
      final String fromPath = conn.getRepositoryRootUrl().getFullPath(command.getFromPath());
      final String toPath = conn.getRepositoryRootUrl().getFullPath(command.getToPath());
      final org.tigris.subversion.javahl.Revision fromRev =
          org.tigris.subversion.javahl.Revision.getInstance(command.getFromRevision().getNumber());
      final org.tigris.subversion.javahl.Revision toRev =
          org.tigris.subversion.javahl.Revision.getInstance(command.getToRevision().getNumber());

      client.diffSummarize(fromPath, fromRev, toPath, toRev, Depth.infinity, null, true, new DiffSummaryReceiver() {
        @Override
        public void onSummary(DiffSummary diffSummary) {
          final ChangeType type = ChangeType.parse(diffSummary.getDiffKind().toString());
          result.add(new DiffStatus(type, diffSummary.getPath(), diffSummary.propsChanged()));
        }
      });
    } catch (ClientException ce) {
      return translateException("Could not calculate diff for " + command.toString(), ce);
    }
    return result;
  }

  @Override
  public AnnotatedTextFile blame(SVNConnection connection, String path, long revision, String charset, Colorer colorer) throws SventonException {
    final JavaHLConnection conn = (JavaHLConnection) connection;
    final SVNClientInterface client = conn.getDelegate();

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
    final long fromRevision;
    final long toRevision;

    if (command.hasPegRevision()) {
      fromRevision = command.getPegRevision();
      toRevision = command.getPegRevision();
    } else {
      fromRevision = command.getFromRevision().getNumber();
      toRevision = command.getToRevision().getNumber();
    }

    final DirEntry.Kind nodeKind1;
    final DirEntry.Kind nodeKind2;

    try {
      nodeKind1 = getNodeKind(connection, command.getFromPath(), fromRevision);
    } catch (NoSuchRevisionException e) {
      throw new DiffException("Path [" + command.getFromPath() + "] does not exist as revision [" + fromRevision + "]");
    }
    try {
      nodeKind2 = getNodeKind(connection, command.getToPath(), toRevision);
    } catch (NoSuchRevisionException e) {
      throw new DiffException("Path [" + command.getToPath() + "] does not exist as revision [" + toRevision + "]");
    }

    assertSameKind(nodeKind1, nodeKind2);
    return nodeKind1;
  }

  /**
   * TODO: Move to abstract base class?
   *
   * @param nodeKind1
   * @param nodeKind2
   * @throws DiffException
   */
  private void assertSameKind(final DirEntry.Kind nodeKind1, final DirEntry.Kind nodeKind2) throws DiffException {
    if (nodeKind1 != nodeKind2) {
      throw new DiffException("Entries are different kinds! " + nodeKind1 + "!=" + nodeKind2);
    }
  }

  @Override
  public Revision translateRevision(Revision revision, long headRevision, SVNConnection connection) throws SventonException {
    final long revisionNumber = revision.getNumber();

    if (revision.isHeadRevision() || revisionNumber == headRevision) {
      return Revision.createHeadRevision(headRevision);
    }

    if (revisionNumber < 0) {
      final Date date = revision.getDate();
      if (date != null) {
        final JavaHLConnection conn = (JavaHLConnection) connection;
        final SVNClientInterface client = conn.getDelegate();
        try {
          final MutableLong revAtDate = new MutableLong();
          client.info2(conn.getRepositoryRootUrl().getUrl(),
              org.tigris.subversion.javahl.Revision.getInstance(date),
              org.tigris.subversion.javahl.Revision.getInstance(date),
              Depth.empty, null, new InfoCallback() {
                @Override
                public void singleInfo(Info2 info2) {
                  revAtDate.setValue(info2.getRev());
                  // TODO: Is it better to look at last changed rev?
//                    revAtDate.setValue(info2.getLastChangedRev());
                }
              });
          return Revision.create(revAtDate.longValue());
        } catch (ClientException ex) {
          return translateException("Unable to translate revision: " + revision, ex);
        }
      } else {
        logger.warn("Unexpected revision: " + revision);
        return Revision.createHeadRevision(headRevision);
      }
    }
    return Revision.create(revisionNumber);
  }

  @Override
  public List<LogEntry> getLatestRevisions(RepositoryName repositoryName, SVNConnection connection, int revisionCount) throws SventonException {
    return getLogEntries(repositoryName, connection, Revision.HEAD.getNumber(), Revision.FIRST.getNumber(), "/", revisionCount, false, true);
  }

  @Override
  public List<Long> getRevisionsForPath(SVNConnection connection, String path, long fromRevision, long toRevision, boolean stopOnCopy, long limit) throws SventonException {
    final List<Long> list = new ArrayList<Long>();
    for (LogEntry entry : getLogEntries(null, connection, fromRevision, toRevision, path, limit, stopOnCopy, false)) {
      list.add(entry.getRevision());
    }
    return list;
  }

  private <T extends Object> T translateException(String errorMessage, ClientException exception) throws SventonException {
    // TODO: Filter exceptions here and translate to sventon specific versions of auth required etc.

    // TODO: Find a better way instead of parsing error messages!

    if (exception.getMessage().contains("Authorization failed")) {
      throw new AuthenticationException(exception.getMessage(), exception);
    }

    if (exception.getMessage().contains("non-existent in revision")) {
      throw new NoSuchRevisionException("Unable to get node kind: " + exception.getMessage());
    }

    if (exception.getMessage().contains("was not found in the repository")) {
      throw new DirEntryNotFoundException("Entry not found: " + exception.getMessage());
    }

    throw new SventonException(errorMessage, exception);
  }

}

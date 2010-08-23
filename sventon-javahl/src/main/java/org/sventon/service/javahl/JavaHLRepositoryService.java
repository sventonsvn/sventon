package org.sventon.service.javahl;

import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.colorer.Colorer;
import org.sventon.diff.DiffException;
import org.sventon.export.ExportDirectory;
import org.sventon.model.*;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.DiffCommand;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * JavaHLRepositoryService.
 *
 * @author jesper@sventon.org
 */
public class JavaHLRepositoryService implements RepositoryService {
  @Override
  public LogEntry getLogEntry(RepositoryName repositoryName, SVNConnection connection, long revision) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<LogEntry> getLogEntriesFromRepositoryRoot(SVNConnection connection, long fromRevision, long toRevision) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<LogEntry> getLogEntries(RepositoryName repositoryName, SVNConnection connection, long fromRevision, long toRevision, String path, long limit, boolean stopOnCopy, boolean includeChangedPaths) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void export(SVNConnection connection, List<PathRevision> targets, long pegRevision, ExportDirectory exportDirectory) throws SventonException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void getFileContents(SVNConnection connection, String path, long revision, OutputStream output) throws SventonException {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
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
  public DirList list(SVNConnection connection, String path, long revision) throws SventonException {
    throw new UnsupportedOperationException();
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
}

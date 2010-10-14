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
package org.sventon.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.diff.*;
import org.sventon.NoSuchRevisionException;
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.model.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 */
public abstract class AbstractRepositoryService implements RepositoryService {

  /**
   * Logger for this class and subclasses.
   */
  final Log logger = LogFactory.getLog(getClass());


  @Override
  public List<SideBySideDiffRow> diffSideBySide(final SVNConnection connection, final PathRevision from,
                                                final PathRevision to, final Revision pegRevision, final String charset)
      throws SventonException, DiffException {

    assertNotBinary(connection, from, to, pegRevision);

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      if (Revision.UNDEFINED.equals(pegRevision)) {
        leftFile = getTextFile(connection, from.getPath(), from.getRevision().getNumber(), charset);
        rightFile = getTextFile(connection, to.getPath(), to.getRevision().getNumber(), charset);
      } else {
        leftFile = getTextFile(connection, from.getPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(connection, to.getPath(), pegRevision.getNumber(), charset);
      }
      return createSideBySideDiff(from, to, charset, leftFile, rightFile);
    } catch (IOException ioex) {
      throw new DiffException("Unable to produce unified diff", ioex);
    }
  }


  public List<SideBySideDiffRow> createSideBySideDiff(final PathRevision from, final PathRevision to,
                                                      final String charset, final TextFile leftFile,
                                                      final TextFile rightFile) throws IOException {
    final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
    final InputStream leftStream = new ByteArrayInputStream(leftFile.getContent().getBytes());
    final InputStream rightStream = new ByteArrayInputStream(rightFile.getContent().getBytes());
    final DiffProducer diffProducer = new DiffProducer(leftStream, rightStream, charset);

    diffProducer.doNormalDiff(diffResult);
    final String diffResultString = diffResult.toString(charset);

    if ("".equals(diffResultString)) {
      throw new IdenticalFilesException(from.getPath() + ", " + to.getPath());
    }
    return new SideBySideDiffCreator(leftFile, rightFile).createFromDiffResult(diffResultString);
  }

  protected final TextFile getTextFile(final SVNConnection connection, final String path, final long revision,
                                       final String charset) throws SventonException, IOException {
    logger.debug("Fetching file " + path + "@" + revision);
    final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    getFileContents(connection, path, revision, outStream);
    return new TextFile(outStream.toString(charset));
  }

  protected void assertNotBinary(final SVNConnection connection, final PathRevision from, final PathRevision to,
                                 final Revision pegRevision) throws SventonException, IllegalFileFormatException {

    final boolean isLeftFileTextType;
    final boolean isRightFileTextType;
    if (Revision.UNDEFINED.equals(pegRevision)) {
      isLeftFileTextType = isTextFile(connection, from.getPath(), from.getRevision().getNumber());
      isRightFileTextType = isTextFile(connection, to.getPath(), to.getRevision().getNumber());
    } else {
      isLeftFileTextType = isTextFile(connection, from.getPath(), pegRevision.getNumber());
      isRightFileTextType = isTextFile(connection, to.getPath(), pegRevision.getNumber());
    }

    if (!isLeftFileTextType && !isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary files: " + from.getPath() + ", " + to.getPath());
    } else if (!isLeftFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + from.getPath());
    } else if (!isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + to.getPath());
    }
  }

  protected boolean isTextFile(SVNConnection connection, String path, long revision) throws SventonException {
    final String mimeType = listProperties(connection, path, revision).getStringValue(Property.MIME_TYPE);
    return (mimeType == null || mimeType.startsWith("text/"));
  }

  @Override
  public final List<InlineDiffRow> diffInline(final SVNConnection connection, final PathRevision from,
                                              final PathRevision to, final Revision pegRevision, final String charset)
      throws SventonException, DiffException {

    assertNotBinary(connection, from, to, pegRevision);

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      if (Revision.UNDEFINED.equals(pegRevision)) {
        leftFile = getTextFile(connection, from.getPath(), from.getRevision().getNumber(), charset);
        rightFile = getTextFile(connection, to.getPath(), to.getRevision().getNumber(), charset);
      } else {
        leftFile = getTextFile(connection, from.getPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(connection, to.getPath(), pegRevision.getNumber(), charset);
      }

      return InlineDiffCreator.createInlineDiff(from, to, charset, leftFile, rightFile);

    } catch (final IOException ioex) {
      throw new DiffException("Unable to produce inline diff", ioex);
    }
  }

  @Override
  public DirEntry.Kind getNodeKindForDiff(final SVNConnection connection, final PathRevision from,
                                          final PathRevision to, final Revision pegRevision)
      throws SventonException, DiffException {

    final long fromRevision;
    final long toRevision;

    if (!pegRevision.equals(Revision.UNDEFINED)) {
      fromRevision = pegRevision.getNumber();
      toRevision = pegRevision.getNumber();
    } else {
      fromRevision = from.getRevision().getNumber();
      toRevision = to.getRevision().getNumber();
    }

    final DirEntry.Kind nodeKind1;
    final DirEntry.Kind nodeKind2;
    try {
      nodeKind1 = getNodeKind(connection, from.getPath(), fromRevision);
    } catch (NoSuchRevisionException e) {
      throw new DiffException("Path [" + from.getPath() + "] does not exist as revision [" + fromRevision + "]");
    }
    try {
      nodeKind2 = getNodeKind(connection, to.getPath(), toRevision);
    } catch (NoSuchRevisionException e) {
      throw new DiffException("Path [" + to.getPath() + "] does not exist as revision [" + toRevision + "]");
    }

    assertFileOrDir(nodeKind1, from.getPath(), fromRevision);
    assertFileOrDir(nodeKind2, to.getPath(), toRevision);
    assertSameKind(nodeKind1, nodeKind2);
    return nodeKind1;
  }

  private void assertSameKind(final DirEntry.Kind nodeKind1, final DirEntry.Kind nodeKind2) throws DiffException {
    if (nodeKind1 != nodeKind2) {
      throw new DiffException("Entries are different kinds! " + nodeKind1 + "!=" + nodeKind2);
    }
  }

  private void assertFileOrDir(final DirEntry.Kind nodeKind, final String path, final long revision) throws DiffException {
    if (DirEntry.Kind.DIR != nodeKind && DirEntry.Kind.FILE != nodeKind) {
      throw new DiffException("Path [" + path + "] does not exist as revision [" + revision + "]");
    }
  }

  @Override
  public List<LogEntry> getLatestRevisions(RepositoryName repositoryName, SVNConnection connection, int revisionCount) throws SventonException {
    return getLogEntries(repositoryName, connection, Revision.HEAD.getNumber(), Revision.FIRST.getNumber(), "/", revisionCount, false, true);
  }
}

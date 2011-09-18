/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
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
import org.sventon.NoSuchRevisionException;
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.diff.*;
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
  protected final Log logger = LogFactory.getLog(getClass());


  @Override
  public List<SideBySideDiffRow> diffSideBySide(final SVNConnection connection, final PathRevision from,
                                                final PathRevision to, final Revision pegRevision, final String charset)
      throws SventonException {

    assertNotBinary(connection, from, to, pegRevision);

    try {
      final TextFile leftFile = getTextFile(connection, from.getPath(), getProperRevision(
          from.getRevision(), pegRevision), charset);
      final TextFile rightFile = getTextFile(connection, to.getPath(), getProperRevision(
          to.getRevision(), pegRevision), charset);
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

  @Override
  public final List<InlineDiffRow> diffInline(final SVNConnection connection, final PathRevision from,
                                              final PathRevision to, final Revision pegRevision, final String charset)
      throws SventonException {

    assertNotBinary(connection, from, to, pegRevision);

    try {
      final TextFile leftFile = getTextFile(connection, from.getPath(), getProperRevision(from.getRevision(), pegRevision), charset);
      final TextFile rightFile = getTextFile(connection, to.getPath(), getProperRevision(to.getRevision(), pegRevision), charset);
      return InlineDiffCreator.createInlineDiff(from, to, charset, leftFile, rightFile);
    } catch (final IOException ioex) {
      throw new DiffException("Unable to produce inline diff", ioex);
    }
  }

  @Override
  public List<LogEntry> getLatestRevisions(SVNConnection connection, RepositoryName repositoryName, int revisionCount) throws SventonException {
    return getLogEntries(connection, repositoryName, Revision.HEAD.getNumber(), Revision.FIRST.getNumber(), "/", revisionCount, false, true);
  }

  @Override
  public DirEntry.Kind getNodeKindForDiff(final SVNConnection connection, final PathRevision from,
                                          final PathRevision to, final Revision pegRevision)
      throws SventonException {

    final long fromRevisionNumber = getProperRevision(from.getRevision(), pegRevision).getNumber();
    final long toRevisionNumber = getProperRevision(to.getRevision(), pegRevision).getNumber();

    final DirEntry.Kind nodeKind1;
    final DirEntry.Kind nodeKind2;

    try {
      nodeKind1 = getNodeKind(connection, from.getPath(), fromRevisionNumber);
    } catch (NoSuchRevisionException e) {
      throw new DiffException("Path [" + from.getPath() + "] does not exist as revision [" + fromRevisionNumber + "]");
    }
    try {
      nodeKind2 = getNodeKind(connection, to.getPath(), toRevisionNumber);
    } catch (NoSuchRevisionException e) {
      throw new DiffException("Path [" + to.getPath() + "] does not exist as revision [" + toRevisionNumber + "]");
    }

    assertSameKind(nodeKind1, nodeKind2);
    return nodeKind1;
  }

  /**
   * @param targetRevision Target revision
   * @param pegRevision    Pegged
   * @return Target revision unless pegRevision is set to override.
   */
  protected Revision getProperRevision(final Revision targetRevision, final Revision pegRevision) {
    return Revision.UNDEFINED.equals(pegRevision) ? targetRevision : pegRevision;
  }

  protected final TextFile getTextFile(final SVNConnection connection, final String path, final Revision revision,
                                       final String charset) throws SventonException, IOException {
    logger.debug("Fetching file " + path + "@" + revision);
    final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    getFileContents(connection, path, revision.getNumber(), outStream);
    return new TextFile(outStream.toString(charset));
  }

  protected void assertNotBinary(final SVNConnection connection, final PathRevision from, final PathRevision to,
                                 final Revision pegRevision) throws SventonException {

    final boolean isLeftFileTextType = isTextFile(connection, from.getPath(), getProperRevision(from.getRevision(), pegRevision));
    final boolean isRightFileTextType = isTextFile(connection, to.getPath(), getProperRevision(to.getRevision(), pegRevision));

    if (!isLeftFileTextType && !isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary files: " + from.getPath() + ", " + to.getPath());
    } else if (!isLeftFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + from.getPath());
    } else if (!isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + to.getPath());
    }
  }

  private void assertSameKind(final DirEntry.Kind nodeKind1, final DirEntry.Kind nodeKind2) {
    if (!nodeKind1.equals(nodeKind2)) {
      throw new DiffException("Entries are different kinds: Cannot diff a " + nodeKind1 + " with a " + nodeKind2 + "!");
    }
  }

  protected boolean isTextFile(SVNConnection connection, String path, Revision revision) throws SventonException {
    final String mimeType = listProperties(connection, path, revision.getNumber()).getStringValue(Property.MIME_TYPE);
    return (mimeType == null || mimeType.startsWith("text/"));
  }

}

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
import org.sventon.NoSuchRevisionException;
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.diff.*;
import org.sventon.model.*;
import org.sventon.util.SVNUtils;
import org.sventon.web.command.DiffCommand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 */
public abstract class AbstractRepositoryService implements RepositoryService{
    /**
   * Logger for this class and subclasses.
   */
  final Log logger = LogFactory.getLog(getClass());

  
  @Override
  public List<SideBySideDiffRow> diffSideBySide(SVNConnection connection, DiffCommand command, Revision pegRevision, String charset) throws SventonException, DiffException {

    assertNotBinary(connection, command, pegRevision);

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      if (Revision.UNDEFINED.equals(pegRevision)) {
        leftFile = getTextFile(connection, command.getFromPath(), command.getFromRevision().getNumber(), charset);
        rightFile = getTextFile(connection, command.getToPath(), command.getToRevision().getNumber(), charset);
      } else {
        leftFile = getTextFile(connection, command.getFromPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(connection, command.getToPath(), pegRevision.getNumber(), charset);
      }
      return createSideBySideDiff(command, charset, leftFile, rightFile);
    } catch (IOException ioex) {
      throw new DiffException("Unable to produce unified diff", ioex);
    }
  }


  public  List<SideBySideDiffRow> createSideBySideDiff(DiffCommand command, String charset, TextFile leftFile, TextFile rightFile) throws IOException {
    final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
    final InputStream leftStream = new ByteArrayInputStream(leftFile.getContent().getBytes());
    final InputStream rightStream = new ByteArrayInputStream(rightFile.getContent().getBytes());
    final DiffProducer diffProducer = new DiffProducer(leftStream, rightStream, charset);

    diffProducer.doNormalDiff(diffResult);
    final String diffResultString = diffResult.toString(charset);

    if ("".equals(diffResultString)) {
      throw new IdenticalFilesException(command.getFromPath() + ", " + command.getToPath());
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

  protected void assertNotBinary(final SVNConnection connection, final DiffCommand command, final Revision pegRevision)
      throws SventonException, IllegalFileFormatException {

    final boolean isLeftFileTextType;
    final boolean isRightFileTextType;
    if (Revision.UNDEFINED.equals(pegRevision)) {
      isLeftFileTextType = isTextFile(connection, command.getFromPath(), command.getFromRevision().getNumber());
      isRightFileTextType = isTextFile(connection, command.getToPath(), command.getToRevision().getNumber());
    } else {
      isLeftFileTextType = isTextFile(connection, command.getFromPath(), pegRevision.getNumber());
      isRightFileTextType = isTextFile(connection, command.getToPath(), pegRevision.getNumber());
    }

    if (!isLeftFileTextType && !isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary files: " + command.getFromPath() + ", " + command.getToPath());
    } else if (!isLeftFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + command.getFromPath());
    } else if (!isRightFileTextType) {
      throw new IllegalFileFormatException("Cannot diff binary file: " + command.getToPath());
    }
  }

  protected boolean isTextFile(SVNConnection connection, String path, long revision) throws SventonException {
    final String mimeType = listProperties(connection, path, revision).getStringValue(Property.MIME_TYPE);
    return SVNUtils.isTextMimeType(mimeType);
  }

  @Override
  public final List<InlineDiffRow> diffInline(final SVNConnection connection, final DiffCommand command,
                                              final Revision pegRevision, final String charset)
      throws SventonException, DiffException {

    assertNotBinary(connection, command, pegRevision);

    try {
      final TextFile leftFile;
      final TextFile rightFile;

      if (Revision.UNDEFINED.equals(pegRevision)) {
        leftFile = getTextFile(connection, command.getFromPath(), command.getFromRevision().getNumber(), charset);
        rightFile = getTextFile(connection, command.getToPath(), command.getToRevision().getNumber(), charset);
      } else {
        leftFile = getTextFile(connection, command.getFromPath(), pegRevision.getNumber(), charset);
        rightFile = getTextFile(connection, command.getToPath(), pegRevision.getNumber(), charset);
      }

      return InlineDiffCreator.createInlineDiff(command, charset, leftFile, rightFile);

    } catch (final IOException ioex) {
      throw new DiffException("Unable to produce inline diff", ioex);
    }
  }

  @Override
  public DirEntry.Kind getNodeKindForDiff(final SVNConnection connection, final DiffCommand command)
      throws SventonException, DiffException {

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

    assertFileOrDir(nodeKind1, command.getFromPath(), fromRevision);
    assertFileOrDir(nodeKind2, command.getToPath(), toRevision);
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

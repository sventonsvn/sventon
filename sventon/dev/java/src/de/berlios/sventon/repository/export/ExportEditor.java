/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.export;

import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDeltaProcessor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ISVNEditor implementation that will add directories and files into
 * the target directory accordingly to update instructions sent by the server.
 *
 * @author jesper@users.berlios.de
 */
public class ExportEditor implements ISVNEditor {

  /**
   * Logger for this class and subclasses.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  private File rootDirectory;

  /**
   * Utility class that will help us to transform 'deltas' sent by the
   * server to the new files contents.
   */
  private final SVNDeltaProcessor deltaProcessor = new SVNDeltaProcessor();

  /**
   * Constructor.
   *
   * @param rootDirectory local directory where the node tree is to be exported into.
   */
  public ExportEditor(final File rootDirectory) {
    this.rootDirectory = rootDirectory;
  }

  /**
   * Server reports revision to which application of the further
   * instructions will update working copy to.
   *
   * @param revision
   * @throws SVNException
   */
  public void targetRevision(long revision) throws SVNException {
  }


  /**
   * Called before sending other instructions.
   *
   * @param revision
   * @throws SVNException
   */
  public void openRoot(long revision) throws SVNException {
  }

  /**
   * Called when a new directory has to be added.
   * <p/>
   * For each 'addDir' call server will call 'closeDir' method after
   * all children of the added directory are added.
   * <p/>
   * This implementation creates corresponding directory below root directory.
   *
   * @param path             Path
   * @param copyFromPath     Path
   * @param copyFromRevision Revision
   * @throws SVNException if a subversion error occurs.
   */
  public void addDir(final String path, final String copyFromPath, final long copyFromRevision) throws SVNException {
    final File newDir = new File(rootDirectory, path);
    if (!newDir.exists()) {
      if (!newDir.mkdirs()) {
        final SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "error: failed to add the directory ''{0}''.", newDir);
        throw new SVNException(err);
      }
    }
  }

  /**
   * Called when there is an existing directory that has to be 'opened' either
   * to modify this directory properties or to process other files and directories
   * inside this directory.
   * <p/>
   * In case of export this method will never be called because we reported
   * that our 'working copy' is empty and so server knows that there are
   * no 'existing' directories.
   *
   * @param path     Path
   * @param revision revision
   * @throws SVNException if a subversion error occurs.
   */
  public void openDir(final String path, final long revision) throws SVNException {
  }

  /**
   * Instructs to change opened or added directory property.
   * <p/>
   * This method is called to update properties set by the user as well
   * as those created automatically, like "svn:committed-rev".
   * See SVNProperty class for default property names.
   * <p/>
   * When property has to be deleted value will be 'null'.
   *
   * @param name  Name
   * @param value Value
   * @throws SVNException if a subversion error occurs.
   */
  public void changeDirProperty(final String name, final String value) throws SVNException {
  }

  /**
   * Called when a new file has to be created.
   * <p/>
   * For each 'addFile' call server will call 'closeFile' method after
   * sending file properties and contents.
   * <p/>
   * This implementation creates empty file below root directory, file contents
   * will be updated later, and for empty files may not be sent at all.
   *
   * @param path             Path
   * @param copyFromPath     from path
   * @param copyFromRevision from revision
   * @throws SVNException if a subversion error occurs.
   */
  public void addFile(final String path, final String copyFromPath, final long copyFromRevision) throws SVNException {
    final File file = new File(rootDirectory, path);
    if (file.exists()) {
      SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "error: exported file ''{0}'' already exists!", file);
      throw new SVNException(err);
    }
    try {
      file.createNewFile();
    } catch (IOException e) {
      final SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "error: cannot create new  file ''{0}''", file);
      throw new SVNException(err);
    }
  }

  /**
   * Called when there is an existing files that has to be 'opened' either
   * to modify file contents or properties.
   * <p/>
   * In case of export this method will never be called because we reported
   * that our 'working copy' is empty and so server knows that there are
   * no 'existing' files.
   *
   * @param path     Path
   * @param revision Revision
   * @throws SVNException if a subversion error occurs.
   */
  public void openFile(final String path, final long revision) throws SVNException {
  }

  /**
   * Instructs to add, modify or delete file property.
   * In this example we skip this instruction, but 'real' export operation
   * may inspect 'svn:eol-style' or 'svn:mime-type' property values to
   * transfor file contents propertly after receiving.
   *
   * @param path  Path
   * @param name  Name
   * @param value Value
   * @throws SVNException if a subversion error occurs.
   */
  public void changeFileProperty(final String path, final String name, final String value) throws SVNException {
  }

  /**
   * Called before sending 'delta' for a file. Delta may include instructions
   * on how to create a file or how to modify existing file. In this example
   * delta will always contain instructions on how to create a new file and so
   * we set up deltaProcessor with 'null' base file and target file to which we would
   * like to store the result of delta application.
   *
   * @param path         Path
   * @param baseChecksum checksum
   * @throws SVNException if a subversion error occurs.
   */
  public void applyTextDelta(final String path, final String baseChecksum) throws SVNException {
    deltaProcessor.applyTextDelta(null, new File(rootDirectory, path), false);
  }

  /**
   * Server sends deltas in form of 'diff windows'. Depending on the file size
   * there may be several diff windows. Utility class SVNDeltaProcessor process
   * these windows for us.
   *
   * @param path       Path
   * @param diffWindow DiffWindow
   * @return Dummy stream - for compatibility only
   * @throws SVNException if a subversion error occurs.
   */
  public OutputStream textDeltaChunk(final String path, final SVNDiffWindow diffWindow) throws SVNException {
    return deltaProcessor.textDeltaChunk(diffWindow);
  }

  /**
   * Called when all diff windows (delta) is transferred.
   *
   * @param path Path
   * @throws SVNException if a subversion error occurs.
   */
  public void textDeltaEnd(final String path) throws SVNException {
    deltaProcessor.textDeltaEnd();
  }

  /**
   * Called when file update is completed.
   * This call always matches addFile or openFile call.
   *
   * @param path         Path
   * @param textChecksum checksum
   * @throws SVNException if a subversion error occurs.
   */
  public void closeFile(final String path, final String textChecksum) throws SVNException {
    logger.debug("Exported: " + path);
  }


  /**
   * Called when all child files and directories are processed.
   * This call always matches addDir, openDir or openRoot call.
   *
   * @throws SVNException if a subversion error occurs.
   */
  public void closeDir() throws SVNException {
  }

  /**
   * Insturcts to delete an entry in the 'working copy'. Of course will not be
   * called during export operation.
   *
   * @param path     Path
   * @param revision Revision
   * @throws SVNException if a subversion error occurs.
   */
  public void deleteEntry(final String path, final long revision) throws SVNException {
  }

  /**
   * Called when directory at 'path' should be somehow processed,
   * but authenticated user (or anonymous user) doesn't have enough
   * access rights to get information on this directory (properties, children).
   *
   * @param path Path
   * @throws SVNException if a subversion error occurs.
   */
  public void absentDir(final String path) throws SVNException {
  }

  /**
   * Called when file at 'path' should be somehow processed,
   * but authenticated user (or anonymous user) doesn't have enough
   * access rights to get information on this file (contents, properties).
   *
   * @param path Path
   * @throws SVNException if a subversion error occurs.
   */
  public void absentFile(final String path) throws SVNException {
  }

  /**
   * Called when update is completed.
   *
   * @return Always null
   * @throws SVNException if a subversion error occurs.
   */
  public SVNCommitInfo closeEdit() throws SVNException {
    return null;
  }

  /**
   * Called when update is completed with an error or server
   * requests client to abort update operation.
   *
   * @throws SVNException if a subversion error occurs.
   */
  public void abortEdit() throws SVNException {
  }

}

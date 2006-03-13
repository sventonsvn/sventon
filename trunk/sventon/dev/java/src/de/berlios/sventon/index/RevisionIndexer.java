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
package de.berlios.sventon.index;

import de.berlios.sventon.ctrl.RepositoryConfiguration;
import de.berlios.sventon.ctrl.RepositoryEntry;
import de.berlios.sventon.svnsupport.LogEntryActionType;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.util.PathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.*;
import java.util.*;

/**
 * RevisionIndexer.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionIndexer {

  /**
   * The revision index.
   */
  private RevisionIndex index;

  /**
   * The repository instance to index.
   */
  private SVNRepository repository;

  /**
   * The repository configuration.
   */
  private RepositoryConfiguration configuration;

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  private boolean isIndexing = false;

  /**
   * The index file name, <tt>sventon.idx</tt>.
   */
  public static final String INDEX_FILENAME = "sventon.idx";

  /**
   * Constructs the index instance using a given repository.
   * The repository must be correctly initialized and credentials
   * must have been applied (if required by server).
   *
   * @param repository The repository instance
   */
  public RevisionIndexer(final SVNRepository repository) {
    logger.debug("Creating index instance using given repository");
    this.repository = repository;
  }

  /**
   * Constructs the index instance using a given repository configuration.
   * If a serialized index is stored on disk it will be read into memory.
   * Otherwise a complete repository indexing will be executed.
   *
   * @param configuration The repository configuration
   */
  public RevisionIndexer(final RepositoryConfiguration configuration) {
    logger.debug("Creating index instance using given configuration");
    setRepositoryConfiguration(configuration);
  }

  /**
   * Sets the repository configuration.
   *
   * @param configuration Configuration.
   */
  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * @return Returns the number of indexed repository items.
   */
  public long getIndexCount() {
    return index.getUnmodifiableEntries().size();
  }

  /**
   * Indexes the files and directories.
   *
   * @throws SVNException if a Subversion error occurs.
   */
  protected synchronized void populateIndex() throws SVNException {
    logger.info("Populating index");
    index = new RevisionIndex(configuration.getUrl());
    logger.debug("Index url: " + index.getUrl());
    long head = repository.getLatestRevision();
    logger.debug("Revision (head): " + head);
    populateIndex("/", head);
    index.setIndexRevision(head);
    logger.info("Number of indexed entries: " + getIndexCount());
  }

  /**
   * Updates the index to HEAD revision.
   * A Subversion <i>log</i> command will be performed and
   * the index will be updated accordingly.
   * <table>
   * <tr><th>Type</th><th>Description</th><th>Affects index</th></tr>
   * <tr><td>'A'</td><td>Added</td><td>Entry is added</td></tr>
   * <tr><td>'D'</td><td>Deleted</td><td>Entry is removed</td></tr>
   * <tr><td>'M'</td><td>Modified</td><td>Entry info is updated</td></tr>
   * <tr><td>'R'</td><td>Replaced (what means that the
   * object is first deleted, then another object of the same name is
   * added, all within a single revision)</td><td>Entry info is updated</td></tr>
   * </table>
   *
   * @throws SVNException if Subversion error occurs.
   */
  @SuppressWarnings("unchecked")
  protected synchronized void updateIndex() throws SVNException {
    String[] targetPaths = new String[]{"/"}; // the path to log
    long latestRevision = repository.getLatestRevision();

    logger.info("Updating index from revision " + index.getIndexRevision() + " to "
        + latestRevision);

    List<SVNLogEntry> logEntries = (List<SVNLogEntry>) repository.log(targetPaths,
        null, index.getIndexRevision() + 1, latestRevision, true, false);

    // One logEntry is one commit (or revision)
    for (SVNLogEntry logEntry : logEntries) {
      long revision = logEntry.getRevision();
      logger.debug("Applying changes from revision " + revision + " to index");
      Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
      List<String> latestPathsList = new ArrayList<String>(map.keySet());
      // Sort the entries to apply changes in right order
      Collections.sort(latestPathsList);
      for (String entryPath : latestPathsList) {
        SVNLogEntryPath logEntryPath = map.get(entryPath);
        switch (LogEntryActionType.valueOf(String.valueOf(logEntryPath.getType()))) {
          case A :
            logger.debug("Adding entry to index: " + logEntryPath.getPath() + " - rev: " + logEntry.getRevision());
            doIndexAdd(logEntryPath, revision);
            break;
          case D :
            logger.debug("Removing deleted entry from index: " + logEntryPath.getPath() + " - rev: " + logEntry.getRevision());
            doIndexDelete(logEntryPath, revision);
            break;
          case R :
            logger.debug("Replacing entry in index: " + logEntryPath.getPath() + " - rev: " + logEntry.getRevision());
            doIndexReplace(logEntryPath, revision);
            break;
          case M :
            logger.debug("Updating modified entry in index: " + logEntryPath.getPath() + " - rev: " + logEntry.getRevision());
            doIndexModify(logEntryPath, revision);
            break;
          default :
            throw new RuntimeException("Unknown log entry type: " + logEntryPath.getType() + " in rev " + logEntry.getRevision());
        }
      }
    }
    index.setIndexRevision(latestRevision);
  }

  /**
   * Modifies an entry (file or directory) in the index.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doIndexModify(SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    index.remove(logEntryPath.getPath(), false);
    index.add(new RepositoryEntry(
        repository.info(logEntryPath.getPath(), revision),
        PathUtil.getPathPart(logEntryPath.getPath()), null));
  }

  /**
   * Replaces an entry (file or directory) in the index.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doIndexReplace(SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    doIndexModify(logEntryPath, revision);
  }

  /**
   * Deletes an entry (file or directory) from the index.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doIndexDelete(SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    // Have to find out if deleted entry was a file or directory
    SVNDirEntry deletedEntry = repository.info(logEntryPath.getPath(), revision - 1);
    if (RepositoryEntry.Kind.valueOf(deletedEntry.getKind().toString()) == RepositoryEntry.Kind.dir) {
      // Directory node deleted
      logger.debug(logEntryPath.getPath() + " is a directory. Doing a recursive delete");
      index.remove(logEntryPath.getPath(), true);
    } else {
      // Single entry delete
      index.remove(logEntryPath.getPath(), false);
    }
  }

  /**
   * Adds an entry (file or directory) to the index.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doIndexAdd(SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    // Have to find out if added entry was a file or directory
    SVNDirEntry addedEntry = repository.info(logEntryPath.getPath(), revision);

    // If the entry is a directory and a copyPath exists the entry is
    // a moved or copied directory (branch). In that case we have to recursively
    // add the entry. If entry is a directory but does not have a copyPath
    // the contents will be added one by one as single entries.
    if (RepositoryEntry.Kind.valueOf(addedEntry.getKind().toString()) == RepositoryEntry.Kind.dir
        && logEntryPath.getCopyPath() != null) {
      // Directory node added
      logger.debug(logEntryPath.getPath() + " is a directory. Doing a recursive add");
      index.add(new RepositoryEntry(addedEntry, PathUtil.getPathPart(logEntryPath.getPath()), null));
      // Add directory contents
      populateIndex(logEntryPath.getPath() + "/", revision);
    } else {
      // Single entry added
      index.add(new RepositoryEntry(addedEntry, PathUtil.getPathPart(logEntryPath.getPath()), null));
    }
  }

  /**
   * Updates the index to current HEAD revision.
   * <p/>
   * If the repository URL has changed in the configuration properties
   * or the index does not contain any entries or if the repository HEAD revision
   * is <i>lower</i> than the indexed revision, a complete indexing
   * will be performed.
   * <p/>
   * If the repository revision is greater than the revision of the
   * index, the index will be updated to reflect HEAD revision.
   * <p/>
   * If sventon has entered configuration mode, i.e. the repository
   * connection cannot be established yet, the method will return without
   * any action performed.
   *
   * @throws SVNException           if Subversion error occurs.
   * @throws RevisionIndexException if an index error occurs.
   */
  public synchronized void update() throws RevisionIndexException, SVNException {

    if (!isConnectionEstablished()) {
      return;
    }

    assertIndexIsInitialized();

    isIndexing = true;
    try {
      if (getIndexCount() == 0 || !index.getUrl().equals(configuration.getUrl()) ||
          index.getIndexRevision() > repository.getLatestRevision()) {
        // index is just created and does not contain any entries
        // or the repository URL has changed in the config properties
        // or the repository revision is LOWER than the index revision
        // do a full repository indexing
        populateIndex();
        storeIndex(configuration.getSVNConfigurationPath() + INDEX_FILENAME);
      } else if (index.getIndexRevision() < repository.getLatestRevision()) {
        // index is out-of-date
        // update it to reflect HEAD revision
        updateIndex();
        storeIndex(configuration.getSVNConfigurationPath() + INDEX_FILENAME);
      }
    } finally {
      isIndexing = false;
    }
  }

  /**
   * Checks if the repository connection is properly initialized.
   * If not, a connection will be created.
   *
   * @throws SVNException If an error occur during the repository connection creation.
   */
  private boolean isConnectionEstablished() throws SVNException {
    if (repository == null) {
      logger.debug("Trying to establish the repository connection");
      repository = RepositoryFactory.INSTANCE.getRepository(configuration);
      if (repository == null) {
        logger.info("Repository not configured yet. Waiting with index creation");
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the index is properly initialized.
   * If not, the index will be loaded from disk.
   * If no index exists an empty one will be created.
   *
   * @throws RevisionIndexException if unable to load index.
   */
  private void assertIndexIsInitialized() throws RevisionIndexException {
    if (index == null) {
      logger.debug("Initializing index");
      logger.info("Reading serialized index from disk, "
          + configuration.getSVNConfigurationPath()
          + INDEX_FILENAME);

      File indexFile = new File(configuration.getSVNConfigurationPath() + INDEX_FILENAME);
      ObjectInputStream in;
      if (indexFile.exists()) {
        try {
          in = new ObjectInputStream(new FileInputStream(indexFile));
          index = (RevisionIndex) in.readObject();
        } catch (Exception ex) {
          throw new RevisionIndexException("Unable to read index file", ex);
        }
      } else {
        // No serialized index exsisted - initialize an empty one.
        index = new RevisionIndex(configuration.getUrl());
      }
    }
  }

  /**
   * Checks if the index is being updated.
   *
   * @return <code>True</code> is index is being updated, <code>false</code> if not.
   */
  public boolean isIndexing() {
    logger.debug("isIndexing: " + isIndexing);
    return isIndexing;
  }

  /**
   * Populates the index by getting all entries in given path
   * and adding them to the index. This method will be recursively
   * called by <code>index()</code>.
   *
   * @param path The path to add to index.
   * @throws SVNException if a Subversion error occurs.
   */
  @SuppressWarnings("unchecked")
  private void populateIndex(final String path, final long revision) throws SVNException {
    List<SVNDirEntry> entriesList = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);

    entriesList.addAll(repository.getDir(path, revision, null, (Collection) null));
    for (SVNDirEntry entry : entriesList) {
      RepositoryEntry newEntry = new RepositoryEntry(entry, path, null);
      if (!index.add(newEntry)) {
        logger.warn("Unable to add already existing entry to index: " + newEntry.toString());
      }
      if (entry.getKind() == SVNNodeKind.DIR) {
        populateIndex(path + entry.getName() + "/", revision);
      }
    }
  }

  /**
   * Finds index entries by a search string. Case is ignored.
   *
   * @param searchString The string to search for.
   * @param startDir     The directory where to start search from.
   * @return The <code>List</code> of <code>RepositoryEntry</code> instances found.
   * @throws RevisionIndexException if an index error occurs.
   * @see de.berlios.sventon.ctrl.RepositoryEntry
   */
  public List<RepositoryEntry> find(final String searchString, final String startDir) throws RevisionIndexException {
    if (searchString == null || searchString.equals("")) {
      throw new IllegalArgumentException("Search string was null or empty");
    }

    if (startDir == null) {
      throw new IllegalArgumentException("startDir was null");
    }

    try {
      update();
    } catch (SVNException svnex) {
      throw new RevisionIndexException("Error during index update", svnex);
    }

    String lcStartDir = startDir.toLowerCase();
    String lcSearchString = searchString.toLowerCase();

    List<RepositoryEntry> result = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    for (RepositoryEntry entry : index.getUnmodifiableEntries()) {
      String name = entry.getFullEntryName().toLowerCase();
      if (name.startsWith(lcStartDir) && name.contains(lcSearchString)) {
        result.add(entry);
      }
    }
    logger.debug("Found " + result.size() + " entries matching search: " + searchString);
    return result;
  }

  /**
   * Finds index entries by a search string.
   *
   * @param searchPattern The regex pattern to search for.
   * @param startDir      The start in directory. Search will be performed in this directory and below.
   * @return The <code>List</code> of <code>RepositoryEntry</code> instances found.
   * @throws RevisionIndexException if an index error occurs.
   * @see java.util.regex.Pattern
   */
  public List<RepositoryEntry> findPattern(final String searchPattern, final String startDir) throws RevisionIndexException {
    return findPattern(searchPattern, startDir, null);
  }


  /**
   * Finds index entries by a search string.
   *
   * @param searchPattern The regex pattern to search for.
   * @param startDir      The start in directory. Search will be performed in this directory and below.
   * @param limit         The limit, maximum number of rows returned, <code>null</code> if no limit wanted.
   * @return The <code>List</code> of <code>RepositoryEntry</code> instances found.
   * @throws RevisionIndexException if an index error occurs.
   * @see java.util.regex.Pattern
   */
  public List<RepositoryEntry> findPattern(final String searchPattern,
                                           final String startDir,
                                           final Integer limit) throws RevisionIndexException {
    if (searchPattern == null || searchPattern.equals("")) {
      throw new IllegalArgumentException("Search string was null or empty");
    }

    if (startDir == null) {
      throw new IllegalArgumentException("startDir was null");
    }

    try {
      update();
    } catch (SVNException svnex) {
      throw new RevisionIndexException("Error during index update", svnex);
    }

    int count = 0;
    List<RepositoryEntry> result = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    for (RepositoryEntry entry : index.getUnmodifiableEntries()) {
      if (entry.getFullEntryName().startsWith(startDir) && entry.getFullEntryName().matches(searchPattern)) {
        result.add(entry);
        if (limit != null && ++count == limit) {
          break;
        }
      }
    }
    logger.debug("Found " + result.size() + " entries matching search pattern: " + searchPattern);
    return result;
  }

  /**
   * Finds all subdirectory entries below given <code>fromPath</code>.
   *
   * @param fromPath The base path to start from.
   * @return A list containing all subdirectory entries below <code>fromPath</code>.
   * @throws RevisionIndexException if an index error occurs.
   */
  public List<RepositoryEntry> findDirectories(final String fromPath) throws RevisionIndexException {
    if (fromPath == null || fromPath.equals("")) {
      throw new IllegalArgumentException("Path was null or empty");
    }

    try {
      update();
    } catch (SVNException svnex) {
      throw new RevisionIndexException("Error during index update", svnex);
    }

    List<RepositoryEntry> result = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    for (RepositoryEntry entry : index.getUnmodifiableEntries()) {
      if (RepositoryEntry.Kind.dir == entry.getKind() && entry.getFullEntryName().startsWith(fromPath)) {
        result.add(entry);
      }
    }
    logger.debug("Found " + result.size() + " directories below: " + fromPath);
    return result;
  }

  /**
   * Dumps the entire index to STDOUT.
   */
  public void dumpIndex() {
    logger.info("Dumping index to STDOUT...");
    for (RepositoryEntry repositoryEntry : index.getUnmodifiableEntries()) {
      System.out.println(repositoryEntry);
    }
  }

  /**
   * Stores the current index to disk using given path and file name.
   * The index will not be stored if it does not contain any entries.
   *
   * @param storagePathAndName Full path and name where to store index file.
   * @throws RevisionIndexException if an index error occurs.
   */
  public void storeIndex(final String storagePathAndName) throws RevisionIndexException {
    if (index != null && index.getUnmodifiableEntries().size() > 0) {
      logger.info("Saving index to disk, " + storagePathAndName);
      ObjectOutputStream out;
      try {
        out = new ObjectOutputStream(new FileOutputStream(storagePathAndName));
        out.writeObject(index);
        out.flush();
        out.close();
      } catch (IOException ioex) {
        throw new RevisionIndexException("Unable to store index to disk", ioex);
      }
    } else {
      logger.info("Index does not contain any entries and will not be stored on disk");
    }
  }

  /**
   * This method serializes the index to disk.
   */
  public void destroy() {
    if (configuration != null) {
      try {
        storeIndex(configuration.getSVNConfigurationPath() + INDEX_FILENAME);
      } catch (RevisionIndexException re) {
        logger.warn(re);
      }
    }
  }

}

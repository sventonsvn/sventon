package de.berlios.sventon.index;

import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * The revision index keeps all repository entries cached for fast repository searching.
 * @author jesper@users.berlios.de
 */
public class RevisionIndex {

  /** The index. */
  private List<IndexEntry> index;

  /** The repository instance to index. */
  private SVNRepository repository;

  /** Path specifying root of index. */
  private String startPath;

  /** Current indexed revision. */
  private long indexRevision = 0;

  /** The logging instance. */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Default constructor.
   */
  public RevisionIndex() {
    logger.debug("Creating index instance.");
    init();
  }

  public RevisionIndex(final String startPath) {
    logger.debug("Creating index instance, start path: " + startPath);
    init();
    this.setStartPath(startPath);
  }

  private void init() {
    logger.debug("Initializing index");
    index = Collections.checkedList(new ArrayList<IndexEntry>(), IndexEntry.class);
    // TODO: Read serialized index from disk.
    // TODO: Update the index according to what's new.
  }

  /**
   * Sets the repository.
   * @param repository
   */
  public void setRepository(final SVNRepository repository) {
    logger.debug("Setting repository");
    this.repository = repository;
  }

  /**
   * Gets the current index revision.
   * @return The revision.
   * @throws SVNException if a Subversion error occurs.
   */
  public long getIndexRevision() throws SVNException {
    logger.debug("Current indexed revision: " + this.indexRevision);
    return this.indexRevision;
  }

  /**
   * Checks if index is up to date.
   * @return <code>True</code> if index revision is same as HEAD, i.e. latest revision.
   * @throws SVNException if a Subversion error occurs.
   */
  public boolean isLatestRevision() throws SVNException {
    return this.repository.getLatestRevision() == this.indexRevision;
  }

  /**
   * Sets the start path from where the repository
   * shall be indexed.
   * @param path The start path, e.g <code>&quot;/&quot;</code> or <code>&quot;/trunk/&quot;</code>
   */
  public void setStartPath(final String path) {
    if (path == null || path.equals("")) {
      throw new IllegalArgumentException("Path cannot be null or empty. Path must at least be set to '/'");
    }

    this.startPath = path;
    // Make sure there's a trailing slash.
    if (!path.endsWith("/")) {
      this.startPath += "/";
    }
  }

  /**
   * Indexes the files and directories, starting at the path
   * specified by calling <code>setStartPath()</code>.
   * @throws SVNException if a Subversion error occurs.
   */
  public void index() throws SVNException {
    logger.info("Building index");
    clearIndex();
    this.indexRevision = this.repository.getLatestRevision();
    populateIndex(startPath);
    logger.info("Number of indexed entries: " + index.size());
  }

  /**
   * Populates the index by getting all entries in given path
   * and adding them to the index. This method will be recursively
   * called by <code>index()</code>.
   * @param path The path to add to index.
   * @throws SVNException if a Subversion error occurs.
   */
  @SuppressWarnings("unchecked")
  private void populateIndex(final String path) throws SVNException {
    List<SVNDirEntry> entriesList = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);

    entriesList.addAll(repository.getDir(path, this.indexRevision, new HashMap(), (Collection) null));
    for (SVNDirEntry entry : entriesList) {
      index.add(new IndexEntry(entry, path));
      if (entry.getKind() == SVNNodeKind.DIR) {
        populateIndex(path + entry.getName() + "/");
      }
    }
  }

  /**
   * Gets the interator for index access.
   * @return The iterator.
   */
  public Iterator<IndexEntry> getEntries() {
    return index.iterator();
  }

  /**
   * Finds index entries by a search string.
   * @param searchString The string to search for.
   * @return The <code>List</code> of <code>IndexEntry</code> instances found.
   * @see de.berlios.sventon.index.IndexEntry
   * @throws SVNException if a Subverions error occurs.
   */
  public List<IndexEntry> find(final String searchString) throws SVNException {
    if (searchString == null || searchString.equals("")) {
      throw new IllegalArgumentException("Search string was null or empty");
    }

    //TODO: Temp fix until index refreshing code is added.
    if (!isLatestRevision()) {
      index();
    }

    List<IndexEntry> result = Collections.checkedList(new ArrayList<IndexEntry>(), IndexEntry.class);
    for (IndexEntry entry : index) {
      if (entry.getFullEntryName().indexOf(searchString) > -1) {
        result.add(entry);
      }
    }
    logger.debug("Found " + result.size() + " entries matching search: " + searchString);
    return result;
  }

  /**
   * Finds index entries by a search string.
   * @see java.util.regex.Pattern
   * @param searchPattern The regex pattern to search for.
   * @return The <code>List</code> of entries found.
   * @throws SVNException if a Subverions error occurs.
   */
  public List findPattern(final String searchPattern) throws SVNException {
    if (searchPattern == null || searchPattern.equals("")) {
      throw new IllegalArgumentException("Search string was null or empty");
    }

    //TODO: Temp fix until index refreshing code is added.
    if (!isLatestRevision()) {
      index();
    }

    List<IndexEntry> result = Collections.checkedList(new ArrayList<IndexEntry>(), IndexEntry.class);
    for (IndexEntry entry : index) {
      if (entry.getFullEntryName().matches(searchPattern)) {
        result.add(entry);
      }
    }
    logger.debug("Found " + result.size() + " entries matching search: " + searchPattern);
    return result;
  }

  /**
   * Clears the index.
   */
  public void clearIndex() {
    logger.debug("Clearing index");
    index.clear();
  }

  /**
   * @return Returns the number of indexed repository items.
   */
  public long getIndexCount() {
    return index.size();
  }

  /**
   * Gets all subdirectory entries below given <code>fromPath</code>.
   * @param fromPath The base path to start from.
   * @return A list containing all subdirectory entries below <code>fromPath</code>.
   * @throws SVNException if a Subverions error occurs.
   */
  public List<IndexEntry> getDirectories(final String fromPath) throws SVNException {
    if (fromPath == null || fromPath.equals("")) {
      throw new IllegalArgumentException("Path was null or empty.");
    }

    //TODO: Temp fix until index refreshing code is added.
    if (!isLatestRevision()) {
      index();
    }

    List<IndexEntry> result = Collections.checkedList(new ArrayList<IndexEntry>(), IndexEntry.class);
    for (IndexEntry entry : index) {
      if (entry.getEntry().getKind() == SVNNodeKind.DIR && entry.getFullEntryName().startsWith(fromPath)) {
        result.add(entry);
      }
    }
    logger.debug("Found " + result.size() + " directories below: " + fromPath);
    return result;
  }

  public void destroy() {
    logger.info("Saving index to disk");
    // TODO: Serialize index to disk
  }
}

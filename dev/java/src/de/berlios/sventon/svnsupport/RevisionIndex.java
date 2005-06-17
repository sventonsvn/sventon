package de.berlios.sventon.svnsupport;

import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNDirEntry;
import org.tmatesoft.svn.core.io.SVNNodeKind;

import java.util.*;

/**
 * The revision index keeps all repository entries cached for fast repository searching.
 * @author jesper@users.berlios.de
 */
public class RevisionIndex {

  /** The index. */
  private List<SVNDirEntry> index;

  /** The repository instance to index. */
  private SVNRepository repository;

  /** Path specifying root of index. */
  private String startPath;

  /** Current indexed revision. */
  private long indexRevision = 0;

  /**
   * Default constructor.
   */
  public RevisionIndex() {
    index = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);
  }

  /**
   * Sets the repository.
   * @param repository
   */
  public void setRepository(final SVNRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets the current index revision.
   * @return The revision.
   * @throws SVNException if a Subversion error occurs.
   */
  public long getIndexRevision() throws SVNException {
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
      throw new IllegalArgumentException("Path cannot be null or empty. Path must at least be set to '/'.");
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
    clearIndex();
    this.indexRevision = this.repository.getLatestRevision();
    populateIndex(startPath);
  }

  /**
   * Populates the index by getting all entries in given path
   * and adding them to the index. This method will be recursively
   * called by <code>index()</code>.
   * @param path The path to add to index.
   * @throws SVNException if a Subversion error occurs.
   */
  private void populateIndex(final String path) throws SVNException {
    List<SVNDirEntry> entriesList = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);

    entriesList.addAll(repository.getDir(path, getIndexRevision(), new HashMap(), new ArrayList()));
    Iterator entries = entriesList.iterator();
    while (entries.hasNext()) {
      SVNDirEntry entry = (SVNDirEntry) entries.next();
      index.add(entry);
      if (entry.getKind() == SVNNodeKind.DIR) {
        populateIndex(path + entry.getName() + "/");
      }
    }
  }

  /**
   * Gets the interator for index access.
   * @return The iterator.
   */
  public Iterator getEntries() {
    return index.iterator();
  }

  /**
   * Finds index entries by a search string.
   * @param searchString The string to search for.
   * @return The <code>List</code> of entries found.
   * @throws SVNException if a Subverions error occurs.
   */
  public List find(final String searchString) throws SVNException {
    if (searchString == null || searchString.equals("")) {
      throw new IllegalArgumentException("Search string was null or empty.");
    }

    //TODO: Temp fix until index refreshing code is added.
    if (!isLatestRevision()) {
      index();
    }

    List<SVNDirEntry> result = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);
    Iterator indexIterator = index.iterator();
    while (indexIterator.hasNext()) {
      SVNDirEntry entry = (SVNDirEntry) indexIterator.next();
      if (entry.getName().indexOf(searchString) > -1) {
        result.add(entry);
      }
    }
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
      throw new IllegalArgumentException("Search string was null or empty.");
    }

    //TODO: Temp fix until index refreshing code is added.
    if (!isLatestRevision()) {
      index();
    }

    List<SVNDirEntry> result = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);
    Iterator indexIterator = index.iterator();
    while (indexIterator.hasNext()) {
      SVNDirEntry entry = (SVNDirEntry) indexIterator.next();
      if (entry.getName().matches(searchPattern)) {
        result.add(entry);
      }
    }
    return result;
  }

  /**
   * Clears the index.
   */
  public void clearIndex() {
    index.clear();
  }
}

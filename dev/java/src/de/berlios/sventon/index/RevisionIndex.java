package de.berlios.sventon.index;

import de.berlios.sventon.ctrl.RepositoryEntry;
import de.berlios.sventon.svnsupport.RepositoryEntryComparator;
import static de.berlios.sventon.svnsupport.RepositoryEntryComparator.NAME;
import java.io.Serializable;
import java.util.*;

/**
 * The revision index keeps all repository entries cached for fast repository searching.
 * The RevisionIndex will be serialized to disk on exit and loaded on startup by
 * RevisionIndexer.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionIndex implements Serializable {

  private static final long serialVersionUID = -6606585955661688509L;

  /**
   * The index.
   */
  private Set<RepositoryEntry> index;

  /**
   * Current indexed revision.
   */
  private long indexRevision = 0;

  /**
   * Indexed URL.
   */
  private String url = "";

  /**
   * Constructor.
   * 
   * @param url The url to index.
   */
  public RevisionIndex(final String url) {
    index = Collections.checkedSet(new TreeSet<RepositoryEntry>(new RepositoryEntryComparator(NAME, false)), RepositoryEntry.class);
    this.url = url;
  }

  /**
   * Gets the current index revision.
   *
   * @return The revision.
   */
  public long getIndexRevision() {
    return this.indexRevision;
  }

  /**
   * Sets the index revision.
   *
   * @param revision The revision
   */
  public void setIndexRevision(final long revision) {
    this.indexRevision = revision;
  }

  /**
   * Clears the index.
   */
  public void clearIndex() {
    index.clear();
  }

  /**
   * Gets the index entries.
   *
   * @return The index entries.
   */
  public Set<RepositoryEntry> getEntries() {
    return index;
  }

  /**
   * Adds an entry to the index.
   *
   * @param entry Entry to add
   */
  public void add(final RepositoryEntry entry) {
    index.add(entry);
  }

  /**
   * Gets the index url.
   *
   * @return The index url
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * Removes an entry from the index.
   * The index will be scanned for the given
   * path and remove the entry if found.
   *
   * @param path The full path to the entry to remove.
   */
  public void remove(String path) {
    for (RepositoryEntry entry : index) {
      if (entry.getFullEntryName().equals(path)) {
        index.remove(entry);
        return;
      }
    }
  }

}

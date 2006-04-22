package de.berlios.sventon.repository.cache;

import static de.berlios.sventon.repository.RepositoryEntry.Kind.any;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.RepositoryEntryComparator;

import java.util.*;

/**
 * Contains a cached set of the repository entries for a specific revision.
 * This class is a data holder, with various finder methods.
 *
 * @author jesper@users.berlios.de
 */
public class DiskPersistentRepositoryEntryCache implements RepositoryEntryCache {

  /**
   * The index.
   */
  private Set<RepositoryEntry> cachedEntries;

  /**
   * Current indexed revision.
   */
  private long cachedRevision = 0;

  /**
   * Indexed URL.
   */
  private String repositoryURL;


  /**
   * Constructor
   *
   * @param repositoryURL Url to repository
   */
  public DiskPersistentRepositoryEntryCache(final String repositoryURL) {
    cachedEntries = Collections.checkedSet(
        new TreeSet<RepositoryEntry>(new RepositoryEntryComparator(RepositoryEntryComparator.FULL_NAME, false)),
        RepositoryEntry.class);
    this.repositoryURL = repositoryURL;
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findByPattern(final String searchString, final RepositoryEntry.Kind kind, final Integer limit) throws Exception {
    int count = 0;
    final List<RepositoryEntry> result = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    for (RepositoryEntry entry : getUnmodifiableEntries()) {
      if (entry.getFullEntryName().matches(searchString) && (entry.getKind() == kind || kind == any)) {
        result.add(entry);
        if (limit != null && ++count == limit) {
          break;
        }
      }
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public boolean add(final RepositoryEntry entry) {
    return cachedEntries.add(entry);
  }

  /**
   * {@inheritDoc}
   */
  public boolean add(final List<RepositoryEntry> entries) {
    return cachedEntries.addAll(entries);
  }

  /**
   * {@inheritDoc}
   */
  public void removeByName(final String pathAndName, final boolean recursive) {
    final List<RepositoryEntry> toBeRemoved = new ArrayList<RepositoryEntry>();

    for (RepositoryEntry entry : cachedEntries) {
      if (recursive) {
        if (entry.getFullEntryName().startsWith(pathAndName)) {
          toBeRemoved.add(entry);
        }
      } else {
        if (entry.getFullEntryName().equals(pathAndName)) {
          cachedEntries.remove(entry);
          return;
        }
      }
    }
    cachedEntries.removeAll(toBeRemoved);
  }

  /**
   * {@inheritDoc}
   */
  public long getCachedRevision() {
    return cachedRevision;
  }

  /**
   * {@inheritDoc}
   */
  public void setCachedRevision(final long revision) {
    this.cachedRevision = revision;
  }

  /**
   * {@inheritDoc}
   */
  public void clear() {
    cachedEntries.clear();
  }

  /**
   * {@inheritDoc}
   */
  public Set<RepositoryEntry> getUnmodifiableEntries() {
    return Collections.unmodifiableSet(cachedEntries);
  }

  /**
   * {@inheritDoc}
   */
  public String getRepositoryUrl() {
    return repositoryURL;
  }

}

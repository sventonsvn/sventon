package de.berlios.sventon.svnsupport;

import de.berlios.sventon.ctrl.RepositoryEntry;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

/**
 * <code>java.util.Comparator&lt;T&gt;</code> implementation to support
 * ordering of <code>RepositoryEntry</code> objects.
 * <p>
 * The comparator can be configured during construction to tweak sorting behavior.
 * 
 * @author patrikfr@users.berlios.de
 */
public class RepositoryEntryComparator implements Comparator<RepositoryEntry>, Serializable {

  private static final long serialVersionUID = -823291078109887289L;

  /**
   * Constant for comparing on name.
   */
  public static final int NAME = 0;

  /**
   * Constant for comparing on last author
   */
  public static final int AUTHOR = 1;

  /**
   * Constant for comparing on revision.
   */
  public static final int REVISION = 2;

  /**
   * Constant for comparing on changed date.
   */
  public static final int DATE = 3;

  /**
   * Constant for comparing on full name including path.
   */
  public static final int FULL_NAME = 4;

  private boolean groupDirs = false;

  private int sortType = 0;

  private static final Set<Integer> legalTypes = new HashSet<Integer>();
  static {
    legalTypes.add(NAME);
    legalTypes.add(AUTHOR);
    legalTypes.add(REVISION);
    legalTypes.add(DATE);
    legalTypes.add(FULL_NAME);
  }

  /**
   * Create a new comparator for comparing <code>RepositoryEntry</code> objects.
   * 
   * @param sortType
   *          Entry type property to perform the comparisions on. See constants
   *          defined in this class.
   * @param groupDirs
   *          <code>true</code> to group directories, this will sort an entry
   *          of kind <code>SVNNodeKind.DIR</code> before an entries of other
   *          kinds.
   */
  public RepositoryEntryComparator(final int sortType, final boolean groupDirs) {

    if (!legalTypes.contains(sortType))
      throw new IllegalArgumentException("Not a valid sort type: " + sortType);

    this.groupDirs = groupDirs;
    this.sortType = sortType;
  }

  /**
   * {@inheritDoc}
   */
  public int compare(RepositoryEntry entry1, RepositoryEntry entry2) {

    if (groupDirs) {
      String kind1 = entry1.getKind();
      String kind2 = entry2.getKind();
      if (kind1 != kind2) // Not equal kinds, have to inspect.
      {
        if ("dir".equals(kind1)) {
          return -1;
        } else if ("dir".equals(kind2)) {
          return 1;
        }
      }// not equal kind, but neither is DIR
    }

    final String entryName1 = entry1.getName();
    final String entryName2 = entry2.getName();

    // Natural ordering of strings as used below may not always be desirable?
    switch (sortType) {
    case NAME:
      return nullSafeCompare(entryName1, entryName2);
    case AUTHOR:
      final String author1 = entry1.getAuthor();
      final String author2 = entry2.getAuthor();
      final int authCompare = nullSafeCompare(author1, author2);
      return authCompare == 0 ? nullSafeCompare(entryName1, entryName2) : authCompare;
    case REVISION:
      final long revision1 = entry1.getRevision();
      final long revision2 = entry2.getRevision();
      if (revision1 == revision2) {
        return nullSafeCompare(entryName1, entryName2);
      } else {
        return revision1 < revision2 ? -1 : 1;
      }
    case DATE:
      final Date date1 = entry1.getDate();
      final Date date2 = entry2.getDate();
      final int dateCompare = nullSafeCompare(date1, date2);
      return dateCompare == 0 ? nullSafeCompare(entryName1, entryName2) : dateCompare;
    case FULL_NAME:
      return nullSafeCompare(entry1.getFullEntryName(), entry2.getFullEntryName()); 
    default:
      throw new IllegalStateException("Illegal sort type: " + sortType);
    }
  }

  private <T> int nullSafeCompare(Comparable<T> o1, T o2) {
    if (o1 != null && o2 != null) {
      return o1.compareTo(o2);
    } else if (o1 == null && o2 == null) {
      return 0;
    } else {
      return o1 == null ? -1 : 1;
    }

  }
}

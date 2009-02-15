package org.sventon.util;

import org.sventon.model.RevisionRange;

import java.util.Iterator;
import java.util.List;

/**
 * RevisionRangeIterator.
 */
public class RevisionRangeIterator implements Iterator<RevisionRange> {

  private final Iterator<List<Long>> revisionsIterator;


  /**
   * Constructor.
   *
   * @param revisionsIterator Revisions.
   */
  public RevisionRangeIterator(final Iterator<List<Long>> revisionsIterator) {
    this.revisionsIterator = revisionsIterator;
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasNext() {
    return revisionsIterator.hasNext();
  }

  /**
   * {@inheritDoc}
   */
  public RevisionRange next() {
    final List<Long> integerList = revisionsIterator.next();
    return new RevisionRange(integerList.get(0), integerList.get(integerList.size() - 1));
  }

  /**
   * {@inheritDoc}
   */
  public void remove() {
    throw new UnsupportedOperationException();
  }
}

/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.util;

import org.sventon.model.RevisionRange;

import java.util.ArrayList;
import java.util.List;

/**
 * RevisionRangeSplitter.
 *
 * @author jesper@sventon.org
 */
public class RevisionRangeSplitter implements Iterable<RevisionRange> {

  private final int maxRevisionsPerElement;

  private final List<List<Long>> revisions = new ArrayList<List<Long>>();

  /**
   * Constructor.
   *
   * @param maxRevisionsPerElement Maximum number of revisions per element.
   * @param revisions              Revisions.
   */
  public RevisionRangeSplitter(final List<Long> revisions, final int maxRevisionsPerElement) {
    this.maxRevisionsPerElement = maxRevisionsPerElement;
    this.revisions.addAll(split(revisions, maxRevisionsPerElement));
  }

  // //ArrayUtils.subarray()
  private List<List<Long>> split(List<Long> input, int maxSize) {
    List<List<Long>> lists = new ArrayList<List<Long>>();

    for (int i = 0; i < input.size(); i += maxSize) {
      int toIndex = Math.min(input.size(), i + maxSize);
      lists.add(input.subList(i, toIndex));
    }
    return lists;
  }

  /**
   * @return Max number of revisions per element.
   */
  public int getMaxRevisionsPerElement() {
    return maxRevisionsPerElement;
  }

  /**
   * @return Size, ie. number of elements.
   */
  public int size() {
    return revisions.size();
  }

  /**
   * {@inheritDoc}
   */
  public RevisionRangeIterator iterator() {
    return new RevisionRangeIterator(revisions.iterator());
  }
}

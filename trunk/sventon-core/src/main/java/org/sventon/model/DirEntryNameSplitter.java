/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Splits up a directory entry name into fragments suited for indexation.
 */
public class DirEntryNameSplitter {

  private String entryName;

  private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{Punct}+");

  /**
   * Constructor.
   *
   * @param entryName Entry name to split
   */
  public DirEntryNameSplitter(final String entryName) {

    this.entryName = entryName;
  }

  /**
   * @return Space delimited fragments without punctuation.
   */
  public List<String> split() {
    final String strippedName = removePunctuationCharacters();
    final List<String> fragments = new ArrayList<String>();
    final StringBuilder tempBuf = new StringBuilder();
    boolean start = false;

    for (int i = 0; i < strippedName.length(); i++) {
      final Character c = strippedName.charAt(i);
      if (Character.isWhitespace(c)) {
        start = addFragment(fragments, tempBuf);
      } else if (Character.isUpperCase(c) || !Character.isLetter(c)) {
        if (!start) {
          start = addFragment(fragments, tempBuf);
        }
        tempBuf.append(c);
      } else {
        if (start) start = false;
        tempBuf.append(c);
      }
    }
    addFragment(fragments, tempBuf);
    return fragments;
  }

  private String removePunctuationCharacters() {
    return PUNCTUATION_PATTERN.matcher(entryName).replaceAll(" ");
  }

  private boolean addFragment(final List<String> fragments, final StringBuilder buf) {
    final String trimmedString = StringUtils.trimToNull(buf.toString());
    if (trimmedString != null) fragments.add(trimmedString);
    buf.setLength(0);
    return true;
  }

}

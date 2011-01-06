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

import java.io.Serializable;
import java.util.List;

/**
 * DirList holds the result of a directory list operation, i.e. a List of DirEntry's
 * and Properties.
 *
 * @author jorgen@sventon.org
 */
public class DirList implements Serializable {

  private static final long serialVersionUID = 760450236832559872L;

  private final List<DirEntry> entries;

  private final Properties properties;

  /**
   * Constructor.
   *
   * @param entries    Entries in a specific directory.
   * @param properties Properties for
   */
  public DirList(final List<DirEntry> entries, final Properties properties) {
    this.entries = entries;
    this.properties = properties;
  }

  /**
   * @return Entries
   */
  public List<DirEntry> getEntries() {
    return entries;
  }

  /**
   * @return Properties
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * @return Number of entries in directory.
   */
  public int getEntriesCount() {
    return entries.size();
  }

  /**
   * @return Number of properties set on directory.
   */
  public int getPropertiesCount() {
    return properties.getSize();
  }
}

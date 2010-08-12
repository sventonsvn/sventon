/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import java.util.List;

/**
 * DirList holds the result of a directory list operation, i.e. a List of DirEntry's
 * and Properties.
 */
public class DirList {
  private final List<DirEntry> entries;
  private final Properties properties;

  public DirList(List<DirEntry> entries, Properties properties) {
    this.entries = entries;
    this.properties = properties;
  }

  public List<DirEntry> getEntries() {
    return entries;
  }

  public Properties getProperties() {
    return properties;
  }
}

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
package org.sventon.web.command.editor;

import org.sventon.model.PathRevision;
import org.sventon.model.Revision;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom property editor for a path/revision combination.
 *
 * @author jesper@sventon.org
 */
public class PathRevisionEditor extends PropertyEditorSupport {

  /**
   * The delimiter between the path and the revision values.
   */
  private static final String DELIMITER = "@";

  @Override
  public void setAsText(final String entryAsString) {
    setValue(toPathRevision(entryAsString));
  }

  private PathRevision toPathRevision(final String entry) {
    if (!entry.contains(DELIMITER)) {
      throw new IllegalArgumentException("Illegal parameter. No delimiter in entry: " + entry);
    }
    final String path = entry.substring(0, entry.lastIndexOf(DELIMITER));
    final String revision = entry.substring(entry.lastIndexOf(DELIMITER) + 1);
    return new PathRevision(path, Revision.parse(revision));
  }

  @Override
  public String getAsText() {
    final PathRevision fileRevision = (PathRevision) super.getValue();
    if (fileRevision == null) {
      return "";
    } else {
      return fileRevision.getPath() + DELIMITER + fileRevision.getRevision();
    }
  }

  public PathRevision[] convert(final String[] entries) {
    final List<PathRevision> fileEntries = new ArrayList<PathRevision>();
    for (String entry : entries) {
      fileEntries.add(toPathRevision(entry));
    }
    return fileEntries.toArray(new PathRevision[fileEntries.size()]);
  }

}
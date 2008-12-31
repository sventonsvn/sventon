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

import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom property editor for repository name.
 *
 * @author jesper@sventon.org
 */
public class SVNFileRevisionEditor extends PropertyEditorSupport {

  /**
   * The delimiter between the path and the revision values.
   */
  private static final String DELIMITER = ";;";

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAsText(final String entryAsString) {
    setValue(toSVNFileRevision(entryAsString));
  }

  private SVNFileRevision toSVNFileRevision(final String entry) {
    if (!entry.contains(DELIMITER)) {
      throw new IllegalArgumentException("Illegal parameter. No delimiter in entry: " + entry);
    }
    final String path = entry.substring(0, entry.lastIndexOf(DELIMITER));
    final String revision = entry.substring(entry.lastIndexOf(DELIMITER) + 2);
    return new SVNFileRevision(path, Long.parseLong(revision), null, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsText() {
    final SVNFileRevision fileRevision = (SVNFileRevision) super.getValue();
    if (fileRevision == null) {
      return "";
    } else {
      return fileRevision.getPath() + ";;" + fileRevision.getRevision();
    }
  }

  public SVNFileRevision[] convert(final String[] entries) {
    final List<SVNFileRevision> fileEntries = new ArrayList<SVNFileRevision>();
    for (String entry : entries) {
      fileEntries.add(toSVNFileRevision(entry));
    }
    return fileEntries.toArray(new SVNFileRevision[fileEntries.size()]);
  }

}
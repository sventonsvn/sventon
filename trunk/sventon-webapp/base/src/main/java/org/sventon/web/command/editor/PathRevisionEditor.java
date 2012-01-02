/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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

import java.beans.PropertyEditorSupport;

/**
 * Custom property editor for a path/revision combination.
 *
 * @author jesper@sventon.org
 */
public class PathRevisionEditor extends PropertyEditorSupport {

  @Override
  public void setAsText(final String entryAsString) {
    setValue(PathRevision.parse(entryAsString));
  }

  @Override
  public String getAsText() {
    final PathRevision fileRevision = (PathRevision) super.getValue();
    if (fileRevision == null) {
      return "";
    } else {
      return fileRevision.getPath() + PathRevision.DELIMITER + fileRevision.getRevision();
    }
  }

}
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

import org.sventon.model.Revision;

import java.beans.PropertyEditorSupport;

/**
 * Custom property editor for repository name.
 *
 * @author jesper@sventon.org
 */
public class RevisionEditor extends PropertyEditorSupport {

  @Override
  public void setAsText(final String text) {
    setValue(Revision.parse(text));
  }

  @Override
  public String getAsText() {
    return super.getValue().toString();
  }

}
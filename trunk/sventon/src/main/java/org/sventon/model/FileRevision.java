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

import java.util.HashMap;
import java.util.Map;

/**
 * FileRevision.
 *
 * @author jorgen@sventon.org
 * @author jesper@sventon.org
 */
public class FileRevision extends PathRevision {


  /**
   * Map holding the RevisionProperties and the corresponding (String) value.
   */
  private final Map<RevisionProperty, String> properties = new HashMap<RevisionProperty, String>();

  public FileRevision(String path, Revision revision) {
    super(path, revision);
  }

  public void addProperty(RevisionProperty property, String propertyValue) {
    properties.put(property, propertyValue);
  }

  public String getProperty(RevisionProperty value) {
    return properties.get(value);
  }

}

/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.export;

import org.sventon.model.RepositoryName;

/**
 * ExportDirectoryFactory.
 *
 * @author jesper@sventon.org
 */
public interface ExportDirectoryFactory {

  /**
   * Creates an export directory.
   *
   * @param repositoryName Repository name.
   * @return Directory instance.
   */
  ExportDirectory create(final RepositoryName repositoryName);

}

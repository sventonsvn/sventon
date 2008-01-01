/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing user specific data.
 * An instance will be stored on the user's HTTPSession.
 * This class works more or less as a map and holds zero or more
 * {@link de.berlios.sventon.web.model.UserRepositoryContext} instances.
 *
 * @author patrik@sventon.org
 */
public final class UserContext implements Serializable {

  private static final long serialVersionUID = 6749054345534594360L;

  private Map<String, UserRepositoryContext> repositoryContexts = new HashMap<String, UserRepositoryContext>();

  /**
   * Get a user context given the repository name.
   *
   * @param repositoryName Repository name.
   * @return Matching instance, {@code null} if not found.
   */
  public UserRepositoryContext getRepositoryContext(final String repositoryName) {
    return repositoryContexts.get(repositoryName);
  }

  /**
   * Add new {@link de.berlios.sventon.web.model.UserRepositoryContext} instances.
   *
   * @param repositoryName Repository name to use for binding the context.
   * @param urc            Context.
   */
  public void add(final String repositoryName, final UserRepositoryContext urc) {
    repositoryContexts.put(repositoryName, urc);
  }

  /**
   * Remove a {@link de.berlios.sventon.web.model.UserRepositoryContext} instance.
   *
   * @param repositoryName Repository name for context to remove.
   */
  public void remove(final String repositoryName) {
    repositoryContexts.remove(repositoryName);
  }

  //TODO: Add toString()
}

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
package org.sventon.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing user specific data.
 * An instance will be stored on the user's HTTPSession.
 * This class works more or less as a map and holds zero or more
 * {@link UserRepositoryContext} instances.
 *
 * @author patrik@sventon.org
 */
public final class UserContext implements Serializable {

  private static final long serialVersionUID = 6749054345534594360L;

  private final Map<RepositoryName, UserRepositoryContext> repositoryContexts =
      new HashMap<RepositoryName, UserRepositoryContext>();

  /**
   * Get a user context given the repository name.
   *
   * @param repositoryName Repository name.
   * @return Matching instance, {@code null} if not found.
   */
  public UserRepositoryContext getUserRepositoryContext(final RepositoryName repositoryName) {
    return repositoryContexts.get(repositoryName);
  }

  /**
   * Add new {@link UserRepositoryContext} instances.
   *
   * @param repositoryName Repository name to use for binding the context.
   * @param urc            Context.
   */
  public void add(final RepositoryName repositoryName, final UserRepositoryContext urc) {
    repositoryContexts.put(repositoryName, urc);
  }

  /**
   * Remove a {@link UserRepositoryContext} instance.
   *
   * @param repositoryName Repository name for context to remove.
   */
  public void remove(final RepositoryName repositoryName) {
    repositoryContexts.remove(repositoryName);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}

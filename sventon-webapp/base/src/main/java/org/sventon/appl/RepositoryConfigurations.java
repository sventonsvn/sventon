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
package org.sventon.appl;

import org.apache.commons.collections.CollectionUtils;
import org.sventon.model.RepositoryName;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Container to hold repository configurations.
 */
public class RepositoryConfigurations {

  /**
   * Map of added subversion repository names and their configurations.
   */
  private final Map<RepositoryName, RepositoryConfiguration> repositoryConfigurations =
      new ConcurrentHashMap<RepositoryName, RepositoryConfiguration>();

  public void add(RepositoryConfiguration repositoryConfiguration) {
    repositoryConfigurations.put(repositoryConfiguration.getName(), repositoryConfiguration);
  }

  public RepositoryConfiguration getConfiguration(RepositoryName name) {
    return repositoryConfigurations.get(name);
  }

  public RepositoryConfiguration remove(RepositoryName name) {
    repositoryConfigurations.values();
    return repositoryConfigurations.remove(name);
  }

  /**
   * Return all repository configurations.
   * Observes the same behavior as {@link java.util.Map#values()}
   *
   * @return Collection with all repository configurations.
   */
  public Collection<RepositoryConfiguration> getAllConfigurations() {
    return repositoryConfigurations.values();
  }

  /**
   * Test if there is an entry for the given repository name.
   *
   * @param repositoryName Name.
   * @return {@code true} if there is an entry for the given name.
   */
  public boolean containsConfiguration(RepositoryName repositoryName) {
    return repositoryConfigurations.containsKey(repositoryName);
  }

  /**
   * Retrieve a set of all RepositoryNames configured.
   *
   * @return Possibly empty set of repository names.
   */
  public Set<RepositoryName> getAllConfigurationNames() {
    return repositoryConfigurations.keySet();
  }

  /**
   * Count all repository configurations.
   *
   * @return Count.
   */
  public int count() {
    return repositoryConfigurations.size();
  }

  /**
   * Compare two configurations using the names of the repository configurations.
   *
   * @param configsToCompare New configurations to compare with.
   * @return A {@link org.sventon.appl.RepositoryConfigurations.ConfigsDiff} instance describing what
   *         needs to be added and removed to this configs instance to get to the {@code configsToCompare} instance.
   */
  public ConfigsDiff diffByRepositoryName(RepositoryConfigurations configsToCompare) {

    ConfigsDiff diff = new ConfigsDiff();

    Set<RepositoryName> oldRepositoryNames = getAllConfigurationNames();
    Set<RepositoryName> newRepositoryNames = configsToCompare.getAllConfigurationNames();

    @SuppressWarnings({"unchecked"})
    Collection<RepositoryName> removed = CollectionUtils.subtract(oldRepositoryNames, newRepositoryNames);
    @SuppressWarnings({"unchecked"})
    Collection<RepositoryName> added = CollectionUtils.subtract(newRepositoryNames, oldRepositoryNames);

    for (RepositoryName repositoryName : added) {
      diff.added.add(configsToCompare.getConfiguration(repositoryName));
    }

    for (RepositoryName repositoryName : removed) {
      diff.removed.add(getConfiguration(repositoryName));
    }


    return diff;

  }

  public void apply(ConfigsDiff diff) {
    for (RepositoryConfiguration repositoryConfiguration : diff.removed) {
      remove(repositoryConfiguration.getName());
    }

    for (RepositoryConfiguration repositoryConfiguration : diff.added) {
      add(repositoryConfiguration);
    }
  }

  public class ConfigsDiff {
    public Set<RepositoryConfiguration> added = new HashSet<RepositoryConfiguration>();
    public Set<RepositoryConfiguration> removed = new HashSet<RepositoryConfiguration>();
  }
}

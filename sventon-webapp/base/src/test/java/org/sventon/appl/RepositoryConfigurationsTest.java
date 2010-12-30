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
package org.sventon.appl;

import org.junit.Before;
import org.junit.Test;
import org.sventon.model.RepositoryName;

import java.util.Collection;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class RepositoryConfigurationsTest {
  private RepositoryConfigurations configs;
  private RepositoryConfiguration cheeseConfig;
  private RepositoryConfiguration hamConfig;
  private RepositoryConfigurations configs2;
  private RepositoryConfiguration milkConfig;
  private RepositoryConfiguration cerealConfig;

  @Before
  public void setUp() throws Exception {
    configs = new RepositoryConfigurations();
    cheeseConfig = new RepositoryConfiguration("cheese");
    hamConfig = new RepositoryConfiguration("ham");
    configs2 = new RepositoryConfigurations();
    milkConfig = new RepositoryConfiguration("milk");
    cerealConfig = new RepositoryConfiguration("cereal");
  }

  @Test
  public void addAndRemoveConfigurations() {
    configs.add(cheeseConfig);
    assertThat(configs.getConfiguration(new RepositoryName("cheese")), sameInstance(cheeseConfig));
    assertThat(configs.getConfiguration(new RepositoryName("ham")), nullValue());

    RepositoryConfiguration removedConfig = configs.remove(new RepositoryName("cheese"));
    assertThat(removedConfig, sameInstance(cheeseConfig));
    assertThat(configs.getConfiguration(new RepositoryName("cheese")), nullValue());
  }

  @Test
  public void getAllConfigurations() {
    configs.add(cheeseConfig);
    configs.add(hamConfig);

    Collection<RepositoryConfiguration> allConfigs = configs.getAllConfigurations();
    assertThat(allConfigs.size(), is(2));
  }

  @Test
  public void containsConfiguration() {
    configs.add(cheeseConfig);
    configs.add(hamConfig);

    assertThat(configs.containsConfiguration(new RepositoryName("cheese")), is(true));
  }

  @Test
  public void getAllConfigurationNames() {
    configs.add(cheeseConfig);
    configs.add(hamConfig);

    Set<RepositoryName> names = configs.getAllConfigurationNames();
    assertThat(names.size(), is(2));
    assertThat(names.contains(hamConfig.getName()), is(true));
    assertThat(names.contains(cheeseConfig.getName()), is(true));
  }

  //-- Diff tests

  @Test
  public void getRepositoryConfigurationCount() {
    RepositoryConfigurations configs = new RepositoryConfigurations();
    assertThat(configs.count(), is(0));
    configs.add(cheeseConfig);
    configs.add(hamConfig);
    assertThat(configs.count(), is(2));
  }

  @Test
  public void diffTwoConfigurationsThatHasNothingInCommon() {
    configs.add(cheeseConfig);
    configs.add(hamConfig);

    configs2.add(milkConfig);
    configs2.add(cerealConfig);

    RepositoryConfigurations.ConfigsDiff diff = configs.diffByRepositoryName(configs2);

    assertThat(diff.added.size(), is(2));
    assertThat(diff.removed.size(), is(2));

    assertThat(diff.added.contains(milkConfig), is(true));
    assertThat(diff.added.contains(cerealConfig), is(true));

    assertThat(diff.removed.contains(cheeseConfig), is(true));
    assertThat(diff.removed.contains(hamConfig), is(true));

  }

  @Test
  public void diffTwoConfigurationsThatHaveTheSameContents() {
    configs.add(cheeseConfig);
    configs.add(hamConfig);

    configs2.add(cheeseConfig);
    configs2.add(hamConfig);

    RepositoryConfigurations.ConfigsDiff diff = configs.diffByRepositoryName(configs2);

    assertThat(diff.added.size(), is(0));
    assertThat(diff.removed.size(), is(0));
  }

  @Test
  public void diffTwoOverlappingConfigurations() {
    configs.add(cheeseConfig);
    configs.add(hamConfig);

    configs2.add(cheeseConfig);
    configs2.add(cerealConfig);

    RepositoryConfigurations.ConfigsDiff diff = configs.diffByRepositoryName(configs2);

    assertThat(diff.added.size(), is(1));
    assertThat(diff.removed.size(), is(1));

    assertThat(diff.added.contains(cerealConfig), is(true));
    assertThat(diff.removed.contains(hamConfig), is(true));
  }

  @Test
  public void diffTwoConfigurationsWhereTheNewContainsAllOldConfigurations() {
    configs.add(cheeseConfig);
    configs.add(hamConfig);

    configs2.add(cheeseConfig);
    configs2.add(hamConfig);
    configs2.add(cerealConfig);

    RepositoryConfigurations.ConfigsDiff diff = configs.diffByRepositoryName(configs2);

    assertThat(diff.added.size(), is(1));
    assertThat(diff.removed.size(), is(0));

    assertThat(diff.added.contains(cerealConfig), is(true));
  }

  @Test
  public void diffTwoConfigurationsWhereTheNewContainsFewerConfigurations() {
    configs.add(cheeseConfig);
    configs.add(hamConfig);

    configs2.add(hamConfig);

    RepositoryConfigurations.ConfigsDiff diff = configs.diffByRepositoryName(configs2);

    assertThat(diff.added.size(), is(0));
    assertThat(diff.removed.size(), is(1));

    assertThat(diff.removed.contains(cheeseConfig), is(true));
  }

  //-- apply tests

  @Test
  public void applyConfigDiffs() {
    configs.add(cheeseConfig);
    configs.add(hamConfig);

    configs2.add(cheeseConfig);
    configs2.add(cerealConfig);

    RepositoryConfigurations.ConfigsDiff diff = configs.diffByRepositoryName(configs2);

    configs.apply(diff);

    assertThat(configs.count(), is(2));
    assertThat(configs.getConfiguration(new RepositoryName("cheese")), sameInstance(cheeseConfig));
    assertThat(configs.getConfiguration(new RepositoryName("cheese")), sameInstance(cheeseConfig));
    assertThat(configs.getConfiguration(new RepositoryName("ham")), nullValue());

  }

}

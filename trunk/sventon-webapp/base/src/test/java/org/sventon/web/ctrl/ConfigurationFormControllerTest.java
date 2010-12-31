/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.appl.Application;
import org.sventon.model.RepositoryName;
import org.sventon.web.command.ConfigCommand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeValues;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;

@SuppressWarnings({"unchecked"})
public class ConfigurationFormControllerTest {
  private static final String CONFIG_FORM_VIEW_NAME = "config/configForm";
  private static final String LIST_CONFIGS_VIEW_NAME = "config/listConfigs";

  @Test
  public void setUpForm() throws Exception {
    Application application = mock(Application.class);

    HashSet<RepositoryName> repositoryNames = new HashSet<RepositoryName>();
    repositoryNames.add(new RepositoryName("sventon"));
    repositoryNames.add(new RepositoryName("nordpolen"));
    when(application.getRepositoryNames()).thenReturn(repositoryNames);
    ConfigurationFormController controller = new ConfigurationFormController(application);

    ModelAndView modelAndView = controller.setUpForm(new ModelAndView());

    Map expectedModel = new HashMap();
    expectedModel.put("addedRepositories", repositoryNames);
    expectedModel.put("command", new ConfigCommand());

    assertModelAttributeValues(modelAndView, expectedModel);
    assertViewName(modelAndView, CONFIG_FORM_VIEW_NAME);
  }

  @Test
  public void setUpFormWhereThereAreNowReposConfigured() throws Exception {

    Application application = mock(Application.class);

    HashSet<RepositoryName> emptyRepoNames = new HashSet<RepositoryName>();
    when(application.getRepositoryNames()).thenReturn(emptyRepoNames);
    ConfigurationFormController controller = new ConfigurationFormController(application);

    ModelAndView modelAndView = controller.setUpForm(new ModelAndView());

    Map expectedModel = new HashMap();
    expectedModel.put("addedRepositories", emptyRepoNames);
    expectedModel.put("command", new ConfigCommand());

    assertModelAttributeValues(modelAndView, expectedModel);
    assertViewName(modelAndView, CONFIG_FORM_VIEW_NAME);
  }

  //---

  @Test
  public void onFormSubmitRedirectsToFormOnValidationError() {
    Application application = mock(Application.class);
    ConfigurationFormController controller = new ConfigurationFormController(application);

    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.hasErrors()).thenReturn(true);

    String formView = controller.onFormSubmit(new ConfigCommand(), bindingResult, mock(Model.class));
    assertThat(formView, is(CONFIG_FORM_VIEW_NAME));
  }

  @Test
  public void onFormSubmitRepositoryAdded() throws Exception {

    Application application = mock(Application.class);

    HashSet<RepositoryName> repositoryNames = new HashSet<RepositoryName>();
    repositoryNames.add(new RepositoryName("sventon"));
    repositoryNames.add(new RepositoryName("nordpolen"));
    when(application.getRepositoryNames()).thenReturn(repositoryNames);

    ConfigCommand command = new ConfigCommand();
    command.setName("nordpolen");
    command.setRepositoryUrl("svn://northpole.com/repo");

    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.hasErrors()).thenReturn(false);

    Model model = new ExtendedModelMap();

    ConfigurationFormController controller = new ConfigurationFormController(application);
    String listConfigView = controller.onFormSubmit(command, bindingResult, model);

    Map expectedModel = new HashMap();
    expectedModel.put("addedRepositories", repositoryNames);
    expectedModel.put("latestAddedRepository", command.getName());

    assertThat(listConfigView, is(LIST_CONFIGS_VIEW_NAME));
    assertThat(model.asMap(), is(expectedModel));

  }
}
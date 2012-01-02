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
package org.sventon.web.ctrl;

import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.sventon.SVNConnectionFactory;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.PathRevision;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditor;

/**
 * Abstract base class for controllers using {@link org.sventon.web.command.BaseCommand}.
 *
 * @author jesper@sventon.org
 */
public abstract class AbstractBaseController extends AbstractCommandController {

  /**
   * The application.
   */
  protected Application application;

  /**
   * Service.
   */
  private RepositoryService repositoryService;

  /**
   * The repository factory.
   */
  protected SVNConnectionFactory connectionFactory;

  /**
   * Name property editor instance.
   */
  private PropertyEditor nameEditor;

  /**
   * Revision editor instance.
   */
  private PropertyEditor revisionEditor;

  /**
   * Revision editor instance.
   */
  private PropertyEditor pathRevisionEditor;

  @Override
  protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) {
    binder.registerCustomEditor(RepositoryName.class, nameEditor);
    binder.registerCustomEditor(Revision.class, revisionEditor);
    binder.registerCustomEditor(PathRevision.class, pathRevisionEditor);
  }

  /**
   * Sets the editor.
   *
   * @param nameEditor Editor.
   */
  public void setNameEditor(final PropertyEditor nameEditor) {
    this.nameEditor = nameEditor;
  }

  /**
   * Sets the editor.
   *
   * @param revisionEditor Editor.
   */
  public void setRevisionEditor(final PropertyEditor revisionEditor) {
    this.revisionEditor = revisionEditor;
  }

  /**
   * Sets the editor.
   *
   * @param pathRevisionEditor Editor.
   */
  public void setPathRevisionEditor(final PropertyEditor pathRevisionEditor) {
    this.pathRevisionEditor = pathRevisionEditor;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public final void setApplication(final Application application) {
    this.application = application;
  }

  /**
   * Get current application configuration.
   *
   * @param name Repository name
   * @return ApplicationConfiguration
   */
  protected final RepositoryConfiguration getRepositoryConfiguration(final RepositoryName name) {
    return application.getConfiguration(name);
  }

  /**
   * Gets the repository service instance.
   *
   * @return Repository service
   */
  protected final RepositoryService getRepositoryService() {
    return repositoryService;
  }

  /**
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  public final void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  /**
   * Sets the connection factory instance.
   *
   * @param connectionFactory Factory instance.
   */
  public void setConnectionFactory(final SVNConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

}
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
package org.sventon.web.ctrl;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.appl.Application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for persisting application configuration.
 * Called after one or more instances have been submitted to {@link ConfigurationFormController}.
 *
 * @author jesper@sventon.org
 */
public final class SubmitConfigurationsController extends AbstractController {

  /**
   * The application.
   */
  private Application application;

  /**
   * The scheduler instance. Used to fire cache update job.
   */
  private Scheduler scheduler;

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public void setApplication(final Application application) {
    this.application = application;
  }

  /**
   * Sets scheduler instance.
   * The scheduler is used to fire cache update job after configuration has been done.
   *
   * @param scheduler The scheduler
   */
  public void setScheduler(final Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    if (!application.hasConfigurations()) {
      logger.warn("No repository has been configured and added");
      return new ModelAndView("error/configurationError");
    }

    application.persistRepositoryConfigurations();
    application.initCaches();

    if (!application.isConfigured()) {
      application.setConfigured(true);

      try {
        logger.debug("Starting up caches");
        scheduler.triggerJob("cacheUpdateJobDetail", Scheduler.DEFAULT_GROUP);
      } catch (SchedulerException sx) {
        logger.warn(sx);
      }
    }

    logger.info("Configuration done!");
    return new ModelAndView(new RedirectView("/repos/list", true));
  }

}

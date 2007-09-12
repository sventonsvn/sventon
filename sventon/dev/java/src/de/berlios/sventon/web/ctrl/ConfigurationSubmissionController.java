/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for persisting application configuration.
 * Called after one or more instances have been submitted to {@link ConfigurationController}.
 *
 * @author jesper@users.berlios.de
 */
public final class ConfigurationSubmissionController extends AbstractController {

  /**
   * The application.
   */
  private Application application;

  /**
   * The scheduler instance. Used to fire cache update job.
   */
  private Scheduler scheduler;

  /**
   * Logger for this class and subclasses.
   */
  private final Log logger = LogFactory.getLog(getClass());

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

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    if (application.isConfigured()) {
      throw new IllegalStateException("sventon is already configured!");
    }

    if (application.getInstanceCount() == 0) {
      logger.warn("No instance has been configured and added");
      return new ModelAndView("configurationError");
    }

    application.storeInstanceConfigurations();
    application.setConfigured(true);
    application.initCaches();

    try {
      logger.debug("Starting up caches");
      scheduler.triggerJob("cacheUpdateJobDetail", Scheduler.DEFAULT_GROUP);
    } catch (SchedulerException sx) {
      logger.warn(sx);
    }

    logger.info("Configuration done!");
    return new ModelAndView(new RedirectView("start.svn"));
  }
}

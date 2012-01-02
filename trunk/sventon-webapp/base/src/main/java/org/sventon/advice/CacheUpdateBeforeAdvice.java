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
package org.sventon.advice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.sventon.model.RepositoryName;
import org.sventon.repository.RepositoryChangeMonitor;

import java.lang.reflect.Method;

/**
 * Before advice that checks if the cache needs to be updated before proceeding.
 *
 * @author jesper@sventon.org
 */
public final class CacheUpdateBeforeAdvice implements MethodBeforeAdvice {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The change monitor. Used to trigger new revision notifications.
   */
  private RepositoryChangeMonitor repositoryChangeMonitor;

  /**
   * Sets the change monitor. Needed to trigger new revision notifications.
   *
   * @param repositoryChangeMonitor The monitor
   */
  @Autowired
  public void setRepositoryChangeMonitor(final RepositoryChangeMonitor repositoryChangeMonitor) {
    this.repositoryChangeMonitor = repositoryChangeMonitor;
  }

  @Override
  public void before(final Method method, final Object[] args, final Object target) throws Throwable {
    final RepositoryName repositoryName = (RepositoryName) args[0];
    logger.debug("Updating cache for repository [" + repositoryName + "] (if needed)");
    repositoryChangeMonitor.update(repositoryName);
  }
}

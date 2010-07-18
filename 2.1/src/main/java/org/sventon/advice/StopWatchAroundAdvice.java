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
package org.sventon.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StopWatch;

/**
 * Around advice that logs the method execution time.
 *
 * @author jesper@sventon.org
 */
public final class StopWatchAroundAdvice implements MethodInterceptor {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  public Object invoke(final MethodInvocation method) throws Throwable {
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    final Object val = method.proceed();
    stopWatch.stop();
    logger.debug(createMessage(method.getMethod().getName(), stopWatch.getTotalTimeMillis()));
    return val;
  }

  protected String createMessage(final String methodName, long milliseconds) {
    return "Method [" + methodName + "] took [" + milliseconds + "] ms";
  }
}
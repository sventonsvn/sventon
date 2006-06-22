/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.RevisionObservable;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Before advice that checks if the cache needs to be updated.
 *
 * @author jesper@users.berlios.de
 */
public class CacheBeforeAdvice implements MethodBeforeAdvice {

  /**
   * The Observable. Used to trigger cache updates.
   */
  private RevisionObservable revisionObservable;

  /**
   * Sets the observable. Needed to trigger cache updates.
   *
   * @param revisionObservable The observable
   */
  public void setRevisionObservable(final RevisionObservable revisionObservable) {
    this.revisionObservable = revisionObservable;
  }

  /**
   * {@inheritDoc]
   */
  public void before(Method method, Object[] args, Object target) throws Throwable {
    revisionObservable.update();
  }
}
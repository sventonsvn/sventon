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
package org.sventon.service;

import org.junit.Test;

import java.util.ServiceLoader;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ServiceLoaderTest {

  @Test
  public void testLoader() throws Exception {
    final ServiceLoader<TestService> loader = ServiceLoader.load(TestService.class);

    TestService loadedService = null;

    for (TestService testService : loader) {
      loadedService = testService;
    }

    if (loadedService == null) fail("Unable to load service");

    assertTrue(loadedService.getMessage().endsWith("TestServiceImpl1"));
  }

  public interface TestService {
    String getMessage();
  }

  /**
   * Test implementation. Will be instantiated by the ServiceLoader.
   */
  public static class TestServiceImpl1 implements TestService {
    @Override
    public String getMessage() {
      return "Hello from: " + this.getClass();
    }
  }

}

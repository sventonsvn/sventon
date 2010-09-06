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

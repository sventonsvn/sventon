package org.sventon.web.ctrl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.sventon.appl.Application;
import org.sventon.cache.CacheException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

public class ConfigurationReloadControllerTest {
  private Application application;
  private ConfigurationReloadController controller;

  private void assertContentType(ResponseEntity<String> response) {
    assertThat(response.getHeaders().getContentType().getType(), equalTo("text"));
    assertThat(response.getHeaders().getContentType().getSubtype(), equalTo("plain"));
  }

  @Test
  public void reloadConfigFileHappyPath() throws Exception {

    ResponseEntity<String> response = controller.reloadConfigAndReinitializeApplication();
    assertThat(response.getBody(), equalTo("Configuration reloaded."));
    assertThat(response.getStatusCode(), equalTo(OK));
    assertContentType(response);
  }

  @Test
  public void reloadConfigFileWhenExceptionOccurs() throws Exception {

    //noinspection ThrowableInstanceNeverThrown
    doThrow(new CacheException("exception")).when(application).reinit();
    ResponseEntity<String> response = controller.reloadConfigAndReinitializeApplication();
    assertThat(response.getBody(), equalTo("Internal error."));
    assertThat(response.getStatusCode(), equalTo(INTERNAL_SERVER_ERROR));
    assertContentType(response);

  }


  @Before
  public void setUp() throws Exception {
    application = mock(Application.class);
    controller = new ConfigurationReloadController(application);
  }
}
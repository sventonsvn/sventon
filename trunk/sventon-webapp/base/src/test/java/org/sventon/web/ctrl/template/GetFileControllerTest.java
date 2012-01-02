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
package org.sventon.web.ctrl.template;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.SVNConnection;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.cache.CacheException;
import org.sventon.cache.ObjectCacheManager;
import org.sventon.cache.objectcache.DefaultObjectCache;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.model.RepositoryName;
import org.sventon.service.RepositoryService;
import org.sventon.util.ImageScaler;
import org.sventon.util.WebUtils;
import org.sventon.web.command.BaseCommand;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class GetFileControllerTest {

  private final MockServletContext mockServletContext = new MockServletContext();

  private Application application;
  private ConfigDirectory configDirectory;
  private RepositoryConfiguration repositoryConfiguration;
  private RepositoryName repositoryName;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();

    mockServletContext.setContextPath("sventon-test");

    repositoryName = new RepositoryName("test");

    configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    configDirectory.setServletContext(mockServletContext);

    application = new Application(configDirectory);
    application.setConfigured(true);

    repositoryConfiguration = new RepositoryConfiguration(repositoryName.toString());
    application.addConfiguration(repositoryConfiguration);
  }

  @Test
  public void testSvnHandleGetImageAsInline() throws Exception {
    final BaseCommand command = new BaseCommand();
    command.setName(repositoryName);
    command.setPath("/testimage.gif");

    final GetFileController ctrl = new GetFileController();
    ctrl.setApplication(application);
    ctrl.setRepositoryService(mock(RepositoryService.class));

    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();
    ctrl.setMimeFileTypeMap(mftm);
    final ModelAndView modelAndView;

    request.addParameter(GetFileController.DISPLAY_REQUEST_PARAMETER, GetFileController.CONTENT_DISPOSITION_INLINE);

    final MockHttpServletResponse res = new MockHttpServletResponse();
    modelAndView = ctrl.svnHandle(null, command, 100, null, request, res, null);

    assertNull(modelAndView);
    assertEquals("image/gif", res.getContentType());
    assertTrue(((String) res.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).startsWith("inline"));
  }

  @Test
  public void testSvnHandleGetFileAsAttachment() throws Exception {
    final BaseCommand command = new BaseCommand();
    command.setName(repositoryName);
    command.setPath("/testimage.gif");

    final GetFileController ctrl = new GetFileController();
    ctrl.setApplication(application);
    ctrl.setRepositoryService(mock(RepositoryService.class));

    request.addParameter(GetFileController.DISPLAY_REQUEST_PARAMETER, (String) null);

    final MockHttpServletResponse mockResponse = new MockHttpServletResponse();
    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, mockResponse, null);

    assertNull(modelAndView);

    assertEquals(WebUtils.APPLICATION_OCTET_STREAM, mockResponse.getContentType());
    assertTrue(((String) mockResponse.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).startsWith("attachment"));
  }

  @Test
  public void testCacheNotUsed() throws Exception {
    final RepositoryService repositoryServiceMock = EasyMock.createMock(RepositoryService.class);

    final GetFileController ctrl = new GetFileController();

    repositoryConfiguration.setCacheUsed(false);

    final ImageScaler imageScaler = new ImageScaler() {
      @Override
      public BufferedImage getThumbnail(BufferedImage image, int maxSize) {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
      }
    };

    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();

    ctrl.setApplication(application);
    ctrl.setRepositoryService(repositoryServiceMock);
    ctrl.setMimeFileTypeMap(mftm);
    ctrl.setImageScaler(imageScaler);
    ctrl.setImageFormatName("jpg");

    final BaseCommand command = new BaseCommand();
    command.setName(repositoryName);
    command.setPath("/test/target.jpg");

    request.addParameter(GetFileController.DISPLAY_REQUEST_PARAMETER, GetFileController.DISPLAY_TYPE_THUMBNAIL);

    repositoryServiceMock.getFileContents((SVNConnection) EasyMock.isNull(), EasyMock.matches(command.getPath()),
        EasyMock.eq(-1L), (OutputStream) EasyMock.anyObject());

    assertEquals(0, response.getContentAsByteArray().length);

    EasyMock.replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, 100, null, request, response, null);
    EasyMock.verify(repositoryServiceMock);

    assertTrue(((String) response.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).contains("target.jpg"));
    assertEquals(622, response.getContentAsByteArray().length);
  }

  @Test
  public void testCacheUsed() throws Exception {
    final RepositoryService repositoryServiceMock = EasyMock.createMock(RepositoryService.class);

    final ObjectCacheManager objectCacheManager = new ObjectCacheManager(
        configDirectory, 0, false, false, 0, 0, false, 0) {
      @Override
      protected ObjectCache createCache(RepositoryName cacheName) throws CacheException {
        return new DefaultObjectCache(cacheName.toString(), null, 1000, false, false, 0, 0, false, 0);
      }
    };
    objectCacheManager.register(repositoryName);

    final GetFileController ctrl = new GetFileController();

    repositoryConfiguration.setCacheUsed(true);

    final ImageScaler imageScaler = new ImageScaler() {
      @Override
      public BufferedImage getThumbnail(BufferedImage image, int maxSize) {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
      }
    };

    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();

    ctrl.setApplication(application);
    ctrl.setRepositoryService(repositoryServiceMock);
    ctrl.setMimeFileTypeMap(mftm);
    ctrl.setImageScaler(imageScaler);
    ctrl.setImageFormatName("png");
    ctrl.setObjectCacheManager(objectCacheManager);

    final BaseCommand command = new BaseCommand();
    command.setName(repositoryName);
    command.setPath("/test/target.png");

    request.addParameter(GetFileController.DISPLAY_REQUEST_PARAMETER, GetFileController.DISPLAY_TYPE_THUMBNAIL);

    repositoryServiceMock.getFileContents((SVNConnection) EasyMock.isNull(), EasyMock.matches(command.getPath()),
        EasyMock.eq(-1L), (OutputStream) EasyMock.anyObject());

    assertEquals(0, response.getContentAsByteArray().length);

    EasyMock.replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, 100, null, request, response, null);
    EasyMock.verify(repositoryServiceMock);

    assertTrue(((String) response.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).contains("target.png"));
    assertEquals(68, response.getContentAsByteArray().length);

    EasyMock.reset(repositoryServiceMock);

    // Thumbnail is now cached - verify that it's used
    EasyMock.replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, 100, null, request, response, null);
    EasyMock.verify(repositoryServiceMock);
  }

  @Test
  public void testSvnHandleGetFile() throws Exception {
    final BaseCommand command = new BaseCommand();
    command.setName(repositoryName);
    command.setPath("/testfile.txt");

    final GetFileController ctrl = new GetFileController();
    ctrl.setApplication(application);
    ctrl.setRepositoryService(mock(RepositoryService.class));

    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();
    ctrl.setMimeFileTypeMap(mftm);
    final ModelAndView modelAndView;

    final MockHttpServletResponse res = new MockHttpServletResponse();
    ctrl.setServletContext(mockServletContext);
    modelAndView = ctrl.svnHandle(null, command, 100, null, request, res, null);

    assertNull(modelAndView);
    assertEquals("text/plain", res.getContentType());
    assertTrue(((String) res.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).startsWith("attachment"));
  }

}
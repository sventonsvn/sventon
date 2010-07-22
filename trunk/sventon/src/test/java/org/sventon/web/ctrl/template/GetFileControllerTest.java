package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.SVNRepositoryStub;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.appl.ObjectCacheManager;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.cache.CacheException;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.cache.objectcache.ObjectCacheImpl;
import org.sventon.model.RepositoryName;
import org.sventon.service.RepositoryService;
import org.sventon.service.RepositoryServiceImpl;
import org.sventon.util.ImageScaler;
import org.sventon.util.WebUtils;
import org.sventon.web.command.BaseCommand;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

public class GetFileControllerTest extends TestCase {

  private Application application;
  private ConfigDirectory configDirectory;
  private RepositoryConfiguration repositoryConfiguration;
  private RepositoryName repositoryName;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  protected void setUp() throws Exception {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();

    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");

    repositoryName = new RepositoryName("test");

    configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    configDirectory.setServletContext(servletContext);

    application = new Application(configDirectory);
    application.setConfigured(true);

    repositoryConfiguration = new RepositoryConfiguration(repositoryName.toString());
    application.addConfiguration(repositoryConfiguration);
  }

  public void testSvnHandleGetImageAsInline() throws Exception {
    final BaseCommand command = new BaseCommand();
    command.setName(repositoryName);
    command.setPath("/testimage.gif");

    final GetFileController ctrl = new GetFileController();
    ctrl.setApplication(application);
    ctrl.setRepositoryService(new RepositoryServiceImpl());

    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();
    ctrl.setMimeFileTypeMap(mftm);
    final ModelAndView modelAndView;

    request.addParameter(GetFileController.DISPLAY_REQUEST_PARAMETER, GetFileController.CONTENT_DISPOSITION_INLINE);

    final MockHttpServletResponse res = new MockHttpServletResponse();
    modelAndView = ctrl.svnHandle(new TestRepository(), command, 100, null, request, res, null);

    assertNull(modelAndView);
    assertEquals("image/gif", res.getContentType());
    assertTrue(((String) res.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).startsWith("inline"));
  }

  public void testSvnHandleGetFileAsAttachment() throws Exception {
    final BaseCommand command = new BaseCommand();
    command.setName(repositoryName);
    command.setPath("/testimage.gif");

    final GetFileController ctrl = new GetFileController();
    ctrl.setApplication(application);
    ctrl.setRepositoryService(new RepositoryServiceImpl());

    request.addParameter(GetFileController.DISPLAY_REQUEST_PARAMETER, (String) null);

    final MockHttpServletResponse mockResponse = new MockHttpServletResponse();
    final ModelAndView modelAndView = ctrl.svnHandle(new TestRepository(), command, 100, null, request, mockResponse, null);

    assertNull(modelAndView);

    assertEquals(WebUtils.APPLICATION_OCTET_STREAM, mockResponse.getContentType());
    assertTrue(((String) mockResponse.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).startsWith("attachment"));
  }

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

    repositoryServiceMock.getFileContents((SVNRepository) EasyMock.isNull(), EasyMock.matches(command.getPath()),
        EasyMock.eq(-1L), (OutputStream) EasyMock.anyObject());

    assertEquals(0, response.getContentAsByteArray().length);

    EasyMock.replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, 100, null, request, response, null);
    EasyMock.verify(repositoryServiceMock);

    assertTrue(((String) response.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).contains("target.jpg"));
    assertEquals(622, response.getContentAsByteArray().length);
  }

  public void testCacheUsed() throws Exception {
    final RepositoryService repositoryServiceMock = EasyMock.createMock(RepositoryService.class);

    final ObjectCacheManager objectCacheManager = new ObjectCacheManager(
        configDirectory, 0, false, false, 0, 0, false, 0) {
      @Override
      protected ObjectCache createCache(RepositoryName cacheName) throws CacheException {
        return new ObjectCacheImpl(cacheName.toString(), null, 1000, false, false, 0, 0, false, 0);
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

    EasyMock.expect(repositoryServiceMock.getFileChecksum(null, command.getPath(), -1L)).andStubReturn("checksum");

    repositoryServiceMock.getFileContents((SVNRepository) EasyMock.isNull(), EasyMock.matches(command.getPath()),
        EasyMock.eq(-1L), (OutputStream) EasyMock.anyObject());

    assertEquals(0, response.getContentAsByteArray().length);

    EasyMock.replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, 100, null, request, response, null);
    EasyMock.verify(repositoryServiceMock);

    assertTrue(((String) response.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).contains("target.png"));
    assertEquals(68, response.getContentAsByteArray().length);

    EasyMock.reset(repositoryServiceMock);

    // Thumbnail is now cached - verify that it's used

    EasyMock.expect(repositoryServiceMock.getFileChecksum(null, command.getPath(), -1L)).andStubReturn("checksum");

    EasyMock.replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, 100, null, request, response, null);
    EasyMock.verify(repositoryServiceMock);
  }

  static class TestRepository extends SVNRepositoryStub {

    public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
      return 0;
    }
  }


}
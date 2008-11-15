package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.InstanceConfiguration;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.objectcache.ObjectCache;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheImpl;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheManager;
import de.berlios.sventon.service.RepositoryService;
import de.berlios.sventon.util.ImageScaler;
import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;

public class GetThumbnailControllerTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testCacheNotUsed() throws Exception {
    final RepositoryService repositoryServiceMock = EasyMock.createMock(RepositoryService.class);

    final String repositoryName = "test";
    final HttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    final GetThumbnailController ctrl = new GetThumbnailController();

    final InstanceConfiguration instanceConfig = new InstanceConfiguration(repositoryName);
    instanceConfig.setCacheUsed(false);

    final ImageScaler imageScaler = new ImageScaler() {
      public BufferedImage getThumbnail(BufferedImage image, int maxSize) {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
      }
    };

    final Application application = new Application(new File(TEMPDIR), "filename");
    application.setConfigured(true);
    application.addInstance(instanceConfig);

    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();

    ctrl.setApplication(application);
    ctrl.setRepositoryService(repositoryServiceMock);
    ctrl.setMimeFileTypeMap(mftm);
    ctrl.setImageScaler(imageScaler);
    ctrl.setImageFormatName("jpg");

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setName(repositoryName);
    command.setPath("/test/target.jpg");

    repositoryServiceMock.getFile((SVNRepository) EasyMock.isNull(), EasyMock.matches(command.getPath()),
        EasyMock.eq(-1L), (OutputStream) EasyMock.anyObject());

    assertEquals(0, response.getContentAsByteArray().length);

    EasyMock.replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, SVNRevision.HEAD, null, request, response, null);
    EasyMock.verify(repositoryServiceMock);

    assertTrue(((String) response.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).contains("target.jpg"));
    assertEquals(622, response.getContentAsByteArray().length);
  }

  public void testCacheUsed() throws Exception {
    final String repositoryName = "test";

    final RepositoryService repositoryServiceMock = EasyMock.createMock(RepositoryService.class);

    final ObjectCacheManager objectCacheManager = new ObjectCacheManager(null, 0, false, false, 0, 0, false, 0) {
      protected ObjectCache createCache(String cacheName) throws CacheException {
        return new ObjectCacheImpl(repositoryName, null, 1000, false, false, 0, 0, false, 0);
      }
    };
    objectCacheManager.register(repositoryName);

    final HttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    final GetThumbnailController ctrl = new GetThumbnailController();

    final InstanceConfiguration instanceConfig = new InstanceConfiguration(repositoryName);
    instanceConfig.setCacheUsed(true);

    final ImageScaler imageScaler = new ImageScaler() {
      public BufferedImage getThumbnail(BufferedImage image, int maxSize) {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
      }
    };

    final Application application = new Application(new File(TEMPDIR), "filename");
    application.setConfigured(true);
    application.addInstance(instanceConfig);

    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();

    ctrl.setApplication(application);
    ctrl.setRepositoryService(repositoryServiceMock);
    ctrl.setMimeFileTypeMap(mftm);
    ctrl.setImageScaler(imageScaler);
    ctrl.setImageFormatName("png");
    ctrl.setObjectCacheManager(objectCacheManager);

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setName(repositoryName);
    command.setPath("/test/target.png");

    EasyMock.expect(repositoryServiceMock.getFileChecksum(null, command.getPath(), -1L)).andStubReturn("checksum");

    repositoryServiceMock.getFile((SVNRepository) EasyMock.isNull(), EasyMock.matches(command.getPath()),
        EasyMock.eq(-1L), (OutputStream) EasyMock.anyObject());

    assertEquals(0, response.getContentAsByteArray().length);

    EasyMock.replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, SVNRevision.HEAD, null, request, response, null);
    EasyMock.verify(repositoryServiceMock);

    assertTrue(((String) response.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).contains("target.png"));
    assertEquals(68, response.getContentAsByteArray().length);

    EasyMock.reset(repositoryServiceMock);

    // Thumbnail is now cached - verify that it's used

    EasyMock.expect(repositoryServiceMock.getFileChecksum(null, command.getPath(), -1L)).andStubReturn("checksum");

    EasyMock.replay(repositoryServiceMock);
    ctrl.svnHandle(null, command, SVNRevision.HEAD, null, request, response, null);
    EasyMock.verify(repositoryServiceMock);
  }

}

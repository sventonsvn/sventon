/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.repository.cache.objectcache.ObjectCache;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheKey;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheManager;
import de.berlios.sventon.util.ImageScaler;
import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.activation.FileTypeMap;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Controller used when downloading single image files as thumbnails.
 *
 * @author jesper@users.berlios.de
 */
public final class GetThumbnailController extends AbstractSVNTemplateController implements Controller {

  /**
   * The mime/file type map.
   */
  private FileTypeMap mimeFileTypeMap;

  /**
   * Object cache manager instance.
   */
  private ObjectCacheManager objectCacheManager;

  /**
   * Image format name to use when generating thumbnails.
   */
  private String imageFormatName;

  /**
   * Specifies the maximum horizontal/vertical size (in pixels) for a generated thumbnail image.
   */
  private int maxThumbnailSize;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Getting file as 'thumbnail'");

    final OutputStream output = response.getOutputStream();

    if (!mimeFileTypeMap.getContentType(svnCommand.getPath()).startsWith("image")) {
      logger.error("File '" + svnCommand.getTarget() + "' is not a image file");
      return null;
    }

    prepareResponse(response, svnCommand);

    final URL fullSizeImageUrl = createFullSizeImageURL(request);
    final boolean cacheUsed = getRepositoryConfiguration(svnCommand.getName()).isCacheUsed();

    ObjectCache objectCache = null;
    ObjectCacheKey cacheKey = null;

    if (cacheUsed) {
      final String checksum = getRepositoryService().getFileChecksum(repository, svnCommand.getPath(), svnCommand.getRevisionNumber());
      objectCache = objectCacheManager.getCache(svnCommand.getName());
      cacheKey = new ObjectCacheKey(svnCommand.getPath(), checksum);
      logger.debug("Using cachekey: " + cacheKey);
      final byte[] thumbnailData = (byte[]) objectCache.get(cacheKey);
      // Check if the thumbnail exists on the cache
      if (thumbnailData != null) {
        // Writing cached thumbnail image to output stream
        output.write(thumbnailData);
      }
    }

    final byte[] thumbnailData = createThumbnail(fullSizeImageUrl);

    if (cacheUsed) {
      // Putting created thumbnail image into the cache.
      logger.debug("Caching thumbnail. Using cachekey: " + cacheKey);
      objectCache.put(cacheKey, thumbnailData);
    }

    output.write(thumbnailData);
    output.flush();
    output.close();
    return null;
  }

  /**
   * Creates a thumbnail version of a full size image.
   *
   * @param fullSizeImageUrl URL to the full size version of the image.
   * @return array  of image bytes
   */
  private byte[] createThumbnail(final URL fullSizeImageUrl) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      logger.debug("Getting full size image from url: " + fullSizeImageUrl);
      final ImageScaler imageScaler = new ImageScaler(ImageIO.read(fullSizeImageUrl));
      ImageIO.write(imageScaler.createThumbnail(maxThumbnailSize), imageFormatName, baos);
    } catch (final IOException ioex) {
      logger.warn("Unable to get thumbnail", ioex);
    }
    return baos.toByteArray();
  }

  /**
   * Prepares the response by setting headers and content type.
   *
   * @param response   Response.
   * @param svnCommand Command.
   */
  protected void prepareResponse(final HttpServletResponse response, final SVNBaseCommand svnCommand) {
    response.setHeader(WebUtils.CONTENT_DISPOSITION_HEADER, "inline; filename=\"" + svnCommand.getTarget() + "\"");
    response.setContentType(mimeFileTypeMap.getContentType(svnCommand.getPath()));
  }

  /**
   * Creates a URL string for accessing the full size image.
   *
   * @param request Request.
   * @return URL.
   * @throws MalformedURLException if unable to construct URL.
   */
  private URL createFullSizeImageURL(final HttpServletRequest request) throws MalformedURLException {
    final StringBuilder urlString = new StringBuilder(
        request.getRequestURL().toString().replaceAll("getthumb.svn", "get.svn"));  //TODO: remove ugly hard-coding!
    urlString.append("?");
    urlString.append(request.getQueryString());
    return new URL(urlString.toString());
  }

  /**
   * Sets the object cache manager instance.
   *
   * @param objectCacheManager The cache manager instance.
   */
  public void setObjectCacheManager(final ObjectCacheManager objectCacheManager) {
    this.objectCacheManager = objectCacheManager;
  }

  /**
   * Sets the image format name.
   *
   * @param imageFormatName The format name, e.g. <tt>png</tt>.
   */
  public void setImageFormatName(final String imageFormatName) {
    this.imageFormatName = imageFormatName;
  }

  /**
   * Sets the maximum vertical/horizontal size in pixels for the generated thumbnail images.
   *
   * @param maxSize Size in pixels.
   */
  public void setMaxThumbnailSize(final int maxSize) {
    this.maxThumbnailSize = maxSize;
  }

  /**
   * Sets the mime/file type map.
   *
   * @param mimeFileTypeMap Map.
   */
  public void setMimeFileTypeMap(final FileTypeMap mimeFileTypeMap) {
    this.mimeFileTypeMap = mimeFileTypeMap;
  }

}

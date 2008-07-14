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
package org.sventon.web.ctrl.template;

import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.cache.objectcache.ObjectCacheKey;
import org.sventon.cache.objectcache.ObjectCacheManager;
import org.sventon.util.EncodingUtils;
import org.sventon.util.ImageScaler;
import static org.sventon.util.WebUtils.CONTENT_DISPOSITION_HEADER;
import org.sventon.web.command.SVNBaseCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.ctrl.template.AbstractSVNTemplateController;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.activation.FileTypeMap;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Controller used when downloading single image files as thumbnails.
 *
 * @author jesper@users.berlios.de
 */
public final class GetThumbnailController extends AbstractSVNTemplateController {

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

    prepareResponse(request, response, svnCommand);

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

    final byte[] thumbnailData = createThumbnail(repository, svnCommand);

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
   * @param repository Repository
   * @param svnCommand Command
   * @return array of image bytes
   */
  private byte[] createThumbnail(final SVNRepository repository, final SVNBaseCommand svnCommand) {
    logger.debug("Getting image file: " + svnCommand.getPath());
    final ByteArrayOutputStream fullSizeImageData = new ByteArrayOutputStream();
    final ByteArrayOutputStream thumbnailImageData = new ByteArrayOutputStream();
    try {
      getRepositoryService().getFile(repository, svnCommand.getPath(), svnCommand.getRevisionNumber(), fullSizeImageData);
      final ImageScaler imageScaler = new ImageScaler(ImageIO.read(new ByteArrayInputStream(
          fullSizeImageData.toByteArray())));
      ImageIO.write(imageScaler.createThumbnail(maxThumbnailSize), imageFormatName, thumbnailImageData);
    } catch (final Exception ex) {
      logger.warn("Unable to create thumbnail", ex);
    }
    return thumbnailImageData.toByteArray();
  }

  /**
   * Prepares the response by setting headers and content type.
   *
   * @param request    Request.
   * @param response   Response.
   * @param svnCommand Command.
   */
  protected void prepareResponse(final HttpServletRequest request, final HttpServletResponse response,
                                 final SVNBaseCommand svnCommand) {
    response.setHeader(CONTENT_DISPOSITION_HEADER, "inline; filename=\"" + EncodingUtils.encodeFilename(svnCommand.getTarget(), request) + "\"");
    response.setContentType(mimeFileTypeMap.getContentType(svnCommand.getPath()));
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

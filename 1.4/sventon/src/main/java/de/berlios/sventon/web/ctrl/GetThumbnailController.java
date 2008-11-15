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
import de.berlios.sventon.util.EncodingUtils;
import de.berlios.sventon.util.ImageScaler;
import static de.berlios.sventon.util.WebUtils.CONTENT_DISPOSITION_HEADER;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.activation.FileTypeMap;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

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
   * Image scaler.
   */
  private ImageScaler imageScaler;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Getting file as 'thumbnail'");

    final OutputStream output = response.getOutputStream();

    if (!mimeFileTypeMap.getContentType(svnCommand.getPath()).startsWith("image")) {
      logger.error("File '" + svnCommand.getTarget() + "' is not a image file");
      return null;
    }

    prepareResponse(request, response, svnCommand);

    final boolean cacheUsed = getInstanceConfiguration(svnCommand.getName()).isCacheUsed();

    ObjectCache objectCache;
    ObjectCacheKey cacheKey;
    byte[] thumbnailData;

    if (cacheUsed) {
      final String checksum = getRepositoryService().getFileChecksum(repository, svnCommand.getPath(), revision.getNumber());
      objectCache = objectCacheManager.getCache(svnCommand.getName());
      cacheKey = new ObjectCacheKey(svnCommand.getPath(), checksum);
      logger.debug("Using cachekey: " + cacheKey);
      thumbnailData = (byte[]) objectCache.get(cacheKey);

      if (thumbnailData == null) {
        // Thumbnail did not exist - create it and cache it
        thumbnailData = createThumbnail(repository, svnCommand, revision);
        logger.debug("Caching thumbnail. Using cachekey: " + cacheKey);
        objectCache.put(cacheKey, thumbnailData);
      }
    } else {
      // Cache is not used - always recreate the thumbnail
      thumbnailData = createThumbnail(repository, svnCommand, revision);
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
   * @param revision   Revision
   * @return array of image bytes
   */
  private byte[] createThumbnail(final SVNRepository repository, final SVNBaseCommand svnCommand, final SVNRevision revision) {
    logger.debug("Creating thumbnail for: " + svnCommand.getPath());
    final ByteArrayOutputStream fullSizeImageData = new ByteArrayOutputStream();
    final ByteArrayOutputStream thumbnailImageData = new ByteArrayOutputStream();
    try {
      getRepositoryService().getFile(repository, svnCommand.getPath(), revision.getNumber(), fullSizeImageData);
      final BufferedImage image = ImageIO.read(new ByteArrayInputStream(fullSizeImageData.toByteArray()));
      ImageIO.write(imageScaler.getThumbnail(image, maxThumbnailSize), imageFormatName, thumbnailImageData);
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
   * Sets the image scaler.
   *
   * @param imageScaler Image scaler
   */
  public void setImageScaler(final ImageScaler imageScaler) {
    this.imageScaler = imageScaler;
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

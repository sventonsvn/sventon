/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl.template;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.appl.ObjectCacheManager;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.cache.objectcache.ObjectCacheKey;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.EncodingUtils;
import org.sventon.util.ImageScaler;
import org.sventon.util.WebUtils;
import static org.sventon.util.WebUtils.CONTENT_DISPOSITION_HEADER;
import org.sventon.web.command.BaseCommand;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.activation.FileTypeMap;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Controller used when downloading single files.
 * <p/>
 * Non-image files will be gotten as <i>attachment</i> using it's corresponding
 * mime-type or <i>application/octet-stream</i> if unknown.
 * <p/>
 * Image files can be gotten in two different ways.
 * <ul>
 * <li><b>inline</b> - Gets the image with correct content type.
 * Image will be displayed inline in browser.</li>
 * <li><b>attachment</b> - Gets the image with content type
 * application/octetstream. A download dialog will appear in browser.</li>
 * </ul>
 *
 * @author jesper@sventon.org
 */
public class GetFileController extends AbstractTemplateController {

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
   * Display parameter.
   */
  public static final String DISPLAY_REQUEST_PARAMETER = "disp";

  /**
   * Inline mode.
   */
  public static final String CONTENT_DISPOSITION_INLINE = "inline";

  /**
   * Attachment mode.
   */
  public static final String CONTENT_DISPOSITION_ATTACHMENT = "attachment";

  /**
   * Thumbnail mode.
   */
  public static final String DISPLAY_TYPE_THUMBNAIL = "thumbnail";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String displayType = ServletRequestUtils.getStringParameter(request, DISPLAY_REQUEST_PARAMETER, CONTENT_DISPOSITION_ATTACHMENT);
    final OutputStream output = response.getOutputStream();
    final boolean cacheUsed = getRepositoryConfiguration(command.getName()).isCacheUsed();
    ObjectCache objectCache;
    ObjectCacheKey cacheKey;
    byte[] thumbnailData;

    if (CONTENT_DISPOSITION_ATTACHMENT.equals(displayType)) {
      logger.debug("Getting file as 'attachment'");
      prepareResponse(CONTENT_DISPOSITION_ATTACHMENT, request, response, getMimeType(command.getTarget().toLowerCase()), command);
      getRepositoryService().getFileContents(repository, command.getPath(), command.getRevisionNumber(), output);
    } else if (CONTENT_DISPOSITION_INLINE.equals(displayType)) {
      if (isImageFile(command.getPath())) {
        logger.debug("Getting file as 'inline'");
        prepareResponse(CONTENT_DISPOSITION_INLINE, request, response, getContentType(command.getPath()), command);
        getRepositoryService().getFileContents(repository, command.getPath(), command.getRevisionNumber(), output);
      } else {
        logger.warn("File [" + command.getTarget() + "] is not an image file - unable to display it 'inline'");
        prepareResponse(CONTENT_DISPOSITION_ATTACHMENT, request, response, getMimeType(command.getTarget().toLowerCase()), command);
        getRepositoryService().getFileContents(repository, command.getPath(), command.getRevisionNumber(), output);
      }
    } else if (DISPLAY_TYPE_THUMBNAIL.equals(displayType)) {
      if (isImageFile(command.getPath())) {
        logger.debug("Getting file as 'thumbnail'");
        prepareResponse(CONTENT_DISPOSITION_INLINE, request, response, getContentType(command.getPath()), command);
        if (cacheUsed) {
          final String checksum = getRepositoryService().getFileChecksum(repository, command.getPath(), command.getRevisionNumber());
          objectCache = objectCacheManager.getCache(command.getName());
          cacheKey = new ObjectCacheKey(command.getPath(), checksum);
          logger.debug("Using cachekey: " + cacheKey);
          thumbnailData = (byte[]) objectCache.get(cacheKey);

          if (thumbnailData == null) {
            // Thumbnail did not exist - create it and cache it
            thumbnailData = createThumbnail(repository, command);
            logger.debug("Caching thumbnail. Using cachekey: " + cacheKey);
            objectCache.put(cacheKey, thumbnailData);
            objectCache.flush();
          }
        } else {
          // Cache is not used - always recreate the thumbnail
          thumbnailData = createThumbnail(repository, command);
        }
        output.write(thumbnailData);
      } else {
        logger.error("File '" + command.getTarget() + "' is not a image file");
      }
    } else {
      throw new IllegalArgumentException("Illegal parameter '" + DISPLAY_REQUEST_PARAMETER + "':" + displayType);
    }
    output.flush();
    output.close();
    return null;
  }

  protected String getContentType(final String path) {
    return mimeFileTypeMap.getContentType(path);
  }

  protected String getMimeType(final String path) {
    String mimeType = WebUtils.APPLICATION_OCTET_STREAM;
    try {
      mimeType = getServletContext().getMimeType(path);
    } catch (IllegalStateException ise) {
      logger.debug("Could not get mimeType for file as an ApplicationContext does not exist. Using default");
    }
    return mimeType != null ? mimeType : WebUtils.APPLICATION_OCTET_STREAM;
  }

  protected boolean isImageFile(final String path) {
    return mimeFileTypeMap.getContentType(path).startsWith("image");
  }

  /**
   * Prepares the response by setting headers and content type.
   *
   * @param disposition Content disposition.
   * @param request     Request.
   * @param response    Response.
   * @param contentType Content type.
   * @param command     Command.
   */
  protected void prepareResponse(final String disposition, final HttpServletRequest request,
                                 final HttpServletResponse response, final String contentType,
                                 final BaseCommand command) {
    response.setContentType(contentType);
    response.setHeader(CONTENT_DISPOSITION_HEADER, disposition + "; filename=\"" +
        EncodingUtils.encodeFilename(command.getTarget(), request) + "\"");
  }

  /**
   * Creates a thumbnail version of a full size image.
   *
   * @param repository Repository
   * @param command    Command
   * @return array of image bytes
   */
  private byte[] createThumbnail(final SVNRepository repository, final BaseCommand command) {
    logger.debug("Creating thumbnail for: " + command.getPath());
    final ByteArrayOutputStream fullSizeImageData = new ByteArrayOutputStream();
    final ByteArrayOutputStream thumbnailImageData = new ByteArrayOutputStream();
    try {
      getRepositoryService().getFileContents(repository, command.getPath(), command.getRevisionNumber(), fullSizeImageData);
      final BufferedImage image = ImageIO.read(new ByteArrayInputStream(fullSizeImageData.toByteArray()));
      ImageIO.write(imageScaler.getThumbnail(image, maxThumbnailSize), imageFormatName, thumbnailImageData);
    } catch (final Exception ex) {
      logger.warn("Unable to create thumbnail", ex);
    }
    return thumbnailImageData.toByteArray();
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

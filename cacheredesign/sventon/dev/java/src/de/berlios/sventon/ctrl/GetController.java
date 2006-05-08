/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.ctrl;

import de.berlios.sventon.cache.SventonCache;
import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.util.ImageUtil;
import de.berlios.sventon.util.PathUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.mail.internet.MimeUtility;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Controller used when downloading single files.
 * Image files can be gotten in three different ways.
 * <ul>
 * <li><b>thumb</b> - Gets a thumbnail version of the image.</li>
 * <li><b>inline</b> - Gets the image with correct content type.
 * Image will be displayed inline in browser.</li>
 * <li><b>attachment</b> - Gets the image with content type
 * application/octetstream. A download dialog will appear in browser.</li>
 * </ul>
 *
 * @author jesper@users.berlios.de
 */
public class GetController extends AbstractSVNTemplateController implements Controller {

  private ImageUtil imageUtil;
  private SventonCache cache;

  public static final String THUMBNAIL_FORMAT = "png";
  public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
  public static final String DISPLAY_REQUEST_PARAMETER = "disp";
  public static final String DISPLAY_TYPE_THUMBNAIL = "thumb";
  public static final String DISPLAY_TYPE_INLINE = "inline";


  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand, final SVNRevision revision,
                                   final HttpServletRequest request, final HttpServletResponse response, final BindException exception)
      throws SventonException, SVNException {

    logger.debug("Getting file: " + svnCommand.getPath());

    final String displayType = request.getParameter(DISPLAY_REQUEST_PARAMETER);
    final ServletOutputStream output;
    final ByteArrayOutputStream baos;
    logger.debug("displayType: " + displayType);

    try {
      output = response.getOutputStream();

      if (DISPLAY_TYPE_THUMBNAIL.equals(displayType)) {
        logger.debug("Getting file as 'thumbnail'");
        if (!imageUtil.isImageFileExtension(PathUtil.getFileExtension(svnCommand.getPath()))) {
          logger.error("File '" + svnCommand.getTarget() + "' is not a image file");
          return null;
        }

        response.setHeader("Content-disposition", "inline; filename=\"" + svnCommand.getTarget() + "\"");

        // Check if the thumbnail exists on the cache
        final HashMap properties = new HashMap();
        repository.getFile(svnCommand.getPath(), revision.getNumber(), properties, null);
        logger.debug(properties);
        String cacheKey = (String) properties.get(SVNProperty.CHECKSUM) + svnCommand.getPath();
        logger.debug("Using cachekey: " + cacheKey);
        byte[] thumbnailData = (byte[]) cache.get(cacheKey);
        if (thumbnailData != null) {
          // Writing cached thumbnail image to ServletOutputStream
          output.write(thumbnailData);
        } else {
          // Thumbnail was not in the cache.
          // Create the thumbnail.
          final StringBuffer urlString = request.getRequestURL();
          urlString.append("?");
          urlString.append(request.getQueryString().replaceAll(DISPLAY_REQUEST_PARAMETER + "=" + DISPLAY_TYPE_THUMBNAIL, DISPLAY_REQUEST_PARAMETER + "=" + DISPLAY_TYPE_INLINE));
          final URL url = new URL(urlString.toString());
          logger.debug("Getting full size image from url: " + url);
          BufferedImage image = ImageIO.read(url);
          int orgWidth = image.getWidth();
          int orgHeight = image.getHeight();

          // Get preferred thumbnail dimension.
          final Dimension thumbnailSize = imageUtil.getThumbnailSize(orgWidth, orgHeight);
          logger.debug("Thumbnail size: " + thumbnailSize.toString());
          // Resize image.
          final Image rescaled = image.getScaledInstance((int) thumbnailSize.getWidth(), (int) thumbnailSize.getHeight(), Image.SCALE_AREA_AVERAGING);
          final BufferedImage biRescaled = imageUtil.toBufferedImage(rescaled, BufferedImage.TYPE_INT_ARGB);
          response.setContentType(imageUtil.getContentType(PathUtil.getFileExtension(svnCommand.getPath())));

          // Write thumbnail to output stream.
          baos = new ByteArrayOutputStream();
          ImageIO.write(biRescaled, THUMBNAIL_FORMAT, baos);

          // Putting created thumbnail image into the cache.
          logger.debug("Caching thumbnail. Using cachekey: " + cacheKey);
          cache.put(cacheKey, baos.toByteArray());
          // Write thumbnail to ServletOutputStream.
          output.write(baos.toByteArray());
        }
      } else {
        if (DISPLAY_TYPE_INLINE.equals(displayType)
            && imageUtil.isImageFileExtension(PathUtil.getFileExtension(svnCommand.getPath()))) {
          logger.debug("Getting file as 'inline'");
          response.setContentType(imageUtil.getContentType(PathUtil.getFileExtension(svnCommand.getPath())));
          response.setHeader("Content-disposition", "inline; filename=\"" + encodeFilename(svnCommand.getTarget(), request) + "\"");
        } else {
          logger.debug("Getting file as 'attachment'");
          String mimeType = null;
          try {
            mimeType = getServletContext().getMimeType(svnCommand.getTarget().toLowerCase());
          } catch (IllegalStateException ise) {
            logger.debug("Could not get mimeType for file as an ApplicationContext does not exist. Using default");
          }
          if (mimeType == null) {
            response.setContentType(DEFAULT_CONTENT_TYPE);
          } else {
            response.setContentType(mimeType);
          }
          response.setHeader("Content-disposition", "attachment; filename=\"" + encodeFilename(svnCommand.getTarget(), request) + "\"");
        }
        // Get the image data and write it to the outputStream.
        repository.getFile(svnCommand.getPath(), revision.getNumber(), null, output);
      }
      output.flush();
      output.close();
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
    return null;
  }

  /**
   * Hack to get the correct format of the file name, based on <code>USER-AGENT</code> string.
   * File name will be returned as-is if unable to parse <code>USER-AGENT</code>.
   *
   * @param filename File name to encode
   * @param request The request
   * @return The coded file name.
   */
  private String encodeFilename(final String filename, final HttpServletRequest request) {
    final String userAgent = request.getHeader("USER-AGENT");
    String codedFilename = null;
    try {
      if (null != userAgent && -1 != userAgent.indexOf("MSIE")) {
          codedFilename = URLEncoder.encode(filename, "UTF-8");
      } else if (null != userAgent && -1 != userAgent.indexOf("Mozilla")) {
        codedFilename = MimeUtility.encodeText(filename, "UTF-8", "B");
      }
    } catch (UnsupportedEncodingException uee) {
      // Silently ignore
    }
    return codedFilename != null ? codedFilename : filename;
  }

  /**
   * Sets the <code>ImageUtil</code> helper instance.
   *
   * @param imageUtil The instance
   * @see de.berlios.sventon.util.ImageUtil
   */
  public void setImageUtil(final ImageUtil imageUtil) {
    this.imageUtil = imageUtil;
  }

  /**
   * Sets the cache instance.
   *
   * @param cache The cache instance.
   */
  public void setCache(final SventonCache cache) {
    this.cache = cache;
  }
}

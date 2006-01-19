/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.util.ImageUtil;
import de.berlios.sventon.util.PathUtil;
import de.berlios.sventon.util.SventonCache;
import de.berlios.sventon.svnsupport.SventonException;
import net.sf.ehcache.CacheException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Controller used when downloading single files.
 * Image files can be gotten in three different ways.
 * <ul>
 *  <li><b>thumb</b> - Gets a thumbnail version of the image.</li>
 *  <li><b>inline</b> - Gets the image with correct content type.
 * Image will be displayed inline in browser.</li>
 *  <li><b>attachment</b> - Gets the image with content type
 * application/octetstream. A download dialog will appear in browser.</li>
 * </ul>
 * @author jesper@users.berlios.de
 */
public class GetController extends AbstractSVNTemplateController implements Controller {

  private ImageUtil imageUtil;

  public static final String THUMBNAIL_FORMAT = "png";
  public static final String DEFAULT_CONTENT_TYPE = "application/octetstream";
  public static final String DISPLAY_REQUEST_PARAMETER = "disp";
  public static final String DISPLAY_TYPE_THUMBNAIL = "thumb";
  public static final String DISPLAY_TYPE_INLINE = "inline";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response, BindException exception) throws SventonException, SVNException {

    logger.debug("Getting file: " + svnCommand.getPath());

    String displayType = request.getParameter(DISPLAY_REQUEST_PARAMETER);
    ServletOutputStream output = null;
    ByteArrayOutputStream baos = null;
    logger.debug("displayType: " + displayType);

    try {
      output = response.getOutputStream();

      if (DISPLAY_TYPE_THUMBNAIL.equals(displayType)) {
        logger.debug("Getting file as 'thumbnail'");
        if (!getImageUtil().isImageFileExtension(PathUtil.getFileExtension(svnCommand.getPath()))) {
          logger.error("File '" + svnCommand.getTarget() + "' is not a image file.");
          return null;
        }

        response.setHeader("Content-disposition", "inline; filename=\"" + svnCommand.getTarget() + "\"");

        // Check if the thumbnail exists on the cache
        HashMap properties = new HashMap();
        repository.getFile(svnCommand.getPath(), revision.getNumber(), properties, null);
        logger.debug(properties);
        String cacheKey = (String) properties.get(SVNProperty.CHECKSUM) + svnCommand.getPath();
        logger.debug("Using cachekey: " + cacheKey);
        byte[] thumbnailData = null;
        try {
          thumbnailData = (byte[]) SventonCache.INSTANCE.get(cacheKey);
        } catch(CacheException ce) {
          logger.warn(ce);
        }
        if (thumbnailData != null) {
          // Writing cached thumbnail image to ServletOutputStream
          output.write(thumbnailData);
        } else {
          // Thumbnail was not in the cache.
          // Create the thumbnail.
          StringBuffer urlString = request.getRequestURL();
          urlString.append("?");
          urlString.append(request.getQueryString().replaceAll(DISPLAY_REQUEST_PARAMETER + "=" + DISPLAY_TYPE_THUMBNAIL, DISPLAY_REQUEST_PARAMETER + "=" + DISPLAY_TYPE_INLINE));
          URL url = new URL(urlString.toString());
          logger.debug("Getting full size image from url: " + url);
          BufferedImage image = ImageIO.read(url);
          int orgWidth = image.getWidth();
          int orgHeight = image.getHeight();
          // Get preferred thumbnail dimension.
          Dimension thumbnailSize = getImageUtil().getThumbnailSize(orgWidth, orgHeight);
          logger.debug("Thumbnail size: " + thumbnailSize.toString());
          // Resize image.
          Image rescaled = image.getScaledInstance((int) thumbnailSize.getWidth(), (int) thumbnailSize.getHeight(), Image.SCALE_AREA_AVERAGING);
          BufferedImage biRescaled = getImageUtil().toBufferedImage(rescaled, BufferedImage.TYPE_INT_ARGB);
          response.setContentType(getImageUtil().getContentType(PathUtil.getFileExtension(svnCommand.getPath())));

          // Write thumbnail to output stream.
          baos = new ByteArrayOutputStream();
          ImageIO.write(biRescaled, THUMBNAIL_FORMAT, baos);

          // Putting created thumbnail image into the cache.
          logger.debug("Caching thumbnail. Using cachekey: " + cacheKey);
          try {
            SventonCache.INSTANCE.put(cacheKey, baos.toByteArray());
          } catch (CacheException ce) {
            logger.warn("Unable to cache thumbnail.");
          }
          // Write thumbnail to ServletOutputStream.
          output.write(baos.toByteArray());
        }
      } else {
        if (DISPLAY_TYPE_INLINE.equals(displayType)
            && getImageUtil().isImageFileExtension(PathUtil.getFileExtension(svnCommand.getPath()))) {
          logger.debug("Getting file as 'inline'");
          response.setContentType(getImageUtil().getContentType(PathUtil.getFileExtension(svnCommand.getPath())));
          response.setHeader("Content-disposition", "inline; filename=\"" + svnCommand.getTarget() + "\"");
        } else {
          logger.debug("Getting file as 'attachment'");
          response.setContentType(DEFAULT_CONTENT_TYPE);
          response.setHeader("Content-disposition", "attachment; filename=\"" + svnCommand.getTarget() + "\"");
        }
        HashMap properties = new HashMap();
        // Get the image data and write it to the outputStream.
        repository.getFile(svnCommand.getPath(), revision.getNumber(), properties, output);
        logger.debug(properties);
      }
      output.flush();
      output.close();
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
    return null;
  }

  /**
   * Gets the <code>ImageUtil</code> helper instance.
   *
   * @return The <code>ImageUtil</code>
   * @see de.berlios.sventon.util.ImageUtil
   */
  public ImageUtil getImageUtil() {
    return imageUtil;
  }

  /**
   * Sets the <code>ImageUtil</code> helper instance.
   *
   * @param imageUtil The instance
   * @see de.berlios.sventon.util.ImageUtil
   */
  public void setImageUtil(ImageUtil imageUtil) {
    this.imageUtil = imageUtil;
  }

}

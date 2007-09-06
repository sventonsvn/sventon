/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.util.EncodingUtils;
import de.berlios.sventon.util.ImageUtil;
import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserContext;
import org.apache.commons.io.FilenameUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * @author jesper@users.berlios.de
 */
public class GetController extends AbstractSVNTemplateController implements Controller {

  public static final String DISPLAY_REQUEST_PARAMETER = "disp";
  public static final String DISPLAY_TYPE_INLINE = "inline";

  /**
   * Image utility.
   */
  private ImageUtil imageUtil;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String displayType = ServletRequestUtils.getStringParameter(request, DISPLAY_REQUEST_PARAMETER, null);

    if (displayType == null) {
      logger.debug("Getting file as 'attachment'");
      getAsAttachment(repository, svnCommand, revision, request, response);
    } else if (DISPLAY_TYPE_INLINE.equals(displayType)) {
      logger.debug("Getting file as 'inline'");

      if (imageUtil.isImageFileExtension(FilenameUtils.getExtension(svnCommand.getPath()))) {
        getAsInlineImage(repository, svnCommand, revision, request, response);
      } else {
        logger.warn("File [" + svnCommand.getTarget() + "] is not an image file - unable to display it 'inline'");
        getAsAttachment(repository, svnCommand, revision, request, response);
      }
    } else {
      throw new IllegalArgumentException("Illegal parameter '" + DISPLAY_REQUEST_PARAMETER + "':" + displayType);
    }
    return null;
  }

  private void getAsInlineImage(final SVNRepository repository, final SVNBaseCommand svnCommand, final SVNRevision revision, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final ServletOutputStream output = response.getOutputStream();
    response.setContentType(imageUtil.getContentType(FilenameUtils.getExtension(svnCommand.getPath())));
    response.setHeader(WebUtils.CONTENT_DISPOSITION_HEADER, "inline; filename=\"" + EncodingUtils.encodeFilename(svnCommand.getTarget(), request) + "\"");
    // Get the image data and write it to the outputStream.
    getRepositoryService().getFile(repository, svnCommand.getPath(), revision.getNumber(), output);
    output.flush();
    output.close();
  }

  private void getAsAttachment(final SVNRepository repository, final SVNBaseCommand svnCommand, final SVNRevision revision, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final ServletOutputStream output = response.getOutputStream();
    String mimeType = null;

    try {
      mimeType = getServletContext().getMimeType(svnCommand.getTarget().toLowerCase());
    } catch (IllegalStateException ise) {
      logger.debug("Could not get mimeType for file as an ApplicationContext does not exist. Using default");
    }

    if (mimeType == null) {
      response.setContentType(WebUtils.APPLICATION_OCTET_STREAM);
    } else {
      response.setContentType(mimeType);
    }
    response.setHeader(WebUtils.CONTENT_DISPOSITION_HEADER, "attachment; filename=\"" + EncodingUtils.encodeFilename(svnCommand.getTarget(), request) + "\"");
    // Get the image data and write it to the outputStream.
    getRepositoryService().getFile(repository, svnCommand.getPath(), revision.getNumber(), output);
    output.flush();
    output.close();
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

}

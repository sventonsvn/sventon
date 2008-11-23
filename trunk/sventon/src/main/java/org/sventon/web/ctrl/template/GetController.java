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

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.EncodingUtils;
import org.sventon.util.WebUtils;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class GetController extends AbstractSVNTemplateController {

  /**
   * The mime/file type map.
   */
  private FileTypeMap mimeFileTypeMap;

  public static final String DISPLAY_REQUEST_PARAMETER = "disp";

  public static final String DISPLAY_TYPE_INLINE = "inline";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String displayType = ServletRequestUtils.getStringParameter(request, DISPLAY_REQUEST_PARAMETER, null);

    if (displayType == null) {
      logger.debug("Getting file as 'attachment'");
      getAsAttachment(repository, command, request, response);
    } else if (DISPLAY_TYPE_INLINE.equals(displayType)) {
      logger.debug("Getting file as 'inline'");

      if (mimeFileTypeMap.getContentType(command.getPath()).startsWith("image")) {
        getAsInlineImage(repository, command, request, response);
      } else {
        logger.warn("File [" + command.getTarget() + "] is not an image file - unable to display it 'inline'");
        getAsAttachment(repository, command, request, response);
      }
    } else {
      throw new IllegalArgumentException("Illegal parameter '" + DISPLAY_REQUEST_PARAMETER + "':" + displayType);
    }
    return null;
  }

  private void getAsInlineImage(final SVNRepository repository, final SVNBaseCommand command, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final OutputStream output = response.getOutputStream();
    response.setContentType(mimeFileTypeMap.getContentType(command.getPath()));
    response.setHeader(WebUtils.CONTENT_DISPOSITION_HEADER, "inline; filename=\"" + EncodingUtils.encodeFilename(command.getTarget(), request) + "\"");
    // Get the image data and write it to the outputStream.
    getRepositoryService().getFile(repository, command.getPath(), command.getRevisionNumber(), output);
    output.flush();
    output.close();
  }

  private void getAsAttachment(final SVNRepository repository, final SVNBaseCommand command, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final OutputStream output = response.getOutputStream();
    String mimeType = null;

    try {
      mimeType = getServletContext().getMimeType(command.getTarget().toLowerCase());
    } catch (IllegalStateException ise) {
      logger.debug("Could not get mimeType for file as an ApplicationContext does not exist. Using default");
    }

    if (mimeType == null) {
      response.setContentType(WebUtils.APPLICATION_OCTET_STREAM);
    } else {
      response.setContentType(mimeType);
    }
    response.setHeader(WebUtils.CONTENT_DISPOSITION_HEADER, "attachment; filename=\"" + EncodingUtils.encodeFilename(command.getTarget(), request) + "\"");
    // Get the image data and write it to the outputStream.
    getRepositoryService().getFile(repository, command.getPath(), command.getRevisionNumber(), output);
    output.flush();
    output.close();
  }

  /**
   * Sets the mime/file type map.
   *
   * @param mimeFileTypeMap Map.
   */
  public final void setMimeFileTypeMap(final FileTypeMap mimeFileTypeMap) {
    this.mimeFileTypeMap = mimeFileTypeMap;
  }

}

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

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.util.ImageUtil;
import de.berlios.sventon.svnsupport.SventonException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.SVNException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for showing selected repository entries as thumbnails.
 *
 * @author jesper@users.berlios.de
 */
public class ShowThumbnailController extends AbstractSVNTemplateController implements Controller {

  private ImageUtil imageUtil;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response, BindException exception) throws SventonException, SVNException {
    final String[] entryParameters = request.getParameterValues("entry");

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    List<String> entries = new ArrayList<String>();

    logger.debug("Showing thumbnail images");
    // Check what entries are image files - and add them to the list of thumbnails.
    for(String entry : entryParameters) {
      logger.debug("entry: " + entry);
      if (getImageUtil().isImageFilename(entry)) {
        entries.add(entry);
      }
    }
    logger.debug(entries.size() + " entries out of " + entryParameters.length
        + " are image files");

    model.put("thumbnailentries", entries);
    return new ModelAndView("showthumbs", model);

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

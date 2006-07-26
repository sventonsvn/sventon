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
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.blame.BlameHandler;
import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.web.command.SVNBaseCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * BlameController.
 * <p/>
 * Controller to support the blame/praise/annotate functionality of Subversion.
 *
 * @author patrikfr@users.berlios.de
 * @author jesper@users.berlios.de 
 */
public class BlameController extends AbstractSVNTemplateController implements Controller {

  private static final SVNRevision FIRST_REVISION = SVNRevision.parse("1");

  private Colorer colorer;

  /**
   * Sets the <tt>Colorer</tt> instance.
   *
   * @param colorer The instance.
   */
  public void setColorer(Colorer colorer) {
    this.colorer = colorer;
  }

  @Override
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand, final SVNRevision revision,
                                   final HttpServletRequest request, final HttpServletResponse response, final BindException exception) throws Exception {

    logger.debug("Blaming path: " + svnCommand.getPath() + ", rev: " + FIRST_REVISION + " - " + revision);

    BlameHandler blameHandler = new BlameHandler();

//    repository.doAnnotate(svnCommand.getPath(), FIRST_REVISION, revision, blameHandler);

    //blameHandler.colorizeContent(colorer, svnCommand.getTarget());
    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    logger.debug("Adding blameHandler: " + blameHandler);
    model.put("handler", blameHandler);
    model.put("properties", new HashMap());   //TODO: Replace with valid entry properties
    return new ModelAndView("blame", model);
  }
}

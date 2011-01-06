/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
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
import org.springframework.web.servlet.ModelAndView;
import org.sventon.Colorer;
import org.sventon.SVNConnection;
import org.sventon.model.AnnotatedTextFile;
import org.sventon.web.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * BlameController.
 * <p/>
 * Controller to support the blame/praise/annotate functionality of Subversion.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public final class BlameController extends AbstractTemplateController {

  /**
   * The colorer instance.
   */
  private final Colorer colorer;

  /**
   * Constructor.
   *
   * @param colorer Colorer
   */
  public BlameController(final Colorer colorer) {
    this.colorer = colorer;
  }

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Blaming: " + command.getPath() + ", revision: " + command.getRevisionNumber());

    final String charset = userRepositoryContext.getCharset();
    logger.debug("Using charset encoding: " + charset);

    final AnnotatedTextFile annotatedFile = getRepositoryService().blame(connection, command.getPath(),
        command.getRevisionNumber(), charset, colorer);

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("annotatedFile", annotatedFile);
    return new ModelAndView(getViewName(), model);
  }
}

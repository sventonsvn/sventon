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
package de.berlios.sventon.web.ctrl.xml;

import de.berlios.sventon.config.ApplicationConfiguration;
import de.berlios.sventon.config.InstanceConfiguration;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.service.RepositoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.bind.ServletRequestUtils;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller used for generating latest commit info as an XML representation.
 *
 * @author jesper@users.berlios.de
 */
public class ShowLatestCommitInfoController extends AbstractController {

  /**
   * Logger for this class and subclasses.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The application configuration.
   */
  private ApplicationConfiguration configuration;

  /**
   * The xml encoding.
   */
  private String encoding;

  /**
   * Date pattern.
   */
  private String datePattern;

  /**
   * The repository service instance.
   */
  private RepositoryService repositoryService;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    logger.debug("Getting latest commit info");
    response.setContentType("text/xml");
    response.setHeader("Cache-Control", "no-cache");

    final String instanceName = ServletRequestUtils.getStringParameter(request, "name", null);

    if (instanceName == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No 'name' parameter provided.");
      return null;
    }

    final InstanceConfiguration instanceConfiguration = configuration.getInstanceConfiguration(instanceName);
    final SVNRepository repository =
        RepositoryFactory.INSTANCE.getRepository(instanceConfiguration);

    if (repository == null) {
      final String errorMessage = "Unable to connect to repository!";
      logger.error(errorMessage + " Have sventon been configured?");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    final long headRevision = repositoryService.getLatestRevision(repository);
    logger.debug("Latest revision is: " + headRevision);

    try {
      response.getWriter().write(XMLDocumentHelper.getAsString(XMLDocumentHelper.createXML(
          repositoryService.getRevision(instanceName, repository, headRevision), datePattern),
          encoding));
    } catch (IOException ioex) {
      logger.warn(ioex);
    }
    return null;
  }

  /**
   * Set application configuration.
   *
   * @param configuration ApplicationConfiguration
   */
  public void setConfiguration(final ApplicationConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the xml encoding.
   *
   * @param encoding The encoding
   */
  public void setEncoding(final String encoding) {
    this.encoding = encoding;
  }

  /**
   * Sets the date pattern.
   *
   * @param datePattern The date pattern
   */
  public void setDatePattern(final String datePattern) {
    this.datePattern = datePattern;
  }

  /**
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }
}

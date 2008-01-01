/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl.xml;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.InstanceConfiguration;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.service.RepositoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller used for generating latest commit info as an XML representation.
 *
 * @author jesper@users.berlios.de
 */
public final class ShowLatestCommitInfoController extends AbstractController {

  /**
   * Logger for this class and subclasses.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The application.
   */
  private Application application;

  /**
   * The xml encoding.
   */
  private String encoding;

  /**
   * Date pattern.
   */
  private String datePattern;

  /**
   * Service.
   */
  private RepositoryService repositoryService;

  private static final String ERROR_MESSAGE = "Unable to get latest commit info";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    logger.debug("Getting latest commit info");
    response.setContentType("text/xml");
    response.setHeader("Cache-Control", "no-cache");

    final String instanceName = ServletRequestUtils.getRequiredStringParameter(request, "name");
    final String uid = ServletRequestUtils.getStringParameter(request, "uid", null);
    final String pwd = ServletRequestUtils.getStringParameter(request, "pwd", null);

    final InstanceConfiguration configuration = application.getInstance(instanceName).getConfiguration();

    SVNRepository repository = null;
    try {
      if (configuration.isAccessControlEnabled()) {
        repository = RepositoryFactory.INSTANCE.getRepository(configuration.getSVNURL(), uid, pwd);
      } else {
        repository = RepositoryFactory.INSTANCE.getRepository(configuration.getSVNURL(),
            configuration.getUid(), configuration.getPwd());
      }

      final long headRevision = repositoryService.getLatestRevision(repository);
      logger.debug("Latest revision is: " + headRevision);

      response.getWriter().write(XMLDocumentHelper.getAsString(
          XMLDocumentHelper.createXML(repositoryService.getRevision(
              instanceName, repository, headRevision), datePattern), encoding));
    } catch (SVNAuthenticationException ae) {
      logger.info(ERROR_MESSAGE + " - " + ae.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ae.getMessage());
    } catch (Exception ex) {
      logger.warn(ERROR_MESSAGE, ex);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
    } finally {
      if (repository != null) {
        repository.closeSession();
      }
    }
    return null;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public void setApplication(final Application application) {
    this.application = application;
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

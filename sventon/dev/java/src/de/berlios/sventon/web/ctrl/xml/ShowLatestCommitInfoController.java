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
package de.berlios.sventon.web.ctrl.xml;

import de.berlios.sventon.repository.RepositoryConfiguration;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.service.RepositoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
   * The repository configuration.
   */
  private RepositoryConfiguration configuration;

  /**
   * The xml encoding, default set to <code>UTF-8</code>.
   */
  private String encoding = "UTF-8";

  /**
   * Date pattern, default set to: <code>yyyy-MM-dd HH:mm:ss</code>.
   */
  private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

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

    final SVNRepository repository = RepositoryFactory.INSTANCE.getRepository(configuration);
    if (repository == null) {
      final String errorMessage = "Unable to connect to repository!";
      logger.error(errorMessage + " Have sventon been configured?");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    final long headRevision = repository.getLatestRevision();
    logger.debug("Latest revision is: " + headRevision);

    try {
      response.getWriter().write(
          getXMLAsString(createXML(repositoryService.getRevision(repository, headRevision)), encoding));
    } catch (IOException ioex) {
      logger.warn(ioex);
    }
    return null;
  }

  /**
   * Set repository configuration.
   *
   * @param configuration Configuration
   */
  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
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
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  /**
   * Creates the XML document based on given log entry.
   *
   * @param log The log entry.
   * @return The XML document.
   */
  private Document createXML(final SVNLogEntry log) {
    final Element root = new Element("latestcommitinfo");
    Element element;

    element = new Element("revision");
    element.setText(String.valueOf(log.getRevision()));
    root.addContent(element);

    element = new Element("author");
    element.setText(log.getAuthor());
    root.addContent(element);

    final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
    element = new Element("date");
    element.setText(sdf.format(log.getDate()));
    root.addContent(element);

    element = new Element("message");
    element.setText(log.getMessage());
    root.addContent(element);

    final Element entries = new Element("entries");

    //noinspection unchecked
    final Map<String, SVNLogEntryPath> map = log.getChangedPaths();
    final List<String> latestPathsList = new ArrayList<String>(map.keySet());

    for (String entryPath : latestPathsList) {
      final SVNLogEntryPath logEntryPath = map.get(entryPath);

      final Element entry = new Element("entry");
      element = new Element("path");
      element.setText(logEntryPath.getPath());
      entry.addContent(element);

      element = new Element("type");
      element.setText(String.valueOf(logEntryPath.getType()));
      entry.addContent(element);

      element = new Element("copypath");
      element.setText(logEntryPath.getCopyPath() == null ? "" : logEntryPath.getCopyPath());
      entry.addContent(element);

      element = new Element("copyrevision");
      element.setText(logEntryPath.getCopyPath() == null ? "" : Long.toString(logEntryPath.getCopyRevision()));
      entry.addContent(element);

      entries.addContent(entry);
    }
    root.addContent(entries);

    return new Document(root);
  }

  /**
   * Gets an XML document as a 'pretty-printed' string.
   *
   * @param document The XML document.
   * @param encoding Encoding
   * @return The XML document as a String.
   */
  private String getXMLAsString(final Document document, final String encoding) {
    // Format the XML document into a String
    final Format format = Format.getPrettyFormat();
    format.setEncoding(encoding);
    final XMLOutputter outputter = new XMLOutputter(format);
    return outputter.outputString(document);
  }
}

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
package de.berlios.sventon.ctrl.xml;

import de.berlios.sventon.repository.RepositoryConfiguration;
import de.berlios.sventon.repository.RepositoryFactory;
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
   * The cached commit info.
   */
  private String cachedInfo;

  /**
   * The cached info's head revision.
   */
  private long cachedInfoHeadRevision;

  /**
   * The xml encoding, default set to <code>ISO-8859-1</code>.
   *
   * TODO: Use UTF-8 as default instead?
   */
  private String encoding = "ISO-8859-1";

  private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    logger.debug("Getting latest commit info");
    response.setContentType("text/xml");
    response.setHeader("Cache-Control", "no-cache");

    SVNRepository repository = RepositoryFactory.INSTANCE.getRepository(configuration);
    if (repository == null) {
      String errorMessage = "Unable to connect to repository!";
      logger.error(errorMessage + " Have sventon been configured?");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    final long headRevision = repository.getLatestRevision();
    logger.debug("Latest revision is: " + headRevision);

    if (cachedInfoHeadRevision != headRevision) {
      final String[] targetPaths = new String[]{"/"}; // the path to show logs for
      logger.debug("Creating xml");
      cachedInfo = getXMLAsString(
          createXML((SVNLogEntry) repository.log(targetPaths,
              null, headRevision, headRevision, true, false).iterator().next()),
          encoding);
      logger.debug("Updating cache to revision: " + headRevision);
      cachedInfoHeadRevision = headRevision;
    } else {
      logger.debug("Returning cached commit info");
    }

    try {
      response.getWriter().write(cachedInfo);
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

  private Document createXML(final SVNLogEntry log) {
    Element root = new Element("latestcommitinfo");
    Element element;

    element = new Element("revision");
    element.setText(String.valueOf(log.getRevision()));
    root.addContent(element);

    element = new Element("author");
    element.setText(log.getAuthor());
    root.addContent(element);

    SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
    element = new Element("date");
    element.setText(sdf.format(log.getDate()));
    root.addContent(element);

    element = new Element("message");
    element.setText(log.getMessage());
    root.addContent(element);

    Element entries = new Element("entries");

    //noinspection unchecked
    Map<String, SVNLogEntryPath> map = log.getChangedPaths();
    List<String> latestPathsList = new ArrayList<String>(map.keySet());

    for (String entryPath : latestPathsList) {
      SVNLogEntryPath logEntryPath = map.get(entryPath);

      Element entry = new Element("entry");
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

  private String getXMLAsString(final Document document, final String encoding) {
    // Format the XML document into a String
    Format format = Format.getPrettyFormat();
    format.setEncoding(encoding);
    XMLOutputter outputter = new XMLOutputter(format);
    return outputter.outputString(document);
  }
}

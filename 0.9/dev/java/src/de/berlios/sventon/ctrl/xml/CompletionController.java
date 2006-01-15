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
package de.berlios.sventon.ctrl.xml;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.index.RevisionIndexer;
import de.berlios.sventon.ctrl.AbstractSVNTemplateController;
import de.berlios.sventon.ctrl.RepositoryEntry;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;

/**
 * Controller used for AJAX driven search completion.
 *
 * @author jesper@users.berlios.de
 */
public class CompletionController extends AbstractSVNTemplateController implements Controller {

  /**
   * The indexer instance.
   */
  private RevisionIndexer revisionIndexer;

  /**
   * The xml encoding, default set to <code>ISO-8859-1</code>.
   *
   * TODO: Use UTF-8 as default instead?
   */
  private String xmlEncoding = "ISO-8859-1";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response, BindException exception) throws SVNException {

    List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    final String searchString = request.getParameter("sventonSearchString");
    final String startDir = request.getParameter("startDir");
    logger.debug("Searching index for: " + searchString);

    entries.addAll(getRevisionIndexer().findPattern(".*/" + searchString + ".*", startDir, 10));

    // Print the XML document
    Format format = Format.getPrettyFormat();
    format.setEncoding(xmlEncoding);
    XMLOutputter outputter = new XMLOutputter(format);

    try {
      response.setContentType("text/xml");
      response.setHeader("Cache-Control", "no-cache");
      response.getWriter().write(outputter.outputString(createXMLDocument(entries)));
    } catch (IOException ioex) {
      logger.warn(ioex);
    }
    return null;
  }

  private Document createXMLDocument(final List<RepositoryEntry> entries) {
    Element items = new Element("items");
    for (RepositoryEntry entry : entries) {
      Element item = new Element("item");
      item.setText(entry.getFullEntryName());
      items.addContent(item);
    }
    return new Document(items);
  }

  /**
   * Sets the xml encoding.
   *
   * @param xmlEncoding The encoding
   */
  public void setXmlEncoding(final String xmlEncoding) {
    this.xmlEncoding = xmlEncoding;
  }

  /**
   * Gets the xml encoding.
   *
   * @return The encoding.
   */
  public String getXmlEncoding() {
    return xmlEncoding;
  }

  /**
   * Sets the revision indexer instance.
   *
   * @param revisionIndexer The instance.
   */
  public void setRevisionIndexer(final RevisionIndexer revisionIndexer) {
    this.revisionIndexer = revisionIndexer;
  }

  /**
   * Gets the revision indexer instance.
   *
   * @return The instance.
   */
  public RevisionIndexer getRevisionIndexer() {
    return revisionIndexer;
  }

}

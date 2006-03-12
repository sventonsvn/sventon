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

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.ctrl.AbstractSVNTemplateController;
import de.berlios.sventon.ctrl.RepositoryEntry;
import de.berlios.sventon.svnsupport.SventonException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller used for AJAX driven search completion.
 *
 * @author jesper@users.berlios.de
 */
public class CompletionController extends AbstractSVNTemplateController implements Controller {

  /**
   * The xml encoding, default set to <code>ISO-8859-1</code>.
   * <p/>
   * TODO: Use UTF-8 as default instead?
   */
  private String encoding = "ISO-8859-1";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response, BindException exception) throws SventonException {

    List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    final String searchString = request.getParameter("complete");
    final String startDir = request.getParameter("startDir");
    logger.debug("Searching index for: " + searchString);

    entries.addAll(getRevisionIndexer().findPattern(".*/" + searchString + ".*", startDir, 10));

    // Print the XML document
    Format format = Format.getPrettyFormat();
    format.setEncoding(encoding);
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
   * Sets the encoding used when creating the XML document.
   *
   * @param encoding The encoding.
   */
  public void setEncoding(final String encoding) {
    this.encoding = encoding;
  }

}

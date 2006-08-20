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

import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.web.ctrl.AbstractSVNTemplateController;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.validation.BindException;
import org.springframework.web.bind.RequestUtils;
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
   * The xml encoding, default set to <code>UTF-8</code>.
   * <p/>
   */
  private String encoding = "UTF-8";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final HttpServletRequest request,
                                   final HttpServletResponse response, final BindException exception) throws Exception {

    final List<RepositoryEntry> entries =
        Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    final String searchString = RequestUtils.getStringParameter(request, "complete");
    final String startDir = RequestUtils.getStringParameter(request, "startDir");
    logger.debug("Searching cache for [" + searchString + "] starting in [" + startDir + "]");

    entries.addAll(getCache().findEntry(svnCommand.getName(), searchString, startDir, 10));

    // Print the XML document
    final Format format = Format.getPrettyFormat();
    format.setEncoding(encoding);
    final XMLOutputter outputter = new XMLOutputter(format);

    try {
      response.setContentType("text/xml");
      response.setHeader("Cache-Control", "no-cache");
      response.getWriter().write(outputter.outputString(createXMLDocument(entries)));
    } catch (IOException ioex) {
      logger.warn(ioex);
    }
    return null;
  }

  /**
   * Creates the XML document.
   *
   * @param entries The repository entries.
   * @return The XML document.
   */
  private Document createXMLDocument(final List<RepositoryEntry> entries) {
    final Element items = new Element("items");
    for (RepositoryEntry entry : entries) {
      final Element item = new Element("item");
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

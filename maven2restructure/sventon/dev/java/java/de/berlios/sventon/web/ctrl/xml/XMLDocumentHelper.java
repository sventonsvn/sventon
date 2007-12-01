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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for handling XML documents.
 *
 * @author jesper@users.berlios.de
 */
public final class XMLDocumentHelper {

  /**
   * Creates the XML document based on given log entry.
   *
   * @param log         The log entry.
   * @param datePattern The date pattern.
   * @return The XML document.
   */
  public static Document createXML(final SVNLogEntry log, final String datePattern) {
    final Element root = new Element("latestcommitinfo");
    Element element;

    element = new Element("revision");
    element.setText(String.valueOf(log.getRevision()));
    root.addContent(element);

    element = new Element("author");
    element.setText(log.getAuthor());
    root.addContent(element);

    final SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
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
  public static String getAsString(final Document document, final String encoding) {
    // Format the XML document into a String
    final Format format = Format.getPrettyFormat();
    format.setEncoding(encoding);
    final XMLOutputter outputter = new XMLOutputter(format);
    return outputter.outputString(document);
  }

}

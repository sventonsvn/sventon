/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.util;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RequestParameterParser.
 *
 * @author jesper@users.berlios.de
 */
public final class RequestParameterParser {

  /**
   * The delimiter between the path and the revision values.
   */
  private static final String DELIMITER = ";;";

  /**
   * Parses the given string array.
   *
   * @param entries Array of entries.
   * @return List of file revisions.
   */
  public List<SVNFileRevision> parseEntries(final String[] entries) {
    final List<SVNFileRevision> result = new ArrayList<SVNFileRevision>();

    for (final String entry : entries) {
      if (!entry.contains(DELIMITER)) {
        throw new IllegalArgumentException("Illegal parameter. No delimiter in entry: " + entry);
      }
      final String path = entry.substring(0, entry.lastIndexOf(DELIMITER));
      final String revision = entry.substring(entry.lastIndexOf(DELIMITER) + 2);
      final SVNFileRevision fileRevision = new SVNFileRevision(path, Long.parseLong(revision), null, null);
      result.add(fileRevision);
    }
    Collections.sort(result);
    return result;
  }

  /**
   * Parses the <code>entry</code> parameters from the given request.
   *
   * @param request Request
   * @return List of file revisions.
   * @throws ServletRequestBindingException if request is missing the parameters.
   */
  public List<SVNFileRevision> parseEntries(final HttpServletRequest request) throws ServletRequestBindingException {
    return parseEntries(ServletRequestUtils.getRequiredStringParameters(request, "entry"));
  }
}

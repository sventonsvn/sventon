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
package de.berlios.sventon.diff;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Diff result parser.
 *
 * @author jesper@users.berlios.de
 */
public class DiffResultParser {

  /**
   * This pattern will match the following strings:
   * <pre>
   * 5c5
   * 10d10
   * 2,3d2
   * 2d2,3
   * 8a8,9
   * 8,9a8
   * 10,12c3,4
   * </pre>
   */
  public static final Pattern DIFF_PATTERN = Pattern.compile("^(\\d*),*(\\d*)([acd])(\\d*),*(\\d*)");

  /**
   * Logger for this class and subclasses.
   */
  private static final Log LOGGER = LogFactory.getLog(DiffResultParser.class);

  /**
   * Private constructor.
   */
  private DiffResultParser() {
  }

  /**
   * Parses result generated by <code>QDiffNormalGenerator</code>.
   *
   * @param normalDiffResult The diff result.
   * @return List of <code>DiffSegment</code>s.
   * @see de.regnis.q.sequence.line.diff.QDiffNormalGenerator
   */
  public static List<DiffSegment> parseNormalDiffResult(final String normalDiffResult) {
    final List<DiffSegment> diffActions = new ArrayList<DiffSegment>();
    int leftStart;
    int leftEnd;
    int rightStart;
    int rightEnd;
    final BufferedReader reader = new BufferedReader(new StringReader(normalDiffResult));

    String tempLine;
    try {
      while ((tempLine = reader.readLine()) != null) {
        final Matcher matcher = DIFF_PATTERN.matcher(tempLine);
        if (matcher.matches()) {
          leftStart = Integer.parseInt(matcher.group(1));
          leftEnd = "".equals(matcher.group(2))
              ? Integer.parseInt(matcher.group(1)) : Integer.parseInt(matcher.group(2));
          rightStart = Integer.parseInt(matcher.group(4));
          rightEnd = "".equals(matcher.group(5))
              ? Integer.parseInt(matcher.group(4)) : Integer.parseInt(matcher.group(5));
          diffActions.add(new DiffSegment(DiffAction.parse(matcher.group(3)),
              leftStart, leftEnd, rightStart, rightEnd));
        }
      }
    } catch (IOException ioex) {
      LOGGER.warn(ioex);
    }
    return diffActions;
  }

}

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
package de.berlios.sventon.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diff result parser.
 *
 * @author jesper@users.berlios.de
 */
public final class DiffResultParser {

  /**
   * Diff pattern.
   * <p/>
   * This pattern will match the following strings.
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
    final Scanner scanner = new Scanner(normalDiffResult);
    try {
      while (scanner.hasNextLine()) {
        final Matcher matcher = DIFF_PATTERN.matcher(scanner.nextLine());
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
    } finally {
      scanner.close();
    }
    return diffActions;
  }

}

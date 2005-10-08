package de.berlios.sventon.svnsupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for the show diff action.
 *
 * @author jesper@users.berlios.de
 */
public class DiffHelper {

  public static final String DELETE_ACTION = "d";
  public static final String CHANGE_ACTION = "c";
  public static final String ADD_ACTION = "a";

  /**
   * This pattern will match the following strings:
   * <pre>
   * 5c5
   * 10d10
   * 2,3d2
   * 2d2,3
   * 8a8,9
   * 8,9a8
   * </pre>
   */
  public static final Pattern DIFF_PATTERN = Pattern.compile("^(\\d*),*(\\d*)([acd])(\\d*),*(\\d*)");

  /**
   * List of diff actions for given diff result.
   */
  private List<DiffAction> diffActions = new ArrayList<DiffAction>();

  /**
   * Constructor.
   *
   * @param normalDiffResult The diff result.
   * @see de.regnis.q.sequence.line.diff.QDiffNormalGenerator
   */
  public DiffHelper(final String normalDiffResult) {
    String[] resultLines = normalDiffResult.split("\r\n");
    for (int i = 0; i < resultLines.length; i++) {
      Matcher matcher = DIFF_PATTERN.matcher(resultLines[i]);
      if (matcher.matches()) {
        if ("".equals(matcher.group(2)) && "".equals(matcher.group(5))) {
          diffActions.add(0, new DiffAction(matcher.group(3),
              Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(4))));
        } else {
          diffActions.add(0,
              new DiffAction(matcher.group(3),
                  !"".equals(matcher.group(2))
              ? Integer.parseInt(matcher.group(1)) : Integer.parseInt(matcher.group(4)),
                  !"".equals(matcher.group(5))
              ? Integer.parseInt(matcher.group(5)) : Integer.parseInt(matcher.group(2))));
        }
      }
    }
  }

  /**
   * Gets a diff action iterator.
   *
   * @return The iterator
   */
  public Iterator<DiffAction> getDiffActions() {
    return diffActions.iterator();
  }
}

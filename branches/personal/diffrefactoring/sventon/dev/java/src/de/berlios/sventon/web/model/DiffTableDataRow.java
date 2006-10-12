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
package de.berlios.sventon.web.model;

import de.berlios.sventon.diff.DiffAction;
import de.berlios.sventon.diff.SourceLine;
import org.apache.commons.lang.StringUtils;

/**
 * Diff table data holder. Hold left and right source line data and
 * additional info used to display the diff page.
 *
 * @author jesper@users.berlios.de
 */
public class DiffTableDataRow {

  private final DiffAction diffAction;
  private final String nextDiffAnchor;
  private final String rowNumber;
  private final String left;
  private final String right;
  private final String diffAnchor;

  public DiffTableDataRow(final SourceLine[] sourceLine, final Integer rowNumber, final String diffAnchor,
                          final String nextDiffAnchor) {

    this.rowNumber = StringUtils.leftPad(String.valueOf(rowNumber), 5); //TODO: Remove hardcoded row number space fill.
    this.diffAction = sourceLine[0].getAction();  // Will be same for both left and right lines.
    this.left = sourceLine[0].getLine();
    this.right = sourceLine[1].getLine();
    this.nextDiffAnchor = nextDiffAnchor;
    this.diffAnchor = diffAnchor;
  }

  public String getNextDiffAnchor() {
    return nextDiffAnchor;
  }

  public String getDiffSymbol() {
    return diffAction.getSymbol();
  }

  public String getRowNumber() {
    return rowNumber;
  }

  public String getLeft() {
    return left;
  }

  public String getRight() {
    return right;
  }

  public String getCssClass() {
    String cssClassName = null;
    switch (diffAction) {
      case ADDED:
        cssClassName = "srcAdd";
        break;
      case CHANGED:
        cssClassName = "srcChg";
        break;
      case DELETED:
        cssClassName = "srcDel";
        break;
      case UNCHANGED:
        cssClassName = "src";
        break;
    }
    return cssClassName;
  }

  public String getDescription() {
    return diffAction.getDescription();
  }

  public String getDiffAnchor() {
    return diffAnchor;
  }
}

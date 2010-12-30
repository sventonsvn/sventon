/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.diff;

import org.apache.commons.io.IOUtils;
import org.sventon.model.DiffAction;
import org.sventon.model.SideBySideDiffRow;
import org.sventon.model.SourceLine;
import org.sventon.model.TextFile;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.sventon.diff.DiffSegment.Side.LEFT;
import static org.sventon.diff.DiffSegment.Side.RIGHT;
import static org.sventon.model.DiffAction.*;

/**
 * Creates side by side diff result instances.
 * Creates diff results by parsing diff produced by
 *
 * @author jesper@sventon.org
 */
public final class SideBySideDiffCreator {

  /**
   * The left source lines.
   */
  private final List<String> leftSourceLines;

  /**
   * The right source lines.
   */
  private final List<String> rightSourceLines;

  /**
   * Constructor.
   *
   * @param fromFile From file
   * @param toFile   To file
   * @throws IOException if IO error
   */
  @SuppressWarnings({"unchecked"})
  public SideBySideDiffCreator(final TextFile fromFile, final TextFile toFile)
      throws IOException {
    leftSourceLines = IOUtils.readLines(new StringReader(fromFile.getContent()));
    rightSourceLines = IOUtils.readLines(new StringReader(toFile.getContent()));
  }

  /**
   * Parses the diff result and creates a side by side object instance as representation.
   *
   * @param diffResult The <tt>NORMAL</tt> diff result string.
   * @return The side-by-side representation.
   */
  public List<SideBySideDiffRow> createFromDiffResult(final String diffResult) {
    final List<DiffSegment> diffSegments = DiffResultParser.parseNormalDiffResult(diffResult);
    final List<SourceLine> leftLinesList = process(LEFT, leftSourceLines, diffSegments);
    final List<SourceLine> rightLinesList = process(RIGHT, rightSourceLines, diffSegments);
    assertEqualListSize(diffResult, leftLinesList, rightLinesList);

    final List<SideBySideDiffRow> diff = new ArrayList<SideBySideDiffRow>();

    for (int i = 0; i < Math.max(leftLinesList.size(), rightLinesList.size()); i++) {
      diff.add(new SideBySideDiffRow(leftLinesList.get(i), rightLinesList.get(i)));
    }
    return diff;
  }

  /**
   * Asserts that given lists have the same number of elements.
   *
   * @param diffResult Diff result string
   * @param firstList  First list
   * @param secondList Second list
   */
  private void assertEqualListSize(final String diffResult, final List<?> firstList, final List<?> secondList) {
    if (firstList.size() != secondList.size()) {
      final StringBuilder sb = new StringBuilder("Error while applying diff result!");
      sb.append("\nLine diff count: ");
      sb.append(firstList.size() - secondList.size());
      sb.append("\nDiffresult:\n");
      sb.append(diffResult);
      sb.append("\nLeft:\n");
      sb.append(firstList);
      sb.append("\nRight:\n");
      sb.append(secondList);
      throw new DiffException(sb.toString());
    }
  }

  /**
   * Process given list of source lines.
   *
   * @param side         Side to process
   * @param sourceLines  Sourcelines
   * @param diffSegments DiffSegments
   * @return List of processed lines.
   */
  private List<SourceLine> process(final DiffSegment.Side side, final List<String> sourceLines, final List<DiffSegment> diffSegments) {
    final List<SourceLine> resultLines = new ArrayList<SourceLine>();

    int lineNumber = 1;

    for (final String tempLine : sourceLines) {
      resultLines.add(new SourceLine(lineNumber++, tempLine));
    }

    int offset = 0;
    for (final DiffSegment diffSegment : diffSegments) {
      final DiffAction action = diffSegment.getAction();
      if (CHANGED == action) {
        offset = applyChanged(side, diffSegment, resultLines, offset);
      } else if (ADDED == action || DELETED == action) {
        offset = applyAddedOrDeleted(action, side, diffSegment, resultLines, offset);
      } else {
        throw new IllegalArgumentException("Unknow action: " + diffSegment.getAction());
      }
    }
    return resultLines;
  }

  /**
   * Applies the diff action CHANGED to given list of source lines.
   *
   * @param side        Left or right
   * @param diffSegment DiffSegment
   * @param resultLines The source lines to update
   * @param offset      Row offset
   * @return Updated row offset
   */
  private int applyChanged(final DiffSegment.Side side, final DiffSegment diffSegment, final List<SourceLine> resultLines, final int offset) {
    final int rowIndex;
    int newOffset = offset;
    int start = diffSegment.getLineIntervalStart(side.opposite());
    int end = diffSegment.getLineIntervalEnd(side.opposite());

    int changedLines = 0;
    for (int i = start; i <= end; i++) {
      final SourceLine sourceLine = resultLines.get(i - 1 + newOffset);
      resultLines.set(i - 1 + newOffset, sourceLine.changeAction(CHANGED));
      changedLines++;
    }

    start = diffSegment.getLineIntervalStart(side);
    end = diffSegment.getLineIntervalEnd(side);
    rowIndex = diffSegment.getLineIntervalEnd(side.opposite());

    int addedLines = 0;
    for (int i = start + changedLines; i <= end; i++) {
      resultLines.add(rowIndex + newOffset, new SourceLine(CHANGED));
      addedLines++;
      if (side == DiffSegment.Side.LEFT) {
        changedLines++;
      }
    }
    newOffset += addedLines;
    return newOffset;
  }

  /**
   * Applies the diff action ADDED to given list of source lines.
   *
   * @param action      Diff action.
   * @param side        Left or right
   * @param diffSegment DiffSegment
   * @param resultLines The source lines to update
   * @param offset      Row offset
   * @return Updated row offset
   */
  private int applyAddedOrDeleted(final DiffAction action, final DiffSegment.Side side, final DiffSegment diffSegment,
                                  final List<SourceLine> resultLines, final int offset) {
    int newOffset = offset;
    final DiffSegment.Side startSide = action == ADDED ? side : side.opposite();

    switch (startSide) {
      case LEFT:
        int addedLines = 0;
        int startLine = diffSegment.getLineIntervalStart(side) + newOffset;
        for (int i = diffSegment.getLineIntervalStart(side.opposite()); i <= diffSegment.getLineIntervalEnd(side.opposite()); i++) {
          resultLines.add(startLine++ - 1, new SourceLine(action));
          addedLines++;
        }
        newOffset += addedLines;
        break;
      case RIGHT:
        for (int i = diffSegment.getLineIntervalStart(side); i <= diffSegment.getLineIntervalEnd(side); i++) {
          final SourceLine sourceLine = resultLines.get(i - 1 + newOffset);
          resultLines.set(i - 1 + newOffset, sourceLine.changeAction(action));
        }
        break;
    }
    return newOffset;
  }
}

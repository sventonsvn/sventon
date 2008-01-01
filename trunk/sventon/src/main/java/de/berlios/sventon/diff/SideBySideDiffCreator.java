/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.diff;

import de.berlios.sventon.content.KeywordHandler;
import static de.berlios.sventon.diff.DiffAction.*;
import static de.berlios.sventon.diff.DiffSegment.Side.LEFT;
import static de.berlios.sventon.diff.DiffSegment.Side.RIGHT;
import de.berlios.sventon.model.SideBySideDiffRow;
import de.berlios.sventon.model.TextFile;
import de.berlios.sventon.util.WebUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates side by side diff result instances.
 * Creates diff results by parsing diff produced by
 * {@link de.berlios.sventon.diff.DiffProducer#doNormalDiff(java.io.OutputStream)}.
 *
 * @author jesper@users.berlios.de
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
   * @param fromFile           From file
   * @param fromKeywordHandler Keyword handler
   * @param fromFileCharset    Charset
   * @param toFile             To file
   * @param toKeywordHandler   Keyword handler
   * @param toFileCharset      Charset
   * @throws IOException if IO error
   */
  @SuppressWarnings({"unchecked"})
  public SideBySideDiffCreator(final TextFile fromFile, final KeywordHandler fromKeywordHandler, final String fromFileCharset,
                               final TextFile toFile, final KeywordHandler toKeywordHandler, final String toFileCharset)
      throws IOException {

    final String leftString = WebUtils.replaceLeadingSpaces(
        appendKeywords(fromKeywordHandler, fromFile.getContent(), fromFileCharset));

    final String rightString = WebUtils.replaceLeadingSpaces(
        appendKeywords(toKeywordHandler, toFile.getContent(), toFileCharset));

    leftSourceLines = IOUtils.readLines(new StringReader(leftString));
    rightSourceLines = IOUtils.readLines(new StringReader(rightString));
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

    for (int i = 0; i < leftLinesList.size(); i++) {
      diff.add(new SideBySideDiffRow(leftLinesList.get(i), rightLinesList.get(i)));
    }
    return diff;
  }

  /**
   * Appends keywords if KeywordHandler is not null.
   *
   * @param keywordHandler Handler
   * @param content        Content to apply keywords to
   * @param encoding       Encoding to use.
   * @return New content
   * @throws UnsupportedEncodingException if given charset encoding is unsupported
   */
  private String appendKeywords(final KeywordHandler keywordHandler, final String content, final String encoding)
      throws UnsupportedEncodingException {

    String newContent = content;
    if (keywordHandler != null) {
      newContent = keywordHandler.substitute(content, encoding);
    }
    return newContent;
  }

  /**
   * Asserts that given lists have the same number of elements.
   *
   * @param diffResult Diff result string
   * @param firstList  First list
   * @param secondList Second list
   * @throws RuntimeException if list size does not match.
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
      throw new RuntimeException(sb.toString());
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
      resultLines.add(new SourceLine(lineNumber++, UNCHANGED, tempLine));
    }

    int offset = 0;
    for (final DiffSegment diffSegment : diffSegments) {
      if (ADDED == diffSegment.getAction()) {
        offset = applyAdded(side, diffSegment, resultLines, offset);
      } else if (DELETED == diffSegment.getAction()) {
        offset = applyDeleted(side, diffSegment, resultLines, offset);
      } else if (CHANGED == diffSegment.getAction()) {
        offset = applyChanged(side, diffSegment, resultLines, offset);
      }
    }
    return resultLines;
  }

  /**
   * Applies the diff action CHANGED to given list of sourcelines.
   *
   * @param side        Left or right
   * @param diffSegment DiffSegment
   * @param resultLines The sourcelines to update
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
      resultLines.add(rowIndex + newOffset, new SourceLine(null, CHANGED, ""));
      addedLines++;
      if (side == DiffSegment.Side.LEFT) {
        changedLines++;
      }
    }
    newOffset += addedLines;
    return newOffset;
  }

  /**
   * Applies the diff action DELETED to given list of sourcelines.
   *
   * @param side        Left or right
   * @param diffSegment DiffSegment
   * @param resultLines The sourcelines to update
   * @param offset      Row offset
   * @return Updated row offset
   */
  private int applyDeleted(final DiffSegment.Side side, final DiffSegment diffSegment, final List<SourceLine> resultLines, final int offset) {
    int deletedLines = 0;
    for (int i = diffSegment.getLineIntervalStart(LEFT); i <= diffSegment.getLineIntervalEnd(LEFT); i++) {
      if (side == LEFT) {
        final SourceLine sourceLine = resultLines.get(i - 1);
        resultLines.set(i - 1, sourceLine.changeAction(DELETED));
      } else {
        resultLines.add(i - 1, new SourceLine(null, DELETED, ""));
        deletedLines++;
      }
    }
    return offset + deletedLines;
  }

  /**
   * Applies the diff action ADDED to given list of sourcelines.
   *
   * @param side        Left or right
   * @param diffSegment DiffSegment
   * @param resultLines The sourcelines to update
   * @param offset      Row offset
   * @return Updated row offset
   */
  private int applyAdded(final DiffSegment.Side side, final DiffSegment diffSegment, final List<SourceLine> resultLines, final int offset) {
    int newOffset = offset;
    switch (side) {
      case LEFT:
        int addedLines = 0;
        int startLine = diffSegment.getLineIntervalStart(side) + newOffset;
        for (int i = diffSegment.getLineIntervalStart(side.opposite()); i <= diffSegment.getLineIntervalEnd(side.opposite()); i++) {
          resultLines.add(startLine++ - 1, new SourceLine(null, ADDED, ""));
          addedLines++;
        }
        newOffset += addedLines;
        break;
      case RIGHT:
        for (int i = diffSegment.getLineIntervalStart(side); i <= diffSegment.getLineIntervalEnd(side); i++) {
          final SourceLine sourceLine = resultLines.get(i - 1 + newOffset);
          resultLines.set(i - 1 + newOffset, sourceLine.changeAction(ADDED));
        }
    }
    return newOffset;
  }
}

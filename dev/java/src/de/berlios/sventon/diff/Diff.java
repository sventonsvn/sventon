package de.berlios.sventon.diff;

import de.berlios.sventon.svnsupport.*;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * String differ.
 *
 * @author jesper@users.berlios.de
 */
public class Diff {

  private CustomArrayList<SourceLine> leftLinesList;
  private CustomArrayList<SourceLine> rightLinesList;

  public static final String ENCODING = "UTF-8";
  private String diffResultString = "";

  /**
   * Constructor.
   *
   * @param leftContent  Left (old) string content to diff.
   * @param rightContent Right (new) string content to diff.
   * @throws de.berlios.sventon.diff.DiffException if IO error occurs.
   */
  public Diff(final String leftContent, final String rightContent) throws DiffException {
    String tempLine;
    BufferedReader reader;

    List<String> leftSourceLines = new ArrayList<String>();
    List<String> rightSourceLines = new ArrayList<String>();

    InputStream leftStream = new ByteArrayInputStream(leftContent.getBytes());
    InputStream rightStream = new ByteArrayInputStream(rightContent.getBytes());

    try {
      ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
      DiffProducer diffProducer = new DiffProducer(leftStream, rightStream, Diff.ENCODING);
      diffProducer.doNormalDiff(diffResult);
      diffResultString = diffResult.toString();

      if ("".equals(diffResultString)) {
        throw new DiffException("Files are identical.");
      }

      LineNumberAppender appender = new LineNumberAppender();
      appender.setEmbedStart("<span class=\"sventonLineNo\">");
      appender.setEmbedEnd("</span>");

      reader = new BufferedReader(new StringReader(appender.appendTo(leftContent)));
      while ((tempLine = reader.readLine()) != null) {
        leftSourceLines.add(tempLine);
      }
      reader = new BufferedReader(new StringReader(appender.appendTo(rightContent)));
      while ((tempLine = reader.readLine()) != null) {
        rightSourceLines.add(tempLine);
      }
    } catch (IOException ioex) {
      throw new DiffException("Unable to produce diff.");
    }

    List<DiffAction> diffActions = DiffResultParser.parseNormalDiffResult(diffResultString);

    leftLinesList = processLeft(leftSourceLines, diffActions);
    rightLinesList = processRight(rightSourceLines, diffActions);

    if (leftLinesList.size() != rightLinesList.size()) {
      StringBuffer sb = new StringBuffer("Error while applying diff result!");
      sb.append("\nLine diff count: ");
      sb.append(leftLinesList.size() - rightLinesList.size());
      sb.append("\nDiffresult:\n");
      sb.append(diffResultString);
      sb.append("\nLeft:\n");
      sb.append(leftLinesList);
      sb.append("\nRight:\n");
      sb.append(rightLinesList);
      throw new RuntimeException(sb.toString());
    }
  }

  private CustomArrayList<SourceLine> processLeft(final List<String> sourceLines, final List<DiffAction> diffActions) {
    CustomArrayList<SourceLine> resultLines = new CustomArrayList<SourceLine>();
    for (String tempLine : sourceLines) {
      resultLines.add(new SourceLine("", tempLine));
    }

    int offset = 0;

    for (DiffAction diffAction : diffActions) {
      if (DiffAction.ADD_ACTION.equals(diffAction.getAction())) {
        // Apply diff action ADD
        int addedLines = 0;
        int startLine = diffAction.getLeftLineIntervalStart() + offset;
        for (int i = diffAction.getRightLineIntervalStart(); i <= diffAction.getRightLineIntervalEnd(); i++) {
          resultLines.add(startLine++ - 1, new SourceLine("a", ""));
          addedLines++;
        }
        offset += addedLines;
      } else if (DiffAction.DELETE_ACTION.equals(diffAction.getAction())) {
        // Apply diff action DELETE
        for (int i = diffAction.getLeftLineIntervalStart(); i <= diffAction.getLeftLineIntervalEnd(); i++) {
          resultLines.update(i - 1, new SourceLine("d", resultLines.get(i - 1).getLine()));
        }
      } else if (DiffAction.CHANGE_ACTION.equals(diffAction.getAction())) {
        // Apply diff action CHANGE
        int changedLines = 0;
        for (int i = diffAction.getRightLineIntervalStart(); i <= diffAction.getRightLineIntervalEnd(); i++) {
          resultLines.update(i - 1 + offset, new SourceLine("c", resultLines.get(i - 1 + offset).getLine()));
          changedLines++;
        }
        int addedLines = 0;
        for (int i = diffAction.getLeftLineIntervalStart() + changedLines; i <= diffAction.getLeftLineIntervalEnd(); i++) {
          resultLines.add(diffAction.getRightLineIntervalEnd() + offset, new SourceLine("c", ""));
          changedLines++;
          addedLines++;
        }
        offset += addedLines;
      }
    }
    return resultLines;
  }

  private CustomArrayList<SourceLine> processRight(final List<String> sourceLines, final List<DiffAction> diffActions) {
    CustomArrayList<SourceLine> resultLines = new CustomArrayList<SourceLine>();
    for (String tempLine : sourceLines) {
      resultLines.add(new SourceLine("", tempLine));
    }

    int offset = 0;

    for (DiffAction diffAction : diffActions) {
      if (DiffAction.ADD_ACTION.equals(diffAction.getAction())) {
        // Apply diff action ADD
        for (int i = diffAction.getRightLineIntervalStart(); i <= diffAction.getRightLineIntervalEnd(); i++) {
          resultLines.update(i - 1 + offset, new SourceLine("a", resultLines.get(i - 1 + offset).getLine()));
        }
      } else if (DiffAction.DELETE_ACTION.equals(diffAction.getAction())) {
        // Apply diff action DELETE
        int deletedLines = 0;
        for (int i = diffAction.getLeftLineIntervalStart(); i <= diffAction.getLeftLineIntervalEnd(); i++) {
          resultLines.add(i - 1, new SourceLine("d", ""));
          deletedLines++;
        }
        offset += deletedLines;
      } else if (DiffAction.CHANGE_ACTION.equals(diffAction.getAction())) {
        // Apply diff action CHANGE
        int changedLines = 0;
        for (int i = diffAction.getLeftLineIntervalStart(); i <= diffAction.getLeftLineIntervalEnd(); i++) {
          resultLines.update(i - 1 + offset, new SourceLine("c", resultLines.get(i - 1 + offset).getLine()));
          changedLines++;
        }
        int addedLines = 0;
        for (int i = diffAction.getRightLineIntervalStart() + changedLines; i <= diffAction.getRightLineIntervalEnd(); i++) {
          resultLines.add(diffAction.getLeftLineIntervalEnd() + offset, new SourceLine("c", ""));
          addedLines++;
        }
        offset += addedLines;
      }
    }
    return resultLines;
  }

  /**
   * Gets the diff result string.
   * @return The result string
   */
  public String getDiffResultString() {
    return diffResultString;
  }

  /**
   * Gets the left lines.
   * @return The list containing the left lines
   */
  public List<SourceLine> getLeft() {
    return leftLinesList;
  }

  /**
   * Gets the right lines.
   * @return The list containing the right lines
   */
  public List<SourceLine> getRight() {
    return rightLinesList;
  }

}

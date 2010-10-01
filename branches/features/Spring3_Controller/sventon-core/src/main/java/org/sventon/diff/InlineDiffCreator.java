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

import de.regnis.q.sequence.line.diff.QDiffGeneratorFactory;
import org.apache.commons.io.IOUtils;
import org.sventon.model.DiffAction;
import org.sventon.model.InlineDiffRow;
import org.sventon.model.TextFile;
import org.sventon.web.command.DiffCommand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to calculate Inline Diff result using DiffProducer
 */
public class InlineDiffCreator {
    public static List<InlineDiffRow> createInlineDiff(DiffCommand command, String charset, TextFile leftFile, TextFile rightFile) throws IOException {
    final List<InlineDiffRow> resultRows = new ArrayList<InlineDiffRow>();
    final ByteArrayOutputStream diffResult = new ByteArrayOutputStream();
    final Map generatorProperties = new HashMap();
    final int maxLines = Math.max(leftFile.getRows().size(), rightFile.getRows().size());
    //noinspection unchecked
    generatorProperties.put(QDiffGeneratorFactory.GUTTER_PROPERTY, maxLines);
    final DiffProducer diffProducer = new DiffProducer(new ByteArrayInputStream(leftFile.getContent().getBytes()),
        new ByteArrayInputStream(rightFile.getContent().getBytes()), charset, generatorProperties);

    diffProducer.doUnifiedDiff(diffResult);

    final String diffResultString = diffResult.toString(charset);
    if ("".equals(diffResultString)) {
      throw new IdenticalFilesException(command.getFromPath() + ", " + command.getToPath());
    }

    int rowNumberLeft = 1;
    int rowNumberRight = 1;
    //noinspection unchecked
    for (final String row : (List<String>) IOUtils.readLines(new StringReader(diffResultString))) {
      if (!row.startsWith("@@")) {
        final char action = row.charAt(0);
        switch (action) {
          case ' ':
            resultRows.add(new InlineDiffRow(rowNumberLeft, rowNumberRight, DiffAction.UNCHANGED, row.substring(1).trim()));
            rowNumberLeft++;
            rowNumberRight++;
            break;
          case '+':
            resultRows.add(new InlineDiffRow(null, rowNumberRight, DiffAction.ADDED, row.substring(1).trim()));
            rowNumberRight++;
            break;
          case '-':
            resultRows.add(new InlineDiffRow(rowNumberLeft, null, DiffAction.DELETED, row.substring(1).trim()));
            rowNumberLeft++;
            break;
          default:
            throw new IllegalArgumentException("Unknown action: " + action);
        }
      }
    }
    return resultRows;
  }
}

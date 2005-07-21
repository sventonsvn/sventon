package de.berlios.sventon.ctrl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.io.ISVNAnnotateHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * BlameHandler.
 * <p>
 * Container class to store blame lines as they are generated by the JavaSVN
 * annotate function.
 * 
 * @author patrikfr@users.berlios.de
 */
public class BlameHandler implements ISVNAnnotateHandler {

  protected final Log logger = LogFactory.getLog(getClass());

  private final List<BlameLine> blameLines = new ArrayList<BlameLine>();

  public void handleLine(Date date, long revision, String author, String line) {
    BlameLine blameLine = new BlameLine(date, revision, author, StringUtils.stripEnd(line, null));
    if (logger.isDebugEnabled()) {
      logger.debug("Added blame line: " + blameLine);
    }
    blameLines.add(blameLine);

  }

  /**
   * Get all the blame lines collected as a list.
   * 
   * @return Collected blame lines.
   */
  public final List<BlameLine> getBlameLines() {
    return blameLines;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "BlameHandler[blameLines=" + blameLines + "]";
  }

}

package de.berlios.sventon.ctrl;

import de.berlios.sventon.svnsupport.Diff;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DiffController.
 *
 * @author jesper@users.berlios.de
 */
public class DiffController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response) throws SVNException {

    logger.debug("Diffing file contents for: " + svnCommand);

    String[] revisions = request.getParameterValues("rev");
    if (revisions.length != 2) {
      throw new SVNException("Illegal revisions: " + revisions);
    }

    long toRevision = Long.parseLong(revisions[0]);
    long fromRevision = Long.parseLong(revisions[1]);

    Map<String, Object> model = new HashMap<String, Object>();
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    logger.debug("From revision: " + fromRevision);
    logger.debug("To revision: " + toRevision);

    String leftLines = null;
    String rightLines = null;

    try {
      // Get content of oldest file (left).
      logger.debug("Getting file contents from revision " + fromRevision);
      repository.getFile(svnCommand.getCompletePath(), fromRevision, new HashMap(), outStream);
      leftLines = outStream.toString();

      outStream = new ByteArrayOutputStream();

      // Get content of newest file (right).
      logger.debug("Getting file contents from revision " + toRevision);
      repository.getFile(svnCommand.getCompletePath(), toRevision, new HashMap(), outStream);
      rightLines = outStream.toString();

      Diff differ = new Diff(leftLines.toString(), rightLines.toString());
/*
    model.put("leftFileContents", ((Colorer) getApplicationContext()
        .getBean("colorer")).getColorizedContent(leftLines.toString(), svnCommand.getTarget()));
    model.put("rightFileContents", ((Colorer) getApplicationContext()
        .getBean("colorer")).getColorizedContent(rightLines.toString(), svnCommand.getTarget()));
*/
      model.put("leftFileContents", differ.getLeft());
      model.put("rightFileContents", differ.getRight());

    } catch (IOException ioex) {
      throw new SVNException(ioex);
    }

    return new ModelAndView("diff", model);
  }

}

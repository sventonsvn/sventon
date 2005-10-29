package de.berlios.sventon.ctrl;

import de.berlios.sventon.diff.Diff;
import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.command.SVNBaseCommand;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
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

    final long fromRevision;
    final long toRevision;
    final String fromPath;
    final String toPath;

    logger.debug("Diffing file contents for: " + svnCommand);
    final String[] revisionParameters = request.getParameterValues("rev");
    String[] pathAndRevision;
    Map<String, Object> model = new HashMap<String, Object>();
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    try {
      pathAndRevision = revisionParameters[0].split(";;");
      toPath = pathAndRevision[0];
      toRevision = Long.parseLong(pathAndRevision[1]);
      pathAndRevision = revisionParameters[1].split(";;");
      fromPath = pathAndRevision[0];
      fromRevision = Long.parseLong(pathAndRevision[1]);
      model.put("toRevision", toRevision);
      model.put("toPath", toPath);
      model.put("fromRevision", fromRevision);
      model.put("fromPath", fromPath);
    } catch (Exception ex) {
      throw new SVNException("Unable to diff. Unable to parse revision and path", ex);
    }

    String leftLines = null;
    String rightLines = null;

    try {
      // Get content of oldest file (left).
      logger.debug("Getting file contents for (from) revision " + fromRevision + ", path: " + fromPath);
      repository.getFile(fromPath, fromRevision, new HashMap(), outStream);
      leftLines = outStream.toString();

      // Re-initialize stream
      outStream = new ByteArrayOutputStream();

      // Get content of newest file (right).
      logger.debug("Getting file contents for (to) revision " + toRevision + ", path: " + toPath);
      repository.getFile(toPath, toRevision, new HashMap(), outStream);
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

    } catch (DiffException dex) {
      model.put("diffException", dex.getMessage());
    }

    return new ModelAndView("diff", model);
  }

}

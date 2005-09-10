package de.berlios.sventon.ctrl;

import de.berlios.sventon.svnsupport.KeywordHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ShowFileController.
 *
 * @author patrikfr@users.berlios.de
 */
public class ShowFileController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response) throws SVNException {

    Map<String, Object> model = new HashMap<String, Object>();
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    logger.debug("Assembling file contents for: " + svnCommand.getPath());

    HashMap properties = new HashMap();
    // Get the file's properties without requesting the content.
    repository.getFile(svnCommand.getPath(), revision.getNumber(), properties, null);
    logger.debug(properties);

    if (!SVNProperty.isTextMimeType((String) properties.get(SVNProperty.MIME_TYPE))) {
      // It's a binary file - we don't have to read the content.
      logger.debug("Binary file detected.");
    } else {
      // Get the file's content. We can skip the properties in this case.
      repository.getFile(svnCommand.getPath(), revision.getNumber(), null, outStream);
      String fileContents = outStream.toString();
      // Check if keywords should be expanded.
      String keywords = (String) properties.get(SVNProperty.KEYWORDS);
      Map keywordsMap = null;
      if (keywords != null) {
        String url = getRepositoryConfiguration().getUrl() + svnCommand.getPath();
        String author = (String) properties.get(SVNProperty.LAST_AUTHOR);
        String date = (String) properties.get(SVNProperty.COMMITTED_DATE);
        String rev = (String) properties.get(SVNProperty.COMMITTED_REVISION);
        keywordsMap = KeywordHandler.computeKeywords(keywords, url, author, date, rev);
        logger.debug("Substituting keywords: " + keywordsMap);
        fileContents = KeywordHandler.substitute(keywordsMap, fileContents);
      }
      fileContents = ((Colorer) getApplicationContext().getBean("colorer")).getColorizedContent(fileContents, svnCommand.getTarget());
      logger.debug("Create model");
      model.put("fileContents", fileContents);
    }

    return new ModelAndView("showfile", model);
  }

}

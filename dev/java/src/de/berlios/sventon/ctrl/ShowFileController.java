package de.berlios.sventon.ctrl;

import de.berlios.sventon.colorizer.Colorizer;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class ShowFileController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {

    Map<String, Object> model = new HashMap<String, Object>();

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    logger.debug("Assembling file contents for: " + svnCommand.getPath());

    repository.getFile(svnCommand.getPath(), revision, new HashMap(), outStream);
    StringReader reader = new StringReader(outStream.toString());
    Colorizer colorizer = new Colorizer(reader);
    StringBuffer sb = new StringBuffer();

    String line = "";
    try {
      while ((line = colorizer.readLine()) != null) {
        sb.append(StringUtils.leftPad(String.valueOf(colorizer.getLineNumber()), 5, "0"));
        sb.append(": ");
        sb.append(line);
        sb.append("\n");
      }
    } catch (IOException ioex) {
      logger.error("Error colorizing stream.", ioex);
    }

    logger.debug("Create model");
    model.put("fileContents", sb.toString());

    return new ModelAndView("showfile", model);
  }

}

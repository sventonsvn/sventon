package de.berlios.sventon.ctrl;

import de.berlios.sventon.util.ImageUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Controller used when downloading single files.
 * Image files can be gotten in three different ways.
 * <ul>
 *  <li><b>thumb</b> - Gets a thumbnail version of the image.</li>
 *  <li><b>inline</b> - Gets the image with correct content type.
 * Image will be displayed inline in browser.</li>
 *  <li><b>attachment</b> - Gets the image with content type
 * application/octetstream. A download dialog will appear in browser.</li>
 * </ul>
 * @author jesper@users.berlios.de
 */
public class GetController extends AbstractSVNTemplateController implements Controller {

  public static final String THUMBNAIL_FORMAT = "jpeg";
  public static final String DEFAULT_CONTENT_TYPE = "application/octetstream";

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response) throws SVNException {

    logger.debug("Getting file: " + svnCommand.getPath());

    String displayType = request.getParameter("disp");
    ServletOutputStream output = null;
    logger.debug("displayType: " + displayType);

    try {
      output = response.getOutputStream();

      if ("thumb".equals(displayType)) {
        logger.debug("Getting file as 'thumbnail'.");
        response.setHeader("Content-disposition", "inline; filename=\"" + svnCommand.getTarget() + "\"");
        StringBuilder urlString = new StringBuilder("http://");
        urlString.append(request.getServerName());
        urlString.append(":");
        urlString.append(request.getServerPort());
        urlString.append(request.getRequestURI());
        urlString.append("?");
        urlString.append(request.getQueryString().replaceAll("disp=thumb", "disp=inline"));
        URL url = new URL(urlString.toString());
        logger.debug("Getting full size image from url: " + url);
        BufferedImage image = ImageIO.read(url);
        int orgWidth = image.getWidth();
        int orgHeight = image.getHeight();
        // Get preferred thumbnail dimension.
        Dimension thumbnailSize = ImageUtil.getThumbnailSize(orgWidth, orgHeight);
        // Resize image.
        Image rescaled = image.getScaledInstance((int) thumbnailSize.getWidth(), (int) thumbnailSize.getHeight(), Image.SCALE_AREA_AVERAGING);
        BufferedImage biRescaled = ImageUtil.toBufferedImage(rescaled, BufferedImage.TYPE_INT_RGB);
        response.setContentType(ImageUtil.getContentType(svnCommand.getFileExtension()));
        // Write thumbnail to output stream.
        ImageIO.write(biRescaled, THUMBNAIL_FORMAT, output);
        output.flush();
        output.close();
      } else {
        if ("inline".equals(displayType)) {
          logger.debug("Getting file as 'inline'.");
          response.setContentType(ImageUtil.getContentType(svnCommand.getFileExtension()));
          response.setHeader("Content-disposition", "inline; filename=\"" + svnCommand.getTarget() + "\"");
        } else {
          logger.debug("Getting file as 'attachment'.");
          response.setContentType(DEFAULT_CONTENT_TYPE);
          response.setHeader("Content-disposition", "attachment; filename=\"" + svnCommand.getTarget() + "\"");
        }
        HashMap properties = new HashMap();
        repository.getFile(svnCommand.getPath(), revision.getNumber(), properties, output);
        logger.debug(properties);
      }
      output.flush();
      output.close();
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
    return null;
  }

}

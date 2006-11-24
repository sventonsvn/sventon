/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import org.apache.log4j.lf5.util.StreamUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Gets static content and writes it to the response stream.
 *
 * @author jesper@users.berlios.de
 */
public class GetStaticContentController extends AbstractController {

  /**
   * Path to static content.
   */
  private String path;


  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    final String filename = ServletRequestUtils.getRequiredStringParameter(request, "id");
    final InputStream is = getServletContext().getResourceAsStream(new File(path, filename + ".html").getPath());

    OutputStream out = null;
    if (is != null) {
      try {
        out = response.getOutputStream();
        StreamUtils.copy(is, out);
      } catch (IOException ioex) {
        logger.error(ioex);
      } finally {
        is.close();
        if (out != null) {
          out.close();
        }
      }
    }
    return null;
  }

  /**
   * Sets the path to the static content.
   *
   * @param path Path
   */
  public void setPath(final String path) {
    this.path = path;
  }

}

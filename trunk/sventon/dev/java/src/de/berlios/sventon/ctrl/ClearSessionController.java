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
package de.berlios.sventon.ctrl;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * ClearCredentialsController.
 * @author patrikfr@users.berlios.de
 */
public class ClearSessionController extends AbstractController {

  protected ModelAndView handleRequestInternal(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    HttpSession session = request.getSession(false);
    if (session != null)
    {
      session.invalidate();
    }
    return new ModelAndView("sessionclear");
  }

}

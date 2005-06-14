package de.berlios.sventon.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ClearCredentialsController extends AbstractController {

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

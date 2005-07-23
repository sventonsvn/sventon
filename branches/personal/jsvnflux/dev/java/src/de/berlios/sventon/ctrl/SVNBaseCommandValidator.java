package de.berlios.sventon.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * SVNBaseCommandValidator.
 * @author patrikfr@users.berlios.de
 */
public class SVNBaseCommandValidator implements Validator {

  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  public boolean supports(Class clazz) {
    return clazz.equals(SVNBaseCommand.class);
  }

  public void validate(Object obj, Errors errors) {
    SVNBaseCommand command = (SVNBaseCommand) obj;

    String revision = command.getRevision();
    
    if (revision == null) {
      return;
    } else if (!"HEAD".equals(revision) && !"".equals(revision)) {
      try {
        Long.parseLong(revision);
      } catch (NumberFormatException nfe) {
        errors.rejectValue("revision", "browse.error.illegal-revision", "Not a valid revision");
      }
    }
  }
}

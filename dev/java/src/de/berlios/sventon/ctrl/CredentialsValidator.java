package de.berlios.sventon.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CredentialsValidator implements Validator {

  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  public boolean supports(Class clazz) {
    return clazz.equals(Credentials.class);
  }

  public void validate(Object obj, Errors errors) {
    Credentials cred = (Credentials) obj;

    if (cred.getUid() == null || cred.getUid().equals("")) {
      errors.rejectValue("uid", "authenticate.error.uid-empty", "User id must not be empty.");
    } else {
      logger.info("Validating with uid: " + cred.getUid() + ", pwd: (nope, will not tell)");
    }
  }
}

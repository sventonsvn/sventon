/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

/**
 * ConfigCommandValidator.
 * @author jesper@users.berlios.de
 */
public class ConfigCommandValidator implements Validator {

  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  public boolean supports(Class clazz) {
    return clazz.equals(ConfigCommand.class);
  }

  public void validate(Object obj, Errors errors) {
    ConfigCommand command = (ConfigCommand) obj;

    String repositoryURL = command.getRepositoryURL();

    if (repositoryURL == null) {
      return;
    }

    try {
      SVNURL.parseURIDecoded(repositoryURL);
    } catch (SVNException ex) {
      errors.rejectValue("repositoryURL", "config.error.illegal-url", "Not a valid repository URL");
    }
  }
}

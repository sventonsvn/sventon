/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.command;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * SVNBaseCommandValidator.
 *
 * @author patrik@sventon.org
 */
public final class SVNBaseCommandValidator implements Validator {

  /**
   * {@inheritDoc}
   */
  public boolean supports(final Class clazz) {
    return SVNBaseCommand.class.isAssignableFrom(clazz);
  }

  /**
   * {@inheritDoc}
   */
  public void validate(final Object target, final Errors errors) {
    final SVNBaseCommand command = (SVNBaseCommand) target;

    if (SVNRevision.UNDEFINED.equals(command.getRevision())) {
      command.setRevision(SVNRevision.HEAD);
      errors.rejectValue("revision", "browse.error.illegal-revision");
    }
  }
}

/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.command;

import de.berlios.sventon.repository.RepositoryEntryComparator;
import de.berlios.sventon.repository.RepositoryEntrySorter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * SVNBaseCommandValidator.
 *
 * @author patrikfr@users.berlios.de
 */
public class SVNBaseCommandValidator implements Validator {

  /**
   * Logger for this class and subclasses
   */
  protected final Log logger = LogFactory.getLog(getClass());

  public boolean supports(final Class clazz) {
    return clazz.equals(SVNBaseCommand.class);
  }

  public void validate(final Object obj, final Errors errors) {
    final SVNBaseCommand command = (SVNBaseCommand) obj;

    if (command.getSortType() != null) {
      try {
        RepositoryEntryComparator.SortType.valueOf(command.getSortType());
      } catch (IllegalArgumentException iae) {
        errors.rejectValue("sortType", "browse.error.illegal-sortType", "Not a valid sort type");
      }
    }

    if (command.getSortMode() != null) {
      try {
        RepositoryEntrySorter.SortMode.valueOf(command.getSortMode());
      } catch (IllegalArgumentException iae) {
        errors.rejectValue("sortMode", "browse.error.illegal-sortMode", "Not a valid sort mode");
      }
    }

    final String revision = command.getRevision();

    if (revision == null) {
      return;
    } else if (!"HEAD".equals(revision) && !"".equals(revision)) {
      final SVNRevision parsedRevision = SVNRevision.parse(revision);
      if (parsedRevision == SVNRevision.UNDEFINED) {
        command.setRevision("HEAD");
        errors.rejectValue("revision", "browse.error.illegal-revision", "Not a valid revision");
      }
    }
  }
}

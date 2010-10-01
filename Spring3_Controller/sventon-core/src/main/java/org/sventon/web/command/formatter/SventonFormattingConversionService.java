/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.command.formatter;

import org.springframework.format.support.FormattingConversionService;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.web.command.BaseCommand;

/**
 *
 */
public class SventonFormattingConversionService extends FormattingConversionService{
  public SventonFormattingConversionService() {
    this.addFormatterForFieldType(BaseCommand.class, new BaseCommandFormatter());
    this.addFormatterForFieldType(Revision.class, new RevisionFormatter());
    this.addFormatterForFieldType(RepositoryName.class, new RepositoryNameFormatter());  
  }
}

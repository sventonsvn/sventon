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

import org.springframework.format.Formatter;
import org.sventon.model.Revision;

import java.text.ParseException;
import java.util.Locale;

/**
 *
 */
public class RevisionFormatter implements Formatter<Revision> {

  @Override
  public Revision parse(String text, Locale locale) throws ParseException {
    return Revision.parse(text);
  }

  @Override
  public String print(Revision object, Locale locale) {
    return object.toString();
  }
}

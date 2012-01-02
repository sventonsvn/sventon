/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.tags;

import org.sventon.util.EncodingUtils;

import javax.servlet.jsp.JspException;

/**
 * UrlTag.
 */
public class UrlTag extends org.apache.taglibs.standard.tag.rt.core.UrlTag {

  private static final long serialVersionUID = -4556271744291309364L;

  @Override
  public int doEndTag() throws JspException {
    super.setValue(EncodingUtils.encodeUrl(super.value));
    return super.doEndTag();
  }
}

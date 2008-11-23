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
package org.sventon.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.internal.wc.admin.SVNTranslator;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler class for subversion keyword substitution.
 * <p/>
 * Valid keywords are:
 * <table>
 * <tr><td>Date</td></tr>
 * <tr><td>LastChangedDate</td></tr>
 * <tr><td>Rev</td></tr>
 * <tr><td>Revision</td></tr>
 * <tr><td>LastChangedRevision</td></tr>
 * <tr><td>Author</td></tr>
 * <tr><td>LastChangedBy</td></tr>
 * <tr><td>URL</td></tr>
 * <tr><td>HeadURL</td></tr>
 * <tr><td>Id</td></tr>
 * </table>
 *
 * @author jesper@sventon.org
 * @see org.tmatesoft.svn.core.internal.wc.admin.SVNTranslator
 */
public final class KeywordHandler {

  public static final String KEYWORD_LAST_CHANGED_DATE = "LastChangedDate";
  public static final String KEYWORD_DATE = "Date";
  public static final String KEYWORD_LAST_CHANGED_REVISION = "LastChangedRevision";
  public static final String KEYWORD_REVISION = "Revision";
  public static final String KEYWORD_REV = "Rev";
  public static final String KEYWORD_LAST_CHANGED_BY = "LastChangedBy";
  public static final String KEYWORD_AUTHOR = "Author";
  public static final String KEYWORD_HEAD_URL = "HeadURL";
  public static final String KEYWORD_URL = "URL";
  public static final String KEYWORD_ID = "Id";

  private final Map<String, byte[]> keywordsMap;

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Constructs the instance and computes keyword variables.
   *
   * @param properties The subversion entry's properties.
   * @param url        The full url to the repository entry
   */
  @SuppressWarnings({"unchecked"})
  public KeywordHandler(final SVNProperties properties, final String url) {
    final String author = properties.getStringValue(SVNProperty.LAST_AUTHOR);
    final String date = properties.getStringValue(SVNProperty.COMMITTED_DATE);
    final String revision = properties.getStringValue(SVNProperty.COMMITTED_REVISION);
    final String keywords = properties.getStringValue(SVNProperty.KEYWORDS);
    keywordsMap = SVNTranslator.computeKeywords(keywords, url, author, date, revision, null);
  }

  /**
   * Substitutes keywords in content.
   *
   * @param content  The content
   * @param encoding Encoding to use.
   * @return Content with substituted keywords.
   * @throws UnsupportedEncodingException if given encoding is unsupported.
   */
  public String substitute(final String content, final String encoding) throws UnsupportedEncodingException {
    String substitutedContent = content;
    for (String keyword : keywordsMap.keySet()) {
      logger.debug("Substituting keywords");
      final String value = new String(keywordsMap.get(keyword), encoding);
      logger.debug(keyword + "=" + value);
      final Pattern keywordPattern = Pattern.compile("\\$" + keyword + "\\$");
      substitutedContent = keywordPattern.matcher(substitutedContent).replaceAll("\\$"
          + keyword + ": " + Matcher.quoteReplacement(value) + " \\$");
    }
    return substitutedContent;
  }

}

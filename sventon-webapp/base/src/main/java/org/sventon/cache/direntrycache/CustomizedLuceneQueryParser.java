/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.cache.direntrycache;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.CompassQueryParser;
import org.compass.core.lucene.engine.queryparser.DefaultLuceneQueryParser;

/**
 * CustomizedLuceneQueryParser.
 *
 * @author jesper@sventon.org
 */
public class CustomizedLuceneQueryParser extends DefaultLuceneQueryParser {

  @Override
  protected CompassQueryParser createQueryParser(String property, Analyzer analyzer, boolean forceAnalyzer) {
    final CompassQueryParser parser = super.createQueryParser(property, analyzer, forceAnalyzer);
    parser.setLowercaseExpandedTerms(false);
    return parser;
  }
}

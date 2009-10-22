package org.sventon.cache.entrycache;

import org.compass.core.lucene.engine.queryparser.DefaultLuceneQueryParser;
import org.apache.lucene.queryParser.CompassQueryParser;
import org.apache.lucene.analysis.Analyzer;

/**
 * CustomizedLuceneQueryParser.
 *
 * @author jesper@sventon.org
 */
public class CustomizedLuceneQueryParser extends DefaultLuceneQueryParser {

  /**
   * {@inheritDoc}
   */
  @Override
  protected CompassQueryParser createQueryParser(String property, Analyzer analyzer, boolean forceAnalyzer) {
    final CompassQueryParser parser = super.createQueryParser(property, analyzer, forceAnalyzer);
    parser.setLowercaseExpandedTerms(false);
    return parser;
  }
}

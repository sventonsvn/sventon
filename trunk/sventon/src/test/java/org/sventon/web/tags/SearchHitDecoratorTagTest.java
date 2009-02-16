package org.sventon.web.tags;

import junit.framework.TestCase;
import org.sventon.web.ctrl.template.SearchEntriesController;

public class SearchHitDecoratorTagTest extends TestCase {

  public void testDecorate() throws Exception {
    assertEquals("this is a <span class=\"hit\">test</span>!", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.TEXT.name(), "test", "this is a test!"));

    assertEquals("<span class=\"hit\">jesper</span>", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.TEXT.name(), "jesper", "jesper"));

    assertEquals("this is a test!", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.TEXT.name(), "not-found", "this is a test!"));

    assertEquals("this is a test!", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.CAMELCASE.name(), "test", "this is a test!"));

  }
}
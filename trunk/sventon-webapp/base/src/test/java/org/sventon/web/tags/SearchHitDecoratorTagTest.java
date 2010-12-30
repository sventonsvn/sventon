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
package org.sventon.web.tags;

import org.junit.Test;
import org.sventon.web.ctrl.template.SearchEntriesController;

import static org.junit.Assert.assertEquals;

public class SearchHitDecoratorTagTest {

  @Test
  public void testDecorate() throws Exception {
    assertEquals("this is a <span class=\"hit\">test</span>!", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.TEXT.name(), "test", "this is a test!"));

    assertEquals("this is a double test <span class=\"hit\">test</span>!", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.TEXT.name(), "test", "this is a double test test!"));

    assertEquals("<span class=\"hit\">jesper</span>", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.TEXT.name(), "jesper", "jesper"));

    assertEquals("<span class=\"hit\">Jesper</span>", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.TEXT.name(), "jesper", "Jesper"));

    assertEquals("this is a test!", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.TEXT.name(), "not-found", "this is a test!"));

    assertEquals("this is a test!", SearchHitDecoratorTag.decorate("hit",
        SearchEntriesController.SearchType.CAMELCASE.name(), "test", "this is a test!"));
  }
}
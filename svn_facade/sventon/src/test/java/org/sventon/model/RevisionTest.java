package org.sventon.model;

import junit.framework.TestCase;

public class RevisionTest extends TestCase {

  public void testParse() {
    assertSame(Revision.HEAD, Revision.parse("HEAD"));
    assertSame(Revision.HEAD, Revision.parse("head"));
    assertSame(Revision.HEAD, Revision.parse("hEAd"));
    assertSame(Revision.UNDEFINED, Revision.parse("undefined"));
    assertSame(Revision.UNDEFINED, Revision.parse("unDEFined"));
    assertSame(Revision.UNDEFINED, Revision.parse("unDEFined"));
    assertSame(Revision.UNDEFINED, Revision.parse("-1"));
  }

  public void testCreate() {
    assertSame(Revision.UNDEFINED, Revision.create(-1));
  }
}

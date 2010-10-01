package org.sventon.model;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class RevisionTest {

  @Test
  public void testCreateNumberRevision() {
    final Revision revision = Revision.create(42);
    assertEquals(42, revision.getNumber());
    assertEquals("42", revision.toString());
  }

  @Test
  public void testCreateHeadRevision() {
    final Revision revision = Revision.createHeadRevision(42);
    assertEquals("HEAD", revision.toString());
  }

  @Test
  public void testCreateDateRevision() {
    final Date now = new Date();
    final Revision revision = Revision.create(now);
    assertEquals(now, revision.getDate());
  }

  @Test
  public void testUndefined() {
    final Revision revision = Revision.create(-1);
    assertSame(Revision.UNDEFINED, revision);
    assertEquals("-1", revision.toString());
  }

  @Test
  public void testParseNamedRevision() {
    assertSame(Revision.HEAD, Revision.parse("HEAD"));
    assertEquals("HEAD", Revision.parse("HEAD").toString());
    assertSame(Revision.HEAD, Revision.parse("head"));
    assertSame(Revision.HEAD, Revision.parse("hEAd"));
    assertSame(Revision.UNDEFINED, Revision.parse("undefined"));
    assertSame(Revision.UNDEFINED, Revision.parse("unDEFined"));
    assertSame(Revision.UNDEFINED, Revision.parse("unDEFined"));
  }

  @Test
  public void parseNumberRevision() throws Exception {
    assertSame(Revision.UNDEFINED, Revision.parse("-1"));
    assertEquals(42, Revision.parse("42").getNumber());
  }

  @Test
  public void parseDateRevision() throws Exception {
    Calendar cal = Calendar.getInstance();
    cal.set(2010, 0, 1, 12, 34, 56);
    cal.set(Calendar.MILLISECOND, 789);
    cal.set(Calendar.ZONE_OFFSET, 0);

    assertEquals(cal.getTime(), Revision.parse("{2010-01-01T12:34:56.789Z}").getDate());
  }

  @Test
  public void parseMalformedDateRevision() throws Exception {
    assertSame(Revision.UNDEFINED, Revision.parse("-r {foobar-not-applicable}"));

  }

  @Test
  public void namedVersionToString() throws Exception {
    assertEquals("HEAD", Revision.HEAD.toString());
  }
}

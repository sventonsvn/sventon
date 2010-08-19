package org.sventon.model;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class RevisionTest{
  @Test
  public void createUndefinedNumberRevision() {
    assertFalse(Revision.create(-1).isNumberRevision());
    assertSame(Revision.UNDEFINED_NUMBER, Revision.create(-1).getNumber());
  }


  @Test
  public void createNumberRevision() {
    assertTrue(Revision.create(42).isNumberRevision());
    assertEquals(42, Revision.create(42).getNumber());
  }

  @Test
  public void createDateRevision() {
    final Date now = new Date();
    assertTrue(Revision.create(now).isDateRevision());
    assertEquals(now, Revision.create(now).getDate());
  }


  @Test
  public void createNamedRevision() {
    assertTrue(Revision.create(Revision.NamedRevision.HEAD).isNamedRevision());
    assertSame(Revision.UNDEFINED_NUMBER, Revision.create(-1).getNumber());
  }


  @Test
  public void parseNamedRevision() {
    assertSame(Revision.NamedRevision.HEAD, Revision.parse("HEAD").getNamedRevision());
    assertSame(Revision.NamedRevision.HEAD, Revision.parse("head").getNamedRevision());
    assertSame(Revision.NamedRevision.HEAD, Revision.parse("hEAd").getNamedRevision());
    assertSame(Revision.NamedRevision.UNDEFINED, Revision.parse("undefined").getNamedRevision());
    assertSame(Revision.NamedRevision.UNDEFINED, Revision.parse("unDEFined").getNamedRevision());
    assertSame(Revision.NamedRevision.UNDEFINED, Revision.parse("unDEFined").getNamedRevision());

  }

  @Test
  public void parseNumberRevision() throws Exception {
    assertSame(Revision.UNDEFINED, Revision.parse("-1"));

    assertEquals(42, Revision.parse("-r 42").getNumber());
  }

  @Test
  public void parseDateRevision() throws Exception {
    Calendar cal = Calendar.getInstance();
    cal.set(2010, 00, 01, 12, 34,56);
    cal.set(Calendar.MILLISECOND, 789);
    cal.set(Calendar.ZONE_OFFSET, 0);

    assertEquals(cal.getTime(), Revision.parse("-r {2010-01-01T12:34:56.789Z}").getDate());

  }


}

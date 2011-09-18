package org.sventon.web.command;

import org.junit.Test;
import org.sventon.model.Revision;

import static org.junit.Assert.assertEquals;

public class LogCommandTest {

  @Test
  public void testCalculateFromRevision() throws Exception {
    final LogCommand command = new LogCommand();
    command.setNextRevision(Revision.HEAD);
    assertEquals(2, command.calculateFromRevision(2));

    command.setNextRevision(Revision.create(1));
    assertEquals(1, command.calculateFromRevision(2));
  }

  @Test
  public void testCalculateNextPath() throws Exception {
    final LogCommand command = new LogCommand();
    assertEquals("/", command.calculateNextPath());

    command.setNextPath("trunk");
    assertEquals("trunk", command.calculateNextPath());
  }

  @Test
  public void testCalculateNextRevision() throws Exception {
    final LogCommand command = new LogCommand();
    assertEquals(Revision.HEAD, command.calculateNextRevision());

    command.setNextRevision(Revision.create(123));
    assertEquals(123, command.calculateNextRevision().getNumber());
  }
}

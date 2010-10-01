package org.sventon.model;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class UserContextTest {

  @Test
  public void testAddGetRemoveUserContext() throws Exception {
    final UserContext context = new UserContext();

    final RepositoryName repository1 = new RepositoryName("repository1");
    final RepositoryName repository2 = new RepositoryName("repository2");

    //Fetching UserRepositoryContext for a repository that doesn't have URC added
    UserRepositoryContext urc1 = context.getUserRepositoryContext(repository1);
    assertNull(urc1);

    //Add one and try again
    urc1 = new UserRepositoryContext();
    context.add(repository1, urc1);

    assertSame(urc1, context.getUserRepositoryContext(repository1));

    //Add one more
    final UserRepositoryContext urc2 = new UserRepositoryContext();

    context.add(repository2, urc2);

    assertSame(urc1, context.getUserRepositoryContext(repository1));
    assertSame(urc2, context.getUserRepositoryContext(repository2));

    //Re-adding changes nothing
    context.add(repository1, urc1);
    assertSame(urc1, context.getUserRepositoryContext(repository1));
    assertSame(urc2, context.getUserRepositoryContext(repository2));

    //Adding a new URC instance for an existing repository overwrites
    final UserRepositoryContext urc3 = new UserRepositoryContext();
    context.add(repository1, urc3);
    assertSame(urc3, context.getUserRepositoryContext(repository1));
    assertSame(urc2, context.getUserRepositoryContext(repository2));

    //Remove context
    context.remove(repository1);
    assertNull(context.getUserRepositoryContext(repository1));

    //Removing non-existing context is silently ignored
    context.remove(new RepositoryName("repository9"));
  }
}
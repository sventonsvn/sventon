package de.berlios.sventon.web.model;

import junit.framework.TestCase;

public class UserContextTest extends TestCase {

  public void testAddGetRemoveUserContext() throws Exception {
    UserContext context = new UserContext();

    //Fetching UserRepositoryContext for a repository that doesn't have URC added
    UserRepositoryContext urc1 = context.getUserRepositoryContext("repository1");
    assertNull(urc1);

    //Add one and try again
    urc1 = new UserRepositoryContext();
    context.add("repository1", urc1);

    assertSame(urc1, context.getUserRepositoryContext("repository1"));

    //Add one more
    UserRepositoryContext urc2 = new UserRepositoryContext();
    context.add("repository2", urc2);

    assertSame(urc1, context.getUserRepositoryContext("repository1"));
    assertSame(urc2, context.getUserRepositoryContext("repository2"));

    //Re-adding changes nothing
    context.add("repository1", urc1);
    assertSame(urc1, context.getUserRepositoryContext("repository1"));
    assertSame(urc2, context.getUserRepositoryContext("repository2"));

    //Adding a new URC instance for an existing repository overwrites
    UserRepositoryContext urc3 = new UserRepositoryContext();
    context.add("repository1", urc3);
    assertSame(urc3, context.getUserRepositoryContext("repository1"));
    assertSame(urc2, context.getUserRepositoryContext("repository2"));

    //Remove context
    context.remove("repository1");
    assertNull(context.getUserRepositoryContext("repository1"));

    //Removing non-existing context is scilently ignored
    context.remove("repository9");    
  }
}
package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.DirEntryLock;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShowLocksControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService serviceMock = mock(RepositoryService.class);

    final BaseCommand command = new BaseCommand();
    command.setPath("trunk/test/");
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    final ShowLocksController ctrl = new ShowLocksController();
    ctrl.setRepositoryService(serviceMock);

    final Map<String, DirEntryLock> result = new HashMap<String, DirEntryLock>();
    result.put("/", new DirEntryLock("id", "path", "owner", "comment", new Date(), new Date()));

    when(serviceMock.getLocks(null, command.getPath())).thenReturn(result);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, null, null, null);
    final Map model = modelAndView.getModel();

    assertEquals(1, model.size());
    final Collection locks = (Collection) model.get("currentLocks");
    assertEquals(1, locks.size());
  }
}
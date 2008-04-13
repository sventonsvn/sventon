package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.RepositoryName;
import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.cache.CacheGateway;
import de.berlios.sventon.web.command.SVNBaseCommand;
import static de.berlios.sventon.web.ctrl.SearchLogsController.SEARCH_STRING_PARAMETER;
import static de.berlios.sventon.web.ctrl.SearchLogsController.START_DIR_PARAMETER;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchLogsControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final CacheGateway mockCache = EasyMock.createMock(CacheGateway.class);
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(SEARCH_STRING_PARAMETER, "abc");
    request.addParameter(START_DIR_PARAMETER, "/trunk/");

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("trunk/test");
    command.setName(new RepositoryName("test"));
    command.setRevision(SVNRevision.create(12));

    final SearchLogsController ctrl = new SearchLogsController();
    ctrl.setCacheGateway(mockCache);

    final List<LogMessage> result = new ArrayList<LogMessage>();
    result.add(new LogMessage(123, "Revision 123."));

    expect(mockCache.find(command.getName(), "abc")).andStubReturn(result);
    replay(mockCache);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, null, null);
    final Map model = modelAndView.getModel();
    verify(mockCache);

    assertEquals(4, model.size());
    assertEquals("abc", (String) model.get(SEARCH_STRING_PARAMETER));
    assertEquals("/trunk/", (String) model.get(START_DIR_PARAMETER));
    assertEquals(result, model.get("logMessages"));
    assertTrue((Boolean) model.get("isLogSearch"));
    assertEquals("searchLogsResult", modelAndView.getViewName());
  }
}
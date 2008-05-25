package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

public class GetThumbnailControllerTest extends TestCase {

  public void testPrepareResponse() throws Exception {
    final HttpServletResponse response = new MockHttpServletResponse();
    final GetThumbnailController ctrl = new GetThumbnailController();
    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();
    ctrl.setMimeFileTypeMap(mftm);

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/test/target.jpg");

    ctrl.prepareResponse(response, command);

    final MockHttpServletResponse mockRsp = (MockHttpServletResponse) response;
    assertTrue(((String) mockRsp.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).contains("target.jpg"));
  }

}

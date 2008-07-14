package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.sventon.util.WebUtils;
import org.sventon.web.command.SVNBaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetThumbnailControllerTest extends TestCase {

  public void testPrepareResponse() throws Exception {
    final HttpServletRequest request = new MockHttpServletRequest();
    final HttpServletResponse response = new MockHttpServletResponse();
    final GetThumbnailController ctrl = new GetThumbnailController();
    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();
    ctrl.setMimeFileTypeMap(mftm);

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/test/target.jpg");

    ctrl.prepareResponse(request, response, command);

    final MockHttpServletResponse mockRsp = (MockHttpServletResponse) response;
    assertTrue(((String) mockRsp.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).contains("target.jpg"));
  }

}

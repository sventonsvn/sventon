package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.util.ImageUtil;
import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

public class GetThumbnailControllerTest extends TestCase {

  public void testPrepareResponse() throws Exception {
    final HttpServletResponse response = new MockHttpServletResponse();
    final GetThumbnailController ctrl = new GetThumbnailController();
    ctrl.setImageUtil(getImageUtil());

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/test/target.jpg");

    ctrl.prepareResponse(response, command);

    final MockHttpServletResponse mockRsp = (MockHttpServletResponse) response;
    assertTrue(((String) mockRsp.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).indexOf("target.jpg") > -1);
  }

  private ImageUtil getImageUtil() {
    final ImageUtil imageUtil = new ImageUtil();
    final Properties prop = new Properties();
    prop.setProperty("jpg", "image/jpg");
    imageUtil.setMimeMappings(prop);
    return imageUtil;
  }

}

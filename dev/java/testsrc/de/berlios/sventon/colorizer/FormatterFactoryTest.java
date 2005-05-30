package de.berlios.sventon.colorizer;

import junit.framework.*;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class FormatterFactoryTest extends TestCase {
  private ApplicationContext context;

  public void setUp() throws IOException {
      context = new FileSystemXmlApplicationContext("dev/java/web/web-inf/sventon-servlet.xml");
  }

  public void testGetFormatterForExtension() throws Exception {
    FormatterFactory ff = (FormatterFactory) context.getBean("formatterFactory");

    // Normal behaviour
    assertTrue(ff.getFormatterForExtension("java") instanceof JavaFormatter);
    assertTrue(ff.getFormatterForExtension("html") instanceof HTMLFormatter);

    //Upper case will be converted into lower case.
    assertTrue(ff.getFormatterForExtension("JAVA") instanceof JavaFormatter);

    // Must not work
    assertFalse(ff.getFormatterForExtension("html") instanceof JavaFormatter);

    // Generic Formatter if no specific exists.
    assertTrue(ff.getFormatterForExtension("abcde") instanceof FormatterImpl);
  }
}
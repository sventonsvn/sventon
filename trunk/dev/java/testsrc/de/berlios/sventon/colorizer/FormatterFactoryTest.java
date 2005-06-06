package de.berlios.sventon.colorizer;

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.IOException;

public class FormatterFactoryTest extends TestCase {
  private ApplicationContext context;

  public void setUp() throws IOException {
    context = new FileSystemXmlApplicationContext("dev/java/web/web-inf/sventon-servlet.xml");
  }

  public void testGetFormatterForExtension() throws Exception {
    FormatterFactory ff = (FormatterFactory) context.getBean("formatterFactory");

    // Normal behaviour
    assertTrue(ff.getFormatterForExtension("java") instanceof JavaFormatter);
    assertTrue(ff.getFormatterForExtension("xml") instanceof XMLFormatter);
    assertTrue(ff.getFormatterForExtension("html") instanceof HTMLFormatter);
    assertTrue(ff.getFormatterForExtension("c") instanceof CPlusPlusFormatter);

    //Upper case will be converted into lower case.
    assertTrue(ff.getFormatterForExtension("JAVA") instanceof JavaFormatter);

    // Must not work
    assertFalse(ff.getFormatterForExtension("html") instanceof JavaFormatter);

    // Generic Formatter if no specific exists.
    assertTrue(ff.getFormatterForExtension("abcde") instanceof FormatterImpl);
  }
}
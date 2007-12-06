package de.berlios.sventon.web.tags;

import junit.framework.TestCase;

public class UrlFormatterTagTest extends TestCase {

  public void testUnescapeLegalCharacters() throws Exception {
    final UrlFormatterTag urlFormatterTag = new UrlFormatterTag();
    assertEquals("http://localhost/svn/repobrowser.svn?path=/trunk&revision=HEAD&name=test",
        urlFormatterTag.unescapeLegalCharacters("http://localhost/svn/repobrowser.svn?path=%2ftrunk&revision=HEAD&name=test"));

    assertEquals("http://localhost/svn/showfile.svn?path=/trunk/public/%c3%a5%c3%a4%c3%b6.txt&revision=HEAD&name=test",
        urlFormatterTag.unescapeLegalCharacters("http://localhost/svn/showfile.svn?path=%2ftrunk%2fpublic%2f%c3%a5%c3%a4%c3%b6.txt&revision=HEAD&name=test"));
  }

}
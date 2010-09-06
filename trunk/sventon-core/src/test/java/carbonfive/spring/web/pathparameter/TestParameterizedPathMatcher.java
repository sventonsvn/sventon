package carbonfive.spring.web.pathparameter;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class TestParameterizedPathMatcher {

  @Test
  public void testMatchesWithoutName() {
    ParameterizedPathMatcher pathMatcher = new ParameterizedPathMatcher();

    assertTrue(pathMatcher.match("com/t?st.jsp", "com/test.jsp"));
    assertTrue(pathMatcher.match("com/t?st.jsp", "com/tast.jsp"));
    assertTrue(pathMatcher.match("com/t?st.jsp", "com/txst.jsp"));
    assertFalse(pathMatcher.match("com/t?st.jsp", "com/text.jsp"));

    assertTrue(pathMatcher.match("com/*.jsp", "com/test.jsp"));
    assertTrue(pathMatcher.match("com/*.jsp", "com/foo.jsp"));
    assertTrue(pathMatcher.match("com/*.jsp", "com/feed.jsp"));
    assertFalse(pathMatcher.match("com/*.jsp", "com/feed.jxp"));
    assertFalse(pathMatcher.match("com/*.jsp", "com/testxjsp"));
    assertFalse(pathMatcher.match("com/*.jsp", "com/test/test.jsp"));

    assertTrue(pathMatcher.match("com/**/test.jsp", "com/test.jsp"));
    assertTrue(pathMatcher.match("com/**/test.jsp", "com/foo/test.jsp"));
    assertFalse(pathMatcher.match("com/**/test.jsp", "com/foo/text.jsp"));
    assertFalse(pathMatcher.match("com/**/test.jsp", "com/foo/test"));

    assertTrue(pathMatcher.match("org/springframework/**/*.jsp", "org/springframework/test.jsp"));
    assertTrue(pathMatcher.match("org/springframework/**/*.jsp", "org/springframework/feed/foo.jsp"));
    assertFalse(pathMatcher.match("org/springframework/**/*.jsp", "org/springframework/test.txt"));
    assertFalse(pathMatcher.match("org/springframework/**/*.jsp", "com/springframework/feed/foo.jsp"));

    assertTrue(pathMatcher.match("org/**/servlet/bla.jsp", "org/springframework/servlet/bla.jsp"));
    assertTrue(pathMatcher.match("org/**/servlet/bla.jsp", "org/springframework/testing/servlet/bla.jsp"));
    assertTrue(pathMatcher.match("org/**/servlet/bla.jsp", "org/servlet/bla.jsp"));
  }

  @Test
  public void testMatchesWithName() {
    ParameterizedPathMatcher pathMatcher = new ParameterizedPathMatcher();

    assertTrue(pathMatcher.match("com/(t?st.jsp:jsp)", "com/test.jsp"));
    assertTrue(pathMatcher.match("com/(t?st.jsp:jsp)", "com/tast.jsp"));
    assertTrue(pathMatcher.match("com/(t?st.jsp:jsp)", "com/txst.jsp"));
    assertFalse(pathMatcher.match("com/(t?st.jsp:jsp)", "com/text.jsp"));

    assertTrue(pathMatcher.match("com/(*.jsp:jsp)", "com/test.jsp"));
    assertTrue(pathMatcher.match("com/(*.jsp:jsp)", "com/foo.jsp"));
    assertTrue(pathMatcher.match("com/(*.jsp:jsp)", "com/feed.jsp"));
    assertFalse(pathMatcher.match("com/(*.jsp:jsp)", "com/feed.jxp"));
    assertFalse(pathMatcher.match("com/(*.jsp:jsp)", "com/testxjsp"));
    assertFalse(pathMatcher.match("com/(*.jsp:jsp)", "com/test/test.jsp"));

    assertTrue(pathMatcher.match("com/(**/test.jsp:path)", "com/test.jsp"));
    assertTrue(pathMatcher.match("com/**/(test.jsp:jsp)", "com/foo/test.jsp"));
    assertFalse(pathMatcher.match("com/(**:path)/test.jsp", "com/foo/text.jsp"));
    assertFalse(pathMatcher.match("com/**/test.jsp", "com/foo/test"));

    assertTrue(pathMatcher.match("org/springframework/(**/*:view).jsp", "org/springframework/test.jsp"));
    assertTrue(pathMatcher.match("org/springframework/(**/*:view).jsp", "org/springframework/feed/foo.jsp"));
    assertFalse(pathMatcher.match("org/springframework/(**/*:view).jsp", "org/springframework/test.txt"));
    assertFalse(pathMatcher.match("org/springframework/(**/*:view).jsp", "com/springframework/feed/foo.jsp"));

    assertTrue(pathMatcher.match("org/(**:path)/servlet/bla.jsp", "org/springframework/servlet/bla.jsp"));
    assertTrue(pathMatcher.match("org/(**:path)/servlet/bla.jsp", "org/springframework/testing/servlet/bla.jsp"));
    assertTrue(pathMatcher.match("org/(**:path)/servlet/bla.jsp", "org/servlet/bla.jsp"));

    assertTrue(pathMatcher.match("/list/(*:name)/(**/*:path)*", "/list/sandbox/trunk/dev?revision=123"));
    assertTrue(pathMatcher.match("/list/(*:name)/(**/*:path)*", "/list/sandbox/trunk/dev/?revision=123"));
  }

  @Test
  public void testNamedGroups() {
    ParameterizedPathMatcher pathMatcher = new ParameterizedPathMatcher();
    assertMapValues(pathMatcher.namedParameters("com/(t?st.jsp:jsp)", "com/test.jsp"), "jsp", "test.jsp");
    assertMapValues(pathMatcher.namedParameters("com/(*.jsp:jsp)", "com/test.jsp"), "jsp", "test.jsp");
    assertMapValues(pathMatcher.namedParameters("com/(**:path)/test.jsp", "com/foo/test.jsp"), "path", "foo");
    assertMapValues(pathMatcher.namedParameters("org/springframework/(**/*:view).jsp", "org/springframework/feed/foo.jsp"), "view", "feed/foo");
    assertMapValues(pathMatcher.namedParameters("/user/(*:user)/found", "\\user\\stefan\\found"), "user", "stefan");

    assertMapValues(pathMatcher.namedParameters("view/page/(*:pageId)", "view/page/23432"), "pageId", "23432");
    assertMapValues(pathMatcher.namedParameters("view/(*:controller)/(*:pageId)", "view/image/12345"),
        "controller", "image", "pageId", "12345");
  }

  @Test
  public void testExtractPathWithinPattern() {
    ParameterizedPathMatcher pathMatcher = new ParameterizedPathMatcher();
    assertEquals("", pathMatcher.extractPathWithinPattern("/docs/cvs/commit.html", "/docs/cvs/commit.html"));
    assertEquals("cvs/commit", pathMatcher.extractPathWithinPattern("/docs/*", "/docs/cvs/commit"));
    assertEquals("commit.html", pathMatcher.extractPathWithinPattern("/docs/cvs/*.html", "/docs/cvs/commit.html"));
    assertEquals("cvs/commit", pathMatcher.extractPathWithinPattern("/docs/**", "/docs/cvs/commit"));
    assertEquals("cvs/commit.html", pathMatcher.extractPathWithinPattern("/docs/**/*.html", "/docs/cvs/commit.html"));
    assertEquals("docs/cvs/commit.html", pathMatcher.extractPathWithinPattern("/**.html", "/docs/cvs/commit.html"));
    assertEquals("/docs/cvs/commit.html", pathMatcher.extractPathWithinPattern("**.html", "/docs/cvs/commit.html"));
    assertEquals("/docs/cvs/commit.html", pathMatcher.extractPathWithinPattern("**", "/docs/cvs/commit.html"));

    assertEquals("cvs/commit.html", pathMatcher.extractPathWithinPattern("/docs/(**:x)/*.html", "/docs/cvs/commit.html"));
    assertEquals("cvs/commit.html", pathMatcher.extractPathWithinPattern("/docs/(**/*:x).html", "/docs/cvs/commit.html"));
    assertEquals("docs/cvs/commit.html", pathMatcher.extractPathWithinPattern("/do(cs/**/*.html:x)", "/docs/cvs/commit.html"));
    assertEquals("docs/cvs/commit.html", pathMatcher.extractPathWithinPattern("/do(cs/**:x)/*.html", "/docs/cvs/commit.html"));
    assertEquals("docs/cvs/commit.html", pathMatcher.extractPathWithinPattern("/(docs:x)/**/*.html", "/docs/cvs/commit.html"));
  }

  private void assertMapValues(Map<String, String> map, String... keyValues) {
    for (int i = 0; i < keyValues.length; i += 2) {
      assertEquals(keyValues[i + 1], map.get(keyValues[i]));
    }
  }

}

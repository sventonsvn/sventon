package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;

public class CustomArrayListTest extends TestCase {

  public void testCustomArrayListUpdate() throws Exception {
    CustomArrayList<String> list = new CustomArrayList<String>();
    list.add(0, "zero");
    list.add(1, "one");
    list.add(2, "two");

    list.update(1, "one - updated");

    assertEquals(3, list.size());
    assertEquals("one - updated", list.get(1));
  }

}
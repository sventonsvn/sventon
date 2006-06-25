package de.berlios.sventon.util;

import junit.framework.TestCase;

import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.Deflater;
import java.io.FileOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileInputStream;

public class ZipUtilTest extends TestCase {

  public void testZipFile() throws Exception {

    ZipUtil zipUtil = null;
    File tempZipFile = null;
    File fileToZip1 = null;
    File fileToZip2 = null;
    PrintWriter writer = null;
    ZipOutputStream zos = null;

    try {
      zipUtil = new ZipUtil();
      tempZipFile = File.createTempFile("sventon", ".zip");
      System.out.println("Created temp ZIP file: " + tempZipFile.toString());

      fileToZip1 = File.createTempFile("sventonfile", null);
      writer = new PrintWriter(fileToZip1);
      writer.println("Contents of file one.");
      writer.close();

      fileToZip2 = File.createTempFile("sventonfile", null);
      writer = new PrintWriter(fileToZip2);
      writer.println("Contents of file two.");
      writer.close();

      assertTrue(fileToZip1.length() > 0);
      assertTrue(fileToZip2.length() > 0);

      zos = new ZipOutputStream(new FileOutputStream(tempZipFile));
      zos.setLevel(Deflater.BEST_COMPRESSION);
      zipUtil.zipFile(fileToZip1, zos);
      zipUtil.zipFile(fileToZip2, zos);
      zos.close();

      assertTrue(tempZipFile.length() > 0);

      // Validate by reading temp zip file.
      ZipInputStream zip = new ZipInputStream(new FileInputStream(tempZipFile));
      int count = 0;
      ZipEntry zipEntry = null;
      while ((zipEntry = zip.getNextEntry()) != null) {
        assertTrue(zipEntry.getName().indexOf("sventonfile") > -1);
        count++;
      }
      assertEquals(2, count);
      zip.close();

    } finally {
      System.out.println("Cleaning up...");
      // Clean up
      if (tempZipFile != null) {
        tempZipFile.delete();
      }
      if (fileToZip1 != null) {
        fileToZip1.delete();
      }
      if (fileToZip2 != null) {
        fileToZip2.delete();
      }
    }
  }

  public void testZipDir() throws Exception {
    ZipUtil zipUtil = null;
    File tempZipFile = null;
    File dirToZip = null;
    File subdir = null;
    File fileToZip1 = null;
    File fileToZip2 = null;
    PrintWriter writer = null;
    ZipOutputStream zos = null;

    try {
      zipUtil = new ZipUtil();
      tempZipFile = File.createTempFile("sventon", ".zip");
      System.out.println("Created temp ZIP file: " + tempZipFile.toString());

      dirToZip = new File(tempZipFile.getParent() + "/sventontest/");
      dirToZip.mkdir();

      subdir = new File(dirToZip, "dir2");
      subdir.mkdir();

      fileToZip1 = File.createTempFile("sventonfile", null, dirToZip);
      writer = new PrintWriter(fileToZip1);
      writer.println("Contents of file one.");
      writer.close();

      fileToZip2 = File.createTempFile("sventonfile", null, subdir);
      writer = new PrintWriter(fileToZip2);
      writer.println("Contents of file two.");
      writer.close();

      assertTrue(fileToZip1.length() > 0);
      assertTrue(fileToZip2.length() > 0);

      zos = new ZipOutputStream(new FileOutputStream(tempZipFile));
      zos.setLevel(Deflater.BEST_COMPRESSION);
      zipUtil.zipDir(dirToZip, zos);
      zos.close();

      assertTrue(tempZipFile.length() > 0);

      // Validate by reading temp zip file.
      ZipInputStream zip = new ZipInputStream(new FileInputStream(tempZipFile));
      int count = 0;
      ZipEntry zipEntry = null;
      while ((zipEntry = zip.getNextEntry()) != null) {
        assertTrue(zipEntry.getName().indexOf("sventonfile") > -1);
        count++;
      }
      assertEquals(2, count);
      zip.close();

    } finally {
      System.out.println("Cleaning up...");
      // Clean up
      if (tempZipFile != null) {
        tempZipFile.delete();
      }
      if (fileToZip1 != null) {
        fileToZip1.delete();
      }
      if (fileToZip2 != null) {
        fileToZip2.delete();
      }
      if (subdir != null) {
        subdir.delete();
      }
      if (dirToZip != null) {
        dirToZip.delete();
      }
    }
  }


  public void testCreateZipEntryName() throws Exception {
    ZipUtil z = new ZipUtil();
    assertEquals("\\file1", z.createZipEntryName("c:\\base", "c:\\base\\file1"));
    assertEquals("\\file1\\", z.createZipEntryName("c:\\base", "c:\\base\\file1\\"));
    assertEquals("\\dir\\file1", z.createZipEntryName("c:\\base", "c:\\base\\dir\\file1"));
  }
}
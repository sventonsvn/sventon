/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for handling ZIP actions.
 *
 * @author jesper@users.berlios.de
 */
public class ZipUtil {

  /**
   * Constuctor.
   */
  public ZipUtil() {
  }

  /**
   * Zips the given directory recursively.
   * @param directory The directory to zip.
   * @param zos The output stream to write to.
   */
  public void zipDir(final String directory, final ZipOutputStream zos) {
    //create a new File object based on the directory we have to zip
    zipDir(new File(directory), zos);
  }

  /**
   * Zips the given directory recursively.
   * @param directory The directory to zip.
   * @param zos The output stream to write to.
   */
  public void zipDir(final File directory, final ZipOutputStream zos) {
    // TODO: Seems like the ZipEntry does not handle long file names in directories.
    // Have to check that more into detail. Also, maybe only relative path is
    // interesting to store in zip archive?!
    try {
      if (!directory.isDirectory()) {
        throw new IllegalArgumentException("Argument is not a directory: " + directory);
      }
      //get a listing of the directory content
      String[] dirList = directory.list();
      byte[] readBuffer = new byte[4096];
      int bytesIn = 0;
      //loop through directory, and zip the files
      for (int i = 0; i < dirList.length; i++) {
        File fileEntry = new File(directory, dirList[i]);
        if (fileEntry.isDirectory()) {
          //if the File object is a directory, call this
          //function again to add its content recursively
          zipDir(fileEntry, zos);
          //loop again
          continue;
        }
        FileInputStream fis = new FileInputStream(fileEntry);
        ZipEntry anEntry = new ZipEntry(fileEntry.getAbsolutePath());
        //place the zip entry in the ZipOutputStream object
        zos.putNextEntry(anEntry);
        //now write the content of the file to the ZipOutputStream
        while ((bytesIn = fis.read(readBuffer)) != -1) {
          zos.write(readBuffer, 0, bytesIn);
        }
        //close the Stream
        fis.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Zips the given file.
   * @param file The file to zip.
   * @param zos The output stream to write to.
   */
  public void zipFile(final String file, final ZipOutputStream zos) {
    //create a new File object based on the file we have to zip
    zipFile(new File(file), zos);
  }

  /**
   * Zips the given file.
   * @param file The file to zip.
   * @param zos The output stream to write to.
   */
  public void zipFile(final File file, final ZipOutputStream zos) {
    try {
      if (!file.isFile()) {
        throw new IllegalArgumentException("Argument is not a file: " + file);
      }
      byte[] readBuffer = new byte[2156];
      int bytesIn = 0;
      FileInputStream fis = new FileInputStream(file);
      ZipEntry anEntry = new ZipEntry(file.getName());
      //place the zip entry in the ZipOutputStream object
      zos.putNextEntry(anEntry);
      //now write the content of the file to the ZipOutputStream
      while ((bytesIn = fis.read(readBuffer)) != -1) {
        zos.write(readBuffer, 0, bytesIn);
      }
      //close the Stream
      fis.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    ZipOutputStream zip = new ZipOutputStream(new FileOutputStream("/test.zip"));
    new ZipUtil().zipDir("/test", zip);
    zip.close();
  }
}
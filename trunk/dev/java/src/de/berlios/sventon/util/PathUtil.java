package de.berlios.sventon.util;

/**
 * Utility class for path handling.
 *
 * @author jesper@users.berlios.de
 */
public class PathUtil {

  /**
   * Gets the target (leaf/end) part of the <code>path</code>, it could be a file
   * or a directory.
   * <p>
   * The returned string will have no final "/", even if it is a directory.
   *
   * @param fullpath Path
   * @return Target part of the path. If <tt>fullpath</tt> is null an
   * empty string will be returned.
   */
  public static String getTarget(final String fullpath) {
    if (fullpath == null) {
      return "";
    }

    String[] splittedString = fullpath.split("/");
    int length = splittedString.length;
    if (length == 0) {
      return "";
    } else {
      return splittedString[splittedString.length - 1];
    }
  }

  /**
   * Gets the file extension.
   *
   * @param fullpath Path
   * @return The file extension if any. Empty string otherwise.
   * If <tt>fullpath</tt> is null an empty string will be returned.
   */
  public static String getFileExtension(final String fullpath) {
    if (fullpath == null) {
      return "";
    }

    String fileExtension = "";
    if (getTarget(fullpath).lastIndexOf(".") > -1) {
      fileExtension = getTarget(fullpath).substring(getTarget(fullpath).lastIndexOf(".") + 1);
    }
    return fileExtension;
  }

  /**
   * Gets the path, excluding the end/leaf.
   * <p>
   * The returned string will have a final "/". If the path info is empty or null, ""
   * (empty string) will be returned.
   *
   * @param fullpath Path
   * @return Path excluding taget (end/leaf).
   */
  public static String getPathPart(final String fullpath) {
    if (fullpath == null) {
      return "";
    }

    String work = fullpath;
    if (work.endsWith("/")) {
      work = work.substring(0, work.length() - 1);
    }

    int lastIndex = work.lastIndexOf('/');
    if (lastIndex == -1) {
      return "";
    } else {
      return work.substring(0, lastIndex) + "/";
    }
  }
}

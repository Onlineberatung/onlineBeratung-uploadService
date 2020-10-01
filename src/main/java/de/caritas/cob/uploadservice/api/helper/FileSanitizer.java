package de.caritas.cob.uploadservice.api.helper;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

public class FileSanitizer {

  private static final String DEFAULT_FILENAME = "Anhang.";
  private static final String REGEX_REMOVE_ALL_DOTS_EXCEPT_LAST = "\\.(?=.*\\.)";
  private static final String REGEX_REMOVE_NOT_ALLOWED_SPECIAL_CHARS =
      "[^a-zA-Z0-9\\u00E4\\u00F6\\u00FC\\u00C4\\u00D6\\u00DC\\u00df#+.-]";
  private static final int MAX_FILE_NAME_LENGTH = 210;

  /**
   * Removes all special chars from the given file name except +-# and german umlauts and limits the
   * length of the file name.
   *
   * @param fileName file name
   * @return sanitized file name
   */
  public static String sanitizeFileName(String fileName) {

    if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
      return null;
    }

    fileName = removeSpecialChars(fileName);

    if (fileName.indexOf(".") < 1) {
      return DEFAULT_FILENAME + FilenameUtils.getExtension(fileName);
    }

    return limitFileNameLength(fileName);
  }

  private static String removeSpecialChars(final String fileName) {

    return fileName.replaceAll(REGEX_REMOVE_ALL_DOTS_EXCEPT_LAST, "")
        .replaceAll(REGEX_REMOVE_NOT_ALLOWED_SPECIAL_CHARS, "");
  }

  private static String limitFileNameLength(final String fileName) {

    final String extension = "." + FilenameUtils.getExtension(fileName);
    final String fileNameWithoutExtension = fileName
        .substring(0, fileName.length() - extension.length());

    return fileNameWithoutExtension.substring(0,
        fileName.length() > MAX_FILE_NAME_LENGTH ? MAX_FILE_NAME_LENGTH - extension.length()
            : fileNameWithoutExtension.length()) + extension;
  }

  private FileSanitizer() {
  }
}
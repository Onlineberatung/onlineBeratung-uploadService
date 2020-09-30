package de.caritas.cob.uploadservice.api.helper;

import org.apache.commons.io.FilenameUtils;

public class FileSanitizer {

  private static final String DEFAULT_FILENAME = "Anhang";

  /**
   * Removes all special chars from the given file name except +-# and german Umlauts.
   *
   * @param fileName file name
   * @return sanitized file name
   */
  public static String sanitizeFileName(String fileName) {

    fileName = fileName.replaceAll("\\.(?=.*\\.)", "")
        .replaceAll("[^a-zA-Z0-9\\u00E4\\u00F6\\u00FC\\u00C4\\u00D6\\u00DC\\u00df#+.-]", "");

    String extension = FilenameUtils.getExtension(fileName);

    if (fileName.length() - extension.length() - 1 == 0) {
      return FileSanitizer.DEFAULT_FILENAME + "." + extension;
    }

    return fileName;
  }

  private FileSanitizer() {
  }
}
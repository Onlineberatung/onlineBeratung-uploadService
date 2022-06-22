package de.caritas.cob.uploadservice.api.exception;

import org.springframework.web.multipart.MultipartException;

/**
 * Exception for wrong file type (upload)
 */
public class InvalidFileTypeException extends MultipartException {

  public InvalidFileTypeException(String msg) {
    super(msg);
  }

  /**
   * Exception for wrong file type (upload).
   *
   * @param msg   String
   * @param cause Throwable
   */
  public InvalidFileTypeException(String msg, Throwable cause) {
    super(msg, cause);
  }
}

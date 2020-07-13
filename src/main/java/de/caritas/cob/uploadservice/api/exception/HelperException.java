package de.caritas.cob.uploadservice.api.exception;

public class HelperException extends RuntimeException {
  private static final long serialVersionUID = -1321906171569622899L;

  /**
   *
   * Exception for helper errors
   *
   */
  public HelperException(String message, Exception exception) {
    super(message, exception);
  }
}

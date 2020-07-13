package de.caritas.cob.uploadservice.api.exception;

public class ServiceException extends RuntimeException {

  private static final long serialVersionUID = -1101045273426330258L;

  /**
   * Service exception
   *
   * @param message
   */
  public ServiceException(String message) {
    super(message);
  }

  /**
   * Service exception
   *
   * @param exception
   */
  public ServiceException(Exception exception) {
    super(exception);
  }

  /**
   * Service exception
   *
   * @param message
   * @param exception
   */
  public ServiceException(String message, Exception exception) {
    super(message, exception);
  }
}

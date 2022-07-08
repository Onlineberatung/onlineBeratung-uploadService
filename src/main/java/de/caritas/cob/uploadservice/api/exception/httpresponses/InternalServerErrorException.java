package de.caritas.cob.uploadservice.api.exception.httpresponses;

import de.caritas.cob.uploadservice.api.service.LogService;
import java.util.function.Consumer;

/**
 * Represents the status code 500 - Server Error.
 */
public class InternalServerErrorException extends CustomHttpStatusException {

  /**
   * InternalServerError exception.
   *
   * @param exception the caused exception
   * @param loggingMethod the method being used to log this exception
   */
  public InternalServerErrorException(Exception exception,
      Consumer<Exception> loggingMethod) {
    super(exception, loggingMethod);
  }

  /**
   * InternalServerError exception.
   *
   * @param message an additional message
   * @param exception the caused exception
   * @param loggingMethod the method being used to log this exception
   */
  public InternalServerErrorException(String message, Exception exception,
      Consumer<Exception> loggingMethod) {
    super(message, exception, loggingMethod);
  }

  /**
   * InternalServerError exception.
   *
   * @param message the exception message
   * @param ex      the exception
   */
  public InternalServerErrorException(String message, Exception ex) {
    super(message, ex, LogService::logInternalServerError);
  }
}

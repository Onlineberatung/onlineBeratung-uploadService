package de.caritas.cob.uploadservice.api.exception.httpresponses;

import java.util.function.Consumer;

/**
 * Custom exception to provide 403 - forbidden with custom quota header information.
 */
public class QuotaReachedException extends CustomHttpStatusException {

  /**
   * QuotaReachedException - FORBIDDEN - 403.
   *
   * @param loggingMethod the method used for logging
   */
  public QuotaReachedException(Consumer<Exception> loggingMethod) {
    super(loggingMethod);
  }

}

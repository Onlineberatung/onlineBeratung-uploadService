package de.caritas.cob.uploadservice.api.exception.httpresponses;

import static java.util.Objects.nonNull;

import java.util.function.Consumer;

public abstract class CustomHttpStatusException extends RuntimeException {

  private final Consumer<Exception> loggingMethod;

  CustomHttpStatusException(Exception exception, Consumer<Exception> loggingMethod) {
    super(exception);
    this.loggingMethod = loggingMethod;
  }

  CustomHttpStatusException(String message, Exception exception,
      Consumer<Exception> loggingMethod) {
    super(message, exception);
    this.loggingMethod = loggingMethod;
  }

  /**
   * Executes the non null logging method.
   */
  public void executeLogging() {
    if (nonNull(this.loggingMethod)) {
      this.loggingMethod.accept(this);
    }
  }

}

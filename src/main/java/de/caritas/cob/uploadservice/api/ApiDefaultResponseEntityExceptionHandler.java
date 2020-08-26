package de.caritas.cob.uploadservice.api;

import de.caritas.cob.uploadservice.api.service.LogService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ApiDefaultResponseEntityExceptionHandler {

  /**
   * "Catch all" respectively fallback for all controller error messages that are not specifically
   * retained by {@link ApiResponseEntityExceptionHandler}. For the caller side does not need to
   * know the exact error stack trace, this method catches the trace and logs it.
   *
   * @param ex RuntimeException
   * @param request WebRequest
   * @return
   */
  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<Object> handleInternal(final RuntimeException ex,
      final WebRequest request) {
    LogService.logInternalServerError(ex);

    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

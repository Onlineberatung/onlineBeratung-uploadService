package de.caritas.cob.uploadservice.api;

import static de.caritas.cob.uploadservice.api.exception.cutomheader.CustomHttpHeader.QUOTA_REACHED;

import de.caritas.cob.uploadservice.api.exception.InvalidFileTypeException;
import de.caritas.cob.uploadservice.api.exception.KeycloakException;
import de.caritas.cob.uploadservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.uploadservice.api.exception.httpresponses.QuotaReachedException;
import de.caritas.cob.uploadservice.api.service.LogService;
import java.net.UnknownHostException;
import javax.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Customizes API error/exception handling to hide information and/or possible security
 * vulnerabilities.
 */
@NoArgsConstructor
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handles {@link MaxUploadSizeExceededException}.
   *
   * @param ex MaxUploadSizeExceededException
   * @param request WebRequest
   * @return a {@link ResponseEntity} instance
   */
  @ExceptionHandler({MaxUploadSizeExceededException.class})
  public ResponseEntity<Object> handleCustomBadRequest(
      final MaxUploadSizeExceededException ex, final WebRequest request) {
    LogService.logWarning(ex);

    return handleExceptionInternal(
        ex, null, new HttpHeaders(), HttpStatus.PAYLOAD_TOO_LARGE, request);
  }

  /**
   * Handles {@link InvalidFileTypeException}.
   *
   * @param ex InvalidFileTypeException
   * @param request WebRequest
   * @return a {@link ResponseEntity} instance
   */
  @ExceptionHandler({InvalidFileTypeException.class})
  public ResponseEntity<Object> handleCustomBadRequest(
      final InvalidFileTypeException ex, final WebRequest request) {
    LogService.logWarning(ex);

    return handleExceptionInternal(
        ex, null, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
  }

  /**
   * Handles {@link MultipartException}.
   *
   * @param ex MultipartException
   * @param request WebRequest
   * @return a {@link ResponseEntity} instance
   */
  @ExceptionHandler({MultipartException.class})
  public ResponseEntity<Object> handleCustomBadRequest(
      final MultipartException ex, final WebRequest request) {
    LogService.logWarning(ex);

    return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Constraint violations.
   *
   * @param ex RuntimeException
   * @param request WebRequest
   * @return a ResponseEntity instance
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleBadRequest(
      final RuntimeException ex, final WebRequest request) {
    LogService.logWarning(ex);

    return handleExceptionInternal(null, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Incoming request body could not be deserialized.
   *
   * @param ex HttpMessageNotReadableException
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return a ResponseEntity instance
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    LogService.logWarning(status, ex);

    return handleExceptionInternal(null, null, headers, status, request);
  }

  /**
   * "@Valid" on object fails validation.
   *
   * @param ex MethodArgumentNotValidException
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return a ResponseEntity instance
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    LogService.logWarning(status, ex);

    return handleExceptionInternal(null, null, headers, status, request);
  }

  /**
   * 409 - Conflict.
   *
   * @param ex RuntimeException
   * @param request WebRequest
   * @return a ResponseEntity instance
   */
  @ExceptionHandler({InvalidDataAccessApiUsageException.class, DataAccessException.class})
  protected ResponseEntity<Object> handleConflict(
      final RuntimeException ex, final WebRequest request) {
    LogService.logWarning(HttpStatus.CONFLICT, ex);

    return handleExceptionInternal(null, null, new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  /**
   * {@link RestTemplate} API client errors.
   *
   * @param ex HttpClientErrorException
   * @param request WebRequest
   * @return a ResponseEntity instance
   */
  @ExceptionHandler({HttpClientErrorException.class})
  protected ResponseEntity<Object> handleHttpClientException(
      final HttpClientErrorException ex, final WebRequest request) {
    LogService.logWarning(ex.getStatusCode(), ex);

    return handleExceptionInternal(null, null, new HttpHeaders(), ex.getStatusCode(), request);
  }

  /**
   * 500 - Internal Server Error.
   *
   * @param ex RuntimeException
   * @param request WebRequest
   * @return a ResponseEntity instance
   */
  @ExceptionHandler(
      value = {
          NullPointerException.class,
          IllegalArgumentException.class,
          IllegalStateException.class,
          KeycloakException.class,
          UnknownHostException.class
      })
  public ResponseEntity<Object> handleInternal(
      final RuntimeException ex, final WebRequest request) {
    LogService.logError(ex);

    return handleExceptionInternal(
        null, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  /**
   * 500 - Internal Server Error.
   *
   * @param ex InternalServerErrorException
   * @param request WebRequest
   * @return a ResponseEntity instance
   */
  @ExceptionHandler(InternalServerErrorException.class)
  public ResponseEntity<Object> handleInternal(
      final InternalServerErrorException ex, final WebRequest request) {
    ex.executeLogging();

    return handleExceptionInternal(
        null, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  /**
   * 403 - Forbidden.
   *
   * @param ex ForbiddenException
   * @param request WebRequest
   * @return a ResponseEntity instance
   */
  @ExceptionHandler(QuotaReachedException.class)
  public ResponseEntity<Object> handleInternal(final QuotaReachedException ex,
      final WebRequest request) {
    ex.executeLogging();

    return handleExceptionInternal(null, null, QUOTA_REACHED.buildHeader(), HttpStatus.FORBIDDEN,
        request);
  }

}

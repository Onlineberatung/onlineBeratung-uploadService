package de.caritas.cob.uploadservice.api;

import de.caritas.cob.uploadservice.api.exception.CustomCryptoException;
import de.caritas.cob.uploadservice.api.exception.InvalidFileTypeException;
import de.caritas.cob.uploadservice.api.exception.KeycloakException;
import de.caritas.cob.uploadservice.api.exception.NoMasterKeyException;
import de.caritas.cob.uploadservice.api.exception.RocketChatBadRequestException;
import java.net.UnknownHostException;
import javax.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
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
@Slf4j
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
    logWarning(ex);

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
    logWarning(ex);

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
    logWarning(ex);

    return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Constraint violations.
   *
   * @param ex RuntimeException
   * @param request WebRequest
   * @return a ResponseEntity instance
   */
  @ExceptionHandler({ConstraintViolationException.class, RocketChatBadRequestException.class})
  public ResponseEntity<Object> handleBadRequest(
      final RuntimeException ex, final WebRequest request) {
    logWarning(ex);

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
    logWarning(status, ex);

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
    logWarning(status, ex);

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
    logWarning(HttpStatus.CONFLICT, ex);

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
    logWarning(ex.getStatusCode(), ex);

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
        ServiceException.class,
        KeycloakException.class,
        UnknownHostException.class,
        CustomCryptoException.class,
        NoMasterKeyException.class
      })
  public ResponseEntity<Object> handleInternal(
      final RuntimeException ex, final WebRequest request) {
    logError(ex);

    return handleExceptionInternal(
        null, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  /**
   * Logs an error.
   *
   * @param ex Exception
   */
  private void logError(final Exception ex) {
    log.error(
        "UploadService API: 500 Internal Server Error: {}",
        org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex));
  }

  /**
   * Logs a warning.
   *
   * @param status HttpStatus
   * @param ex Exception
   */
  private void logWarning(final HttpStatus status, final Exception ex) {
    log.warn(
        "UploadService API: {}: {}",
        status.getReasonPhrase(),
        org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex));
  }

  /**
   * Logs a warning.
   *
   * @param ex Exception
   */
  private void logWarning(final Exception ex) {
    log.warn(
        "UploadService API: {}: {}",
        org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex));
  }
}

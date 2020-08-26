package de.caritas.cob.uploadservice.api.service;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/** Service for logging */
public class LogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);

  private static final String RC_SERVICE_ERROR = "Rocket.Chat service error: ";
  private static final String RC_ENCRYPTION_SERVICE_ERROR = "Encryption service error: ";
  private static final String RC_ENCRYPTION_BAD_KEY_SERVICE_ERROR =
      "Encryption service error - possible bad key error: ";
  private static final String USERSERVICE_HELPER_ERROR = "UserServiceHelper error: ";
  private static final String RC_BAD_REQUEST_ERROR = "Rocket.Chat Bad Request service error: ";
  private static final String INTERNAL_SERVER_ERROR_TEXT = "Internal Server Error: ";
  private static final String BAD_REQUEST_TEXT = "Bad Request: ";
  private static final String UPLOAD_SERVICE_API_TEXT = "UploadService API: {}";

  /**
   * Logs a Rocket.Chat service error.
   *
   * @param exception the exception to be logged
   */
  public static void logRocketChatServiceError(Exception exception) {
    LOGGER.error(RC_SERVICE_ERROR + "{}", getStackTrace(exception));
  }

  /**
   * Logs a Rocket.Chat service error.
   *
   * @param message the message to be logged
   */
  public static void logRocketChatServiceError(String message) {
    LOGGER.error(RC_SERVICE_ERROR + "{}", message);
  }

  /**
   * Logs a Rocket.Chat service error.
   *
   * @param message the message to be logged
   * @param exception the exception to be logged
   */
  public static void logRocketChatServiceError(String message, Exception exception) {
    LOGGER.error(RC_SERVICE_ERROR + "{}", message);
    LOGGER.error(RC_SERVICE_ERROR + "{}", getStackTrace(exception));
  }

  /**
   * Logs a Encryption service error.
   *
   * @param exception the exception to be logged
   */
  public static void logEncryptionServiceError(Exception exception) {
    LOGGER.error(RC_ENCRYPTION_SERVICE_ERROR + "{}", getStackTrace(exception));
  }

  /**
   * Logs a Encryption possible bad key service error.
   *
   * @param exception the exception to be logged
   */
  public static void logEncryptionPossibleBadKeyError(Exception exception) {
    LOGGER.error(RC_ENCRYPTION_BAD_KEY_SERVICE_ERROR + "{}", getStackTrace(exception));
  }

  /**
   * Logs a Rocket.Chat Bad Request error.
   *
   * @param exception the exception to be logged
   */
  public static void logRocketChatBadRequestError(Exception exception) {
    LOGGER.error(RC_BAD_REQUEST_ERROR + "{}", getStackTrace(exception));
  }

  /**
   * Logs a UserServiceHelper error.
   *
   * @param exception the exception to be logged
   */
  public static void logUserServiceHelperError(Exception exception) {
    LOGGER.error(USERSERVICE_HELPER_ERROR + "{}", getStackTrace(exception));
  }

  /**
   * Logs a Info message.
   *
   * @param msg The message
   */
  public static void logInfo(String msg) {
    LOGGER.info(msg);
  }

  /**
   * Internal Server Error/Exception.
   *
   * @param message the message to be logged
   * @param exception the exception to be logged
   */
  public static void logInternalServerError(String message, Exception exception) {
    LOGGER.error("{}{}", INTERNAL_SERVER_ERROR_TEXT, message);
    LOGGER.error("{}", getStackTrace(exception));
  }

  /**
   * Internal Server Error/Exception.
   *
   * @param exception the exception to be logged
   */
  public static void logInternalServerError(Exception exception) {
    LOGGER.error("{}", INTERNAL_SERVER_ERROR_TEXT);
    LOGGER.error("{}", getStackTrace(exception));
  }

  /**
   * Bad Request.
   *
   * @param message the message to be logged
   */
  public static void logBadRequest(String message) {
    LOGGER.error(BAD_REQUEST_TEXT + "{}", message);
  }

  /**
   * Warning.
   *
   * @param exception the exception to be logged
   */
  public static void logWarning(Exception exception) {
    LOGGER.warn(UPLOAD_SERVICE_API_TEXT, getStackTrace(exception));
  }

  /**
   * Warning.
   *
   * @param httpStatus an status to be logged
   * @param exception the exception to be logged
   */
  public static void logWarning(HttpStatus httpStatus, Exception exception) {
    LOGGER.warn("UploadService API: {}: {}", httpStatus.getReasonPhrase(),
        getStackTrace(exception));
  }

  /**
   * Logs an error message.
   *
   * @param exception the exception to be logged
   */
  public static void logError(Exception exception) {
    LOGGER.error(UPLOAD_SERVICE_API_TEXT, getStackTrace(exception));
  }

  /**
   * Logs an debug message.
   *
   * @param message the message to be logged
   */
  public static void logDebug(String message) {
    LOGGER.debug(UPLOAD_SERVICE_API_TEXT, message);
  }
}

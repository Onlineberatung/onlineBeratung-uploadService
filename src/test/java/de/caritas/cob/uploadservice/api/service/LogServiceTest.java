package de.caritas.cob.uploadservice.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.io.PrintWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class LogServiceTest {

  private static final String ERROR_MESSAGE = "error";
  private static final String RC_SERVICE_ERROR_TEXT = "Rocket.Chat service error: ";
  private static final String INTERNAL_SERVER_ERROR_TEXT = "Internal Server Error: ";
  private static final String BAD_REQUEST_TEXT = "Bad Request: ";

  @Mock
  Exception exception;

  @Mock
  private Logger logger;

  @Before
  public void setup() {
    setInternalState(LogService.class, "LOGGER", logger);
  }

  /**
   * Tests for method: logRocketChatServiceError
   */
  @Test
  public void logRocketChatServiceError_Should_LogExceptionStackTrace() {

    LogService.logRocketChatServiceError(exception);
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  @Test
  public void logRocketChatServiceError_Should_LogErrorMessage() {

    LogService.logRocketChatServiceError(ERROR_MESSAGE);
    verify(logger, times(1)).error(eq(RC_SERVICE_ERROR_TEXT + "{}"),
        eq(ERROR_MESSAGE));
  }

  @Test
  public void logRocketChatServiceError_Should_LogErrorMessageAndExceptionStackTrace() {

    LogService.logRocketChatServiceError(ERROR_MESSAGE, exception);
    verify(logger, times(1)).error(eq(RC_SERVICE_ERROR_TEXT + "{}"),
        eq(ERROR_MESSAGE));
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  /**
   * Tests for method: logUserServiceHelperError
   */
  @Test
  public void logUserServiceHelperError_Should_LogExceptionStackTrace() {

    LogService.logUserServiceHelperError(exception);
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  /**
   * Tests for method: logInfo
   */
  @Test
  public void logInfo_Should_LogMessage() {

    LogService.logInfo(ERROR_MESSAGE);
    verify(logger, times(1)).info(eq(ERROR_MESSAGE));
  }

  /**
   * Tests for method: logEncryptionServiceError
   */
  @Test
  public void logEncryptionServiceError_Should_LogExceptionStackTrace() {

    LogService.logEncryptionServiceError(exception);
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  /**
   * Tests for method: logRocketChatBadRequestError
   */
  @Test
  public void logRocketChatBadRequestError_Should_LogExceptionStackTrace() {

    LogService.logRocketChatBadRequestError(exception);
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  /**
   * Tests for method: logInternalServerError
   */
  @Test
  public void logInternalServerError_Should_LogErrorMessageAndExceptionStackTrace() {

    LogService.logInternalServerError(ERROR_MESSAGE, exception);
    verify(logger, times(1))
        .error(anyString(), eq(INTERNAL_SERVER_ERROR_TEXT), eq(ERROR_MESSAGE));
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  @Test
  public void logInternalServerError_Should_LogExceptionStackTrace() {

    LogService.logInternalServerError(exception);
    verify(logger, times(1)).error(anyString(), eq(INTERNAL_SERVER_ERROR_TEXT));
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  /**
   * Tests for method: logInfo
   */
  @Test
  public void logBadRequest_Should_LogMessage() {

    LogService.logBadRequest(ERROR_MESSAGE);
    verify(logger, times(1)).error(eq(BAD_REQUEST_TEXT + "{}"),
        eq(ERROR_MESSAGE));
  }

  @Test
  public void logWarning_Should_LogMessage() {

    LogService.logWarning(exception);
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
    verify(logger, times(1)).warn(anyString(), anyString());
  }

  @Test
  public void logWarning_Should_LogMessageAndStatus() {

    LogService.logWarning(HttpStatus.ACCEPTED, exception);
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
    verify(logger, times(1)).warn(anyString(), eq(HttpStatus.ACCEPTED.getReasonPhrase()),
        anyString());
  }

  @Test
  public void logError_Should_LogMessage() {

    LogService.logError(exception);
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
    verify(logger, times(1)).error(anyString(), anyString());
  }

  @Test
  public void logDebug_Should_LogMessage() {

    LogService.logDebug(ERROR_MESSAGE);
    verify(logger, times(1)).debug(anyString(), eq(ERROR_MESSAGE));
  }
}

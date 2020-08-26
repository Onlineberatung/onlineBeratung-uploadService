package de.caritas.cob.uploadservice.api.service;

import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_NAME_RC_HEADER_AUTH_TOKEN;
import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_NAME_RC_HEADER_USER_ID;
import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_NAME_RC_POST_GROUP_MESSAGES_READ;
import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_NAME_RC_POST_USER_LOGIN_URL;
import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_NAME_RC_ROOMS_UPLOAD_URL;
import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_VALUE_RC_POST_GROUP_MESSAGES_READ;
import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_VALUE_RC_POST_USER_LOGIN_URL;
import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_VALUE_RC_ROOMS_UPLOAD_URL;
import static de.caritas.cob.uploadservice.helper.TestConstants.ERROR_MSG;
import static de.caritas.cob.uploadservice.helper.TestConstants.INVALID_RC_SYSTEM_USER;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_DESCRIPTION;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_MESSAGE;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_SYSTEM_USER;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_TMID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_TOKEN;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_ENTITY_TOO_LARGE;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_INVALID_FILE_TYPE;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_RESPONSE_BODY_ENTITY_TOO_LARGE;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_RESPONSE_BODY_INVALID_FILE_TYPE;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_RESPONSE_BODY_UNKNOWN_ERROR;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_RESPONSE_DTO_ENTITY_TOO_LARGE;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_RESPONSE_DTO_INVALID_FILE_TYPE;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_RESPONSE_DTO_SUCCESS;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_RESPONSE_DTO_UNKNOWN_ERROR;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_UNKNOWN_ERROR;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_USER_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.STANDARD_SUCCESS_RESPONSE_DTO;
import static de.caritas.cob.uploadservice.helper.TestConstants.UPLOAD_FILE;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.exception.InvalidFileTypeException;
import de.caritas.cob.uploadservice.api.exception.RocketChatPostMarkGroupAsReadException;
import de.caritas.cob.uploadservice.api.helper.UploadErrorHelper;
import de.caritas.cob.uploadservice.api.model.rocket.chat.StandardResponseDto;
import de.caritas.cob.uploadservice.api.model.rocket.chat.UploadResponseDto;
import de.caritas.cob.uploadservice.api.service.helper.RocketChatCredentialsHelper;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@RunWith(MockitoJUnitRunner.class)
public class RocketChatServiceTest {

  @InjectMocks private RocketChatService rocketChatService;
  @Mock RocketChatCredentialsHelper rcCredentialsHelper;
  @Mock private RestTemplate restTemplate;
  @Mock private Logger logger;
  @Mock private EncryptionService encryptionService;
  @Mock private UploadErrorHelper uploadErrorHelper;

  private RocketChatCredentials rocketChatCredentials;
  private RocketChatUploadParameter rocketChatUploadParameter;

  /**
   * Setup method.
   *
   * @throws NoSuchFieldException
   * @throws SecurityException
   */
  @Before
  public void setup() throws NoSuchFieldException, SecurityException {
    FieldSetter.setField(
        rocketChatService,
        rocketChatService.getClass().getDeclaredField(FIELD_NAME_RC_HEADER_AUTH_TOKEN),
        String.valueOf(RC_TOKEN));
    FieldSetter.setField(
        rocketChatService,
        rocketChatService.getClass().getDeclaredField(FIELD_NAME_RC_HEADER_USER_ID),
        String.valueOf(RC_USER_ID));
    FieldSetter.setField(
        rocketChatService,
        rocketChatService.getClass().getDeclaredField(FIELD_NAME_RC_POST_GROUP_MESSAGES_READ),
        FIELD_VALUE_RC_POST_GROUP_MESSAGES_READ);
    FieldSetter.setField(
        rocketChatService,
        rocketChatService.getClass().getDeclaredField(FIELD_NAME_RC_POST_USER_LOGIN_URL),
        FIELD_VALUE_RC_POST_USER_LOGIN_URL);
    FieldSetter.setField(
        rocketChatService,
        rocketChatService.getClass().getDeclaredField(FIELD_NAME_RC_ROOMS_UPLOAD_URL),
        FIELD_VALUE_RC_ROOMS_UPLOAD_URL);

    rocketChatCredentials =
        RocketChatCredentials.builder()
            .rocketChatToken(RC_TOKEN)
            .rocketChatUserId(RC_USER_ID)
            .build();

    rocketChatUploadParameter =
        RocketChatUploadParameter.builder()
            .description(RC_DESCRIPTION)
            .file(UPLOAD_FILE)
            .message(RC_MESSAGE)
            .roomId(RC_ROOM_ID)
            .tmId(RC_TMID)
            .build();

    setInternalState(LogService.class, "LOGGER", logger);
  }

  /* Method: markGroupAsReadForSystemUser */
  @Test
  public void markGroupAsReadForSystemUser_Should_LogError_WhenMarkGroupAsReadFails()
      throws NoSuchFieldException, SecurityException {

    when(rcCredentialsHelper.getSystemUser()).thenReturn(RC_SYSTEM_USER);

    RestClientException ex = new RestClientException(ERROR_MSG);
    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<StandardResponseDto>>any()))
        .thenThrow(ex);

    try {
      rocketChatService.markGroupAsReadForSystemUser(RC_ROOM_ID);
      fail("Expected exception: RocketChatPostMarkGroupAsReadException");
    } catch (RocketChatPostMarkGroupAsReadException rocketChatPostMarkGroupAsReadException) {
      assertTrue("Expected RocketChatPostMarkGroupAsReadException thrown", true);
    }

    verify(logger, times(1)).error(anyString(), eq(getStackTrace(ex)));
  }

  @Test
  public void markGroupAsReadForSystemUser_Should_MarkGroupAsRead_WhenProvidedWithValidGroupId()
      throws NoSuchFieldException, SecurityException {

    when(rcCredentialsHelper.getSystemUser()).thenReturn(RC_SYSTEM_USER);

    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<StandardResponseDto>>any()))
        .thenReturn(STANDARD_SUCCESS_RESPONSE_DTO);

    boolean response = rocketChatService.markGroupAsReadForSystemUser(RC_ROOM_ID);
    assertTrue(response);
  }

  @Test
  public void
      markGroupAsReadForSystemUser_Should_LogError_WhenProvidedWithInvalidRChatSysUserCredentials()
          throws NoSuchFieldException, SecurityException {

    when(rcCredentialsHelper.getSystemUser()).thenReturn(INVALID_RC_SYSTEM_USER);

    boolean response = rocketChatService.markGroupAsReadForSystemUser(RC_ROOM_ID);
    assertFalse(response);
    verify(logger, times(1)).error(anyString(), anyString());
  }

  /* Method: roomsUpload */
  @Test
  public void
      roomsUpload_Should_ThrowMaxUploadSizeExceededException_WhenRcErrorTypeIsFileTooLarge() {

    HttpStatusCodeException httpStatusCodeException =
        new HttpServerErrorException(
            HttpStatus.BAD_REQUEST,
            ERROR_MSG,
            RC_UPLOAD_ERROR_RESPONSE_BODY_ENTITY_TOO_LARGE.getBytes(),
            StandardCharsets.UTF_8);
    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<UploadResponseDto>>any()))
        .thenThrow(httpStatusCodeException);

    when(uploadErrorHelper.getParsedErrorResponse(
            httpStatusCodeException.getResponseBodyAsString()))
        .thenReturn(RC_UPLOAD_ERROR_RESPONSE_DTO_ENTITY_TOO_LARGE);
    when(uploadErrorHelper.getErrorFromUploadResponse(
            Mockito.eq(RC_UPLOAD_ERROR_RESPONSE_DTO_ENTITY_TOO_LARGE),
            Mockito.anyString(),
            Mockito.anyString()))
        .thenReturn(RC_UPLOAD_ERROR_ENTITY_TOO_LARGE);

    try {
      rocketChatService.roomsUpload(rocketChatCredentials, rocketChatUploadParameter);
      fail("Expected exception: MaxUploadSizeExceededException");
    } catch (MaxUploadSizeExceededException maxUploadSizeExceededException) {
      assertTrue("Expected MaxUploadSizeExceededException thrown", true);
    }
  }

  @Test
  public void roomsUpload_Should_LogErrorMessage_WhenRcErrorTypeIsFileTooLarge() {

    HttpStatusCodeException httpStatusCodeException =
        new HttpServerErrorException(
            HttpStatus.BAD_REQUEST,
            ERROR_MSG,
            RC_UPLOAD_ERROR_RESPONSE_BODY_ENTITY_TOO_LARGE.getBytes(),
            StandardCharsets.UTF_8);
    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<UploadResponseDto>>any()))
        .thenThrow(httpStatusCodeException);

    when(uploadErrorHelper.getParsedErrorResponse(
            httpStatusCodeException.getResponseBodyAsString()))
        .thenReturn(RC_UPLOAD_ERROR_RESPONSE_DTO_ENTITY_TOO_LARGE);
    when(uploadErrorHelper.getErrorFromUploadResponse(
            Mockito.eq(RC_UPLOAD_ERROR_RESPONSE_DTO_ENTITY_TOO_LARGE),
            Mockito.anyString(),
            Mockito.anyString()))
        .thenReturn(RC_UPLOAD_ERROR_ENTITY_TOO_LARGE);

    try {
      rocketChatService.roomsUpload(rocketChatCredentials, rocketChatUploadParameter);
    } catch (MaxUploadSizeExceededException maxUploadSizeExceededException) {
      // nop
    }

    verify(logger, times(1))
        .error("Rocket.Chat service error: {}", RC_UPLOAD_ERROR_ENTITY_TOO_LARGE.getErrorMessage());
  }

  @Test
  public void roomsUpload_Should_ThrowInvalidFileTypeException_WhenRcErrorTypeIsInvalidFileType() {

    HttpStatusCodeException httpStatusCodeException =
        new HttpServerErrorException(
            HttpStatus.BAD_REQUEST,
            ERROR_MSG,
            RC_UPLOAD_ERROR_RESPONSE_BODY_INVALID_FILE_TYPE.getBytes(),
            StandardCharsets.UTF_8);
    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<UploadResponseDto>>any()))
        .thenThrow(httpStatusCodeException);

    when(uploadErrorHelper.getParsedErrorResponse(
            httpStatusCodeException.getResponseBodyAsString()))
        .thenReturn(RC_UPLOAD_ERROR_RESPONSE_DTO_INVALID_FILE_TYPE);
    when(uploadErrorHelper.getErrorFromUploadResponse(
            Mockito.eq(RC_UPLOAD_ERROR_RESPONSE_DTO_INVALID_FILE_TYPE),
            Mockito.anyString(),
            Mockito.anyString()))
        .thenReturn(RC_UPLOAD_ERROR_INVALID_FILE_TYPE);

    try {
      rocketChatService.roomsUpload(rocketChatCredentials, rocketChatUploadParameter);
      fail("Expected exception: InvalidFileTypeException");
    } catch (InvalidFileTypeException invalidFileTypeException) {
      assertTrue("Expected InvalidFileTypeException thrown", true);
    }
  }

  @Test
  public void roomsUpload_Should_LogErrorMessage_WhenRcErrorTypeIsInvalidFileType() {

    HttpStatusCodeException httpStatusCodeException =
        new HttpServerErrorException(
            HttpStatus.BAD_REQUEST,
            ERROR_MSG,
            RC_UPLOAD_ERROR_RESPONSE_BODY_INVALID_FILE_TYPE.getBytes(),
            StandardCharsets.UTF_8);
    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<UploadResponseDto>>any()))
        .thenThrow(httpStatusCodeException);

    when(uploadErrorHelper.getParsedErrorResponse(
            httpStatusCodeException.getResponseBodyAsString()))
        .thenReturn(RC_UPLOAD_ERROR_RESPONSE_DTO_INVALID_FILE_TYPE);
    when(uploadErrorHelper.getErrorFromUploadResponse(
            Mockito.eq(RC_UPLOAD_ERROR_RESPONSE_DTO_INVALID_FILE_TYPE),
            Mockito.anyString(),
            Mockito.anyString()))
        .thenReturn(RC_UPLOAD_ERROR_INVALID_FILE_TYPE);

    try {
      rocketChatService.roomsUpload(rocketChatCredentials, rocketChatUploadParameter);
    } catch (InvalidFileTypeException invalidFileTypeException) {
      // nop
    }

    verify(logger, times(1))
        .error("Rocket.Chat service error: {}",
            RC_UPLOAD_ERROR_INVALID_FILE_TYPE.getErrorMessage());
  }

  @Test
  public void roomsUpload_Should_ThrowMultipartException_WhenRcErrorIsUnknown() {

    HttpStatusCodeException httpStatusCodeException =
        new HttpServerErrorException(
            HttpStatus.BAD_REQUEST,
            ERROR_MSG,
            RC_UPLOAD_ERROR_RESPONSE_BODY_UNKNOWN_ERROR.getBytes(),
            StandardCharsets.UTF_8);
    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<UploadResponseDto>>any()))
        .thenThrow(httpStatusCodeException);

    when(uploadErrorHelper.getParsedErrorResponse(
            httpStatusCodeException.getResponseBodyAsString()))
        .thenReturn(RC_UPLOAD_ERROR_RESPONSE_DTO_UNKNOWN_ERROR);
    when(uploadErrorHelper.getErrorFromUploadResponse(
            Mockito.eq(RC_UPLOAD_ERROR_RESPONSE_DTO_UNKNOWN_ERROR),
            Mockito.anyString(),
            Mockito.anyString()))
        .thenReturn(RC_UPLOAD_ERROR_UNKNOWN_ERROR);

    try {
      rocketChatService.roomsUpload(rocketChatCredentials, rocketChatUploadParameter);
      fail("Expected exception: MultipartException");
    } catch (MultipartException multipartException) {
      assertTrue("Expected MultipartException thrown", true);
    }
  }

  @Test
  public void roomsUpload_Should_ThrowMultipartException_WhenResponseIsNotSuccess() {

    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<UploadResponseDto>>any()))
        .thenReturn(RC_UPLOAD_ERROR_RESPONSE_DTO_UNKNOWN_ERROR);

    try {
      rocketChatService.roomsUpload(rocketChatCredentials, rocketChatUploadParameter);
      fail("Expected exception: MultipartException");
    } catch (MultipartException multipartException) {
      assertTrue("Expected MultipartException thrown", true);
    }
  }

  @Test
  public void roomsUpload_Should_ThrowMultipartException_WhenResponseIsNull() {

    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<UploadResponseDto>>any()))
        .thenReturn(null);

    try {
      rocketChatService.roomsUpload(rocketChatCredentials, rocketChatUploadParameter);
      fail("Expected exception: MultipartException");
    } catch (MultipartException multipartException) {
      assertTrue("Expected MultipartException thrown", true);
    }
  }

  @Test
  public void roomsUpload_Should_NotThrowException_WhenResponseIsSuccess() {

    when(restTemplate.postForObject(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(),
            ArgumentMatchers.<Class<UploadResponseDto>>any()))
        .thenReturn(RC_UPLOAD_ERROR_RESPONSE_DTO_SUCCESS);

    try {
      rocketChatService.roomsUpload(rocketChatCredentials, rocketChatUploadParameter);
    } catch (Exception exception) {
      fail("Method should not throw exception on success");
    }
  }
}

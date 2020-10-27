package de.caritas.cob.uploadservice.helper;

import static de.caritas.cob.uploadservice.api.service.RocketChatService.ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE;
import static de.caritas.cob.uploadservice.api.service.RocketChatService.ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE;

import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.UploadError;
import de.caritas.cob.uploadservice.api.model.rocket.chat.StandardResponseDto;
import de.caritas.cob.uploadservice.api.model.rocket.chat.UploadResponseDto;
import org.springframework.mock.web.MockMultipartFile;

public class TestConstants {

  /* COMMON */
  public static final String INVALID_JSON_BODY = "{\"success\":";
  public static final String FIELD_NAME_LOGSERVICE = "logService";
  public static final String ERROR_MSG = "error";

  /* Keycloak */
  public static final String KEYCLOAK_ACCESS_TOKEN = "jklsfljkeiouwi34jklfsjlksfjlkf";
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";

  /* Master Key */
  public static final String MASTER_KEY_1 = "key1";
  public static final String MASTER_KEY_2 = "key2";
  public static final String MASTER_KEY_DTO_KEY_1 = "{\"masterKey\": \"" + MASTER_KEY_1 + "\"}";
  public static final String MASTER_KEY_DTO_KEY_2 = "{\"masterKey\": \"" + MASTER_KEY_2 + "\"}";

  /* CSRF */
  public static final String CSRF_COOKIE = "CSRF-TOKEN";
  public static final String CSRF_HEADER = "X-CSRF-TOKEN";
  public static final String CSRF_VALUE = "test";

  /*
   * User
   */
  public static final String ENCODING_PREFIX = "enc.";
  public static final String USERNAME_ENCODED = ENCODING_PREFIX + "OVZWK4TOMFWWK...";
  public static final String USERNAME_DECODED = "username";
  public static final String USERNAME_INVALID_ENCODED = "enc.223======";

  /* Rocket.Chat */
  public static final String RC_ROOM_ID = "yhZt736Kalh";
  public static final String RC_ROOM_ID_WITH_HTML = "yhZt736Kalh<body>";
  public static final String RC_TMID = "jkadsjk833";
  public static final String RC_TMID_WITH_HTML = "<strong>jkadsjk833";
  public static final String RC_UPLOAD_ERROR_RESPONSE_BODY =
      "{\n"
          + "    \"success\": false,\n"
          + "    \"error\": \"Invalid room [error-invalid-room]\",\n"
          + "    \"errorType\": \"error-invalid-room\"\n"
          + "}";
  public static final String RC_TOKEN_HEADER_PARAMETER_NAME = "RCToken";
  public static final String RC_USER_ID_HEADER_PARAMETER_NAME = "RCUserId";
  public static final String RC_TOKEN = "2fUGwNSqvpiEDTsMJQ54XeYdx0XzzCWdu0PP0lXFNu8";
  public static final String RC_USER_ID = "ogCRwt3ieDiBNJFaR";
  public static final String RC_MESSAGE = "message";
  public static final String RC_MESSAGE_WITH_HTML = "<html>message";
  public static final String RC_DESCRIPTION = "description";
  public static final String RC_DESCRIPTION_WITH_HTML = "description<font>";
  public static final String RC_SYSTEM_USERNAME = "system";
  public static final String RC_SYSTEM_USER_ID = "systemId";
  public static final String RC_SYSTEM_USER_AUTH_TOKEN = "systemToken";
  public static final StandardResponseDto STANDARD_SUCCESS_RESPONSE_DTO =
      new StandardResponseDto(true, null);
  public static final RocketChatCredentials RC_SYSTEM_USER =
      RocketChatCredentials.builder()
          .rocketChatToken(RC_SYSTEM_USER_AUTH_TOKEN)
          .rocketChatUserId(RC_SYSTEM_USER_ID)
          .rocketChatUsername(RC_SYSTEM_USERNAME)
          .build();
  public static final RocketChatCredentials INVALID_RC_SYSTEM_USER =
      RocketChatCredentials.builder().build();
  public static final UploadResponseDto RC_UPLOAD_ERROR_RESPONSE_DTO_ENTITY_TOO_LARGE =
      new UploadResponseDto(
          false, ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE, ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE);
  public static final UploadError RC_UPLOAD_ERROR_ENTITY_TOO_LARGE =
      UploadError.builder()
          .rcError(ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE)
          .rcErrorType(ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE)
          .errorMessage(ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE)
          .build();
  public static final String RC_UPLOAD_ERROR_RESPONSE_BODY_ENTITY_TOO_LARGE =
      "{"
          + "    \"success\": false,"
          + "    \"error\": \""
          + ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE
          + "\","
          + "    \"errorType\": \""
          + ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE
          + "\""
          + "}";
  public static final UploadResponseDto RC_UPLOAD_ERROR_RESPONSE_DTO_INVALID_FILE_TYPE =
      new UploadResponseDto(
          false,
          ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE,
          ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE);
  public static final UploadError RC_UPLOAD_ERROR_INVALID_FILE_TYPE =
      UploadError.builder()
          .rcError(ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE)
          .rcErrorType(ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE)
          .errorMessage(ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE)
          .build();
  public static final String RC_UPLOAD_ERROR_RESPONSE_BODY_INVALID_FILE_TYPE =
      "{"
          + "    \"success\": false,"
          + "    \"error\": \""
          + ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE
          + "\","
          + "    \"errorType\": \""
          + ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE
          + "\""
          + "}";
  public static final UploadResponseDto RC_UPLOAD_ERROR_RESPONSE_DTO_UNKNOWN_ERROR =
      new UploadResponseDto(false, ERROR_MSG, ERROR_MSG);
  public static final UploadError RC_UPLOAD_ERROR_UNKNOWN_ERROR =
      UploadError.builder()
          .rcError(ERROR_MSG)
          .rcErrorType(ERROR_MSG)
          .errorMessage(ERROR_MSG)
          .build();
  public static final String RC_UPLOAD_ERROR_RESPONSE_BODY_UNKNOWN_ERROR =
      "{"
          + "    \"success\": false,"
          + "    \"error\": \""
          + ERROR_MSG
          + "\","
          + "    \"errorType\": \""
          + ERROR_MSG
          + "\""
          + "}";
  public static final UploadResponseDto RC_UPLOAD_ERROR_RESPONSE_DTO_SUCCESS =
      new UploadResponseDto(true, null, null);

  /* Upload */
  public static final String FORM_PARAM_SEND_NOTIFICATION = "sendNotification";
  public static final String FORM_PARAM_SEND_NOTIFICATION_TRUE = "true";
  public static final String FORM_PARAM_DESCRIPTION = "description";
  public static final String FORM_PARAM_DESCRIPTION_VALUE = "This is the description";
  public static final String FORM_PARAM_MESSAGE = "msg";
  public static final String FORM_PARAM_MESSAGE_VALUE = "This is the message";
  public static final String FORM_PARAM_TMID = "tmId";
  public static final String FORM_PARAM_TMID_VALUE = "This is the tmid";
  public static final String FORM_PARAM_FILE = "file";
  public static final String FILE_NAME_UNSANITIZED = "äöüßÄÖÜt!\"e...~´´`::.jpg;.jpg;st-+_#$1.jpg.doc";
  public static final String FILE_NAME_UNSANITIZED_WITH_SPACES = "   äöüßÄÖÜt!\"e ...~´´`::.jpg;.jpg;st-+_#$1.jpg.doc  ";
  public static final String FILE_NAME_SANITIZED = "äöüßÄÖÜtejpgjpgst-+#1jpg.doc";
  public static final String FILE_NAME_SANITIZED_WITH_SPACES = "äöüßÄÖÜte jpgjpgst-+#1jpg.doc";
  public static final String FILE_NAME_ONLY_SPECIAL_CHARS = "!...~´´`.;;;§%%%_$.jpg";
  public static final String FILE_NAME_DEFAULT = "Anhang.jpg";
  public static final MockMultipartFile UPLOAD_FILE =
      new MockMultipartFile("uploadFile", "uploadFile.txt", "text/plain", "content".getBytes());
  public static final MockMultipartFile UNSANITIZED_UPLOAD_FILE =
      new MockMultipartFile("uploadFile", FILE_NAME_UNSANITIZED, "text/plain",
          "content".getBytes());
}

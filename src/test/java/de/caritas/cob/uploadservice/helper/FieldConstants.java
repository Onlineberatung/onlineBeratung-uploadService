package de.caritas.cob.uploadservice.helper;

public class FieldConstants {

  /**
   * RocketChatService
   */
  public static final String FIELD_NAME_RC_POST_GROUP_MESSAGES_READ = "rcPostGroupMessagesRead";
  public static final String FIELD_VALUE_RC_POST_GROUP_MESSAGES_READ = "/api/v1/subscriptions.read";
  public static final String FIELD_NAME_RC_HEADER_AUTH_TOKEN = "rcHeaderAuthToken";
  public static final String FIELD_NAME_RC_HEADER_USER_ID = "rcHeaderUserId";
  public static final String FIELD_NAME_RC_ROOMS_UPLOAD_URL = "rcRoomsUpload";
  public static final String FIELD_VALUE_RC_ROOMS_UPLOAD_URL =
      "http://localhost/api/v1/rooms.upload";

  /**
   * ServiceHelper
   */
  public static final String FIELD_NAME_CSRF_TOKEN_HEADER_PROPERTY = "csrfHeaderProperty";
  public static final String FIELD_NAME_CSRF_TOKEN_COOKIE_PROPERTY = "csrfCookieProperty";
  public static final String CSRF_TOKEN_HEADER_VALUE = "X-CSRF-TOKEN";
  public static final String CSRF_TOKEN_COOKIE_VALUE = "CSRF-TOKEN";
}

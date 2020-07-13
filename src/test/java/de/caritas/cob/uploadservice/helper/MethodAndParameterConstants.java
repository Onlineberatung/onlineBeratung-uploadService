package de.caritas.cob.uploadservice.helper;

import org.springframework.web.multipart.MultipartFile;

public class MethodAndParameterConstants {

  /* UploadController */
  public static final String UPLOAD_FILE_TO_ROOM_METHOD_NAME = "uploadFileToRoom";
  public static final Class<?>[] UPLOAD_FILE_TO_ROOM_METHOD_PARAMS =
      new Class[] {
        String.class,
        String.class,
        String.class,
        MultipartFile.class,
        String.class,
        String.class,
        String.class,
        String.class
      };
  public static final String UPLOAD_FILE_TO_FEEDBACK_ROOM_METHOD_NAME = "uploadFileToFeedbackRoom";
  public static final Class<?>[] UPLOAD_FILE_TO_FEEDBACK_ROOM_METHOD_PARAMS =
      new Class[] {
        String.class,
        String.class,
        String.class,
        MultipartFile.class,
        String.class,
        String.class,
        String.class,
        String.class
      };
}

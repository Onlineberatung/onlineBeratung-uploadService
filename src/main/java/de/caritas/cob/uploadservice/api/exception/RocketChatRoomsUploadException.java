package de.caritas.cob.uploadservice.api.exception;

public class RocketChatRoomsUploadException extends RuntimeException {

  private static final long serialVersionUID = -5345387091852981692L;

  /**
   * Exception, when a Rocket.Chat API call for uploading a file to a room fails
   *
   * @param exception Exception
   */
  public RocketChatRoomsUploadException(Exception exception) {
    super(exception);
  }

  /**
   * Exception, when a Rocket.Chat API call for uploading a file to a room fails
   *
   * @param message Exception
   * @param exception Exception
   */
  public RocketChatRoomsUploadException(String message, Exception exception) {
    super(message, exception);
  }

  /**
   * Exception, when a Rocket.Chat API call for uploading a file to a room fails
   *
   * @param message Exception
   */
  public RocketChatRoomsUploadException(String message) {
    super(message);
  }
}

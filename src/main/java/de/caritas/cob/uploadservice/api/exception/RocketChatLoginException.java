package de.caritas.cob.uploadservice.api.exception;

public class RocketChatLoginException extends RuntimeException {

  private static final long serialVersionUID = 5198347832036308397L;

  /**
   * Exception when login for technical user in Rocket.Chat fails
   * 
   * @param ex
   */
  public RocketChatLoginException(Exception ex) {
    super(ex);
  }

  public RocketChatLoginException(String message) {
    super(message);
  }
}

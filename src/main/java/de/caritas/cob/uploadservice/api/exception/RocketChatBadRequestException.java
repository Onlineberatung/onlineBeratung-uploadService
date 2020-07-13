package de.caritas.cob.uploadservice.api.exception;

public class RocketChatBadRequestException extends RuntimeException {

  private static final long serialVersionUID = 362702101121444833L;

  /**
   * Exception, when a Rocket.Chat API call for message posting fails due to a Bad Request
   * 
   * @param message
   */
  public RocketChatBadRequestException(String message) {
    super(message);
  }

}

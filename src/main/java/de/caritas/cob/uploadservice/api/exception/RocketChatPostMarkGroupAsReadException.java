package de.caritas.cob.uploadservice.api.exception;

public class RocketChatPostMarkGroupAsReadException extends RuntimeException {

  private static final long serialVersionUID = -5666387091852981692L;

  /**
   * Exception, when a Rocket.Chat API call for marking a room as read fails
   * 
   * @param ex
   */
  public RocketChatPostMarkGroupAsReadException(Exception ex) {
    super(ex);
  }

}

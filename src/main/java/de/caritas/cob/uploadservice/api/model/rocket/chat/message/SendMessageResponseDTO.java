package de.caritas.cob.uploadservice.api.model.rocket.chat.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response object for Rocket.Chat API Call for posting a message See <a
 * href="https://developer.rocket.chat/reference/api/rest-api/endpoints/team-collaboration-endpoints/chat-endpoints/sendmessage">send
 * message documentation</a>
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageResponseDTO {

  private SendMessageResultDTO message;
  private boolean success;
  private String error;
  private String errorType;

}

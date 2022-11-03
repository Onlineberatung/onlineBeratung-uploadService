package de.caritas.cob.uploadservice.api.model.rocket.chat.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body object for Rocket.Chat API Call for sending a message. See <a
 * href="https://developer.rocket.chat/reference/api/rest-api/endpoints/team-collaboration-endpoints/chat-endpoints/sendmessage">send
 * message documentation</a>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageDTO {

  private String rid;
  private String msg;
  private String org;
  private String alias;
  private String t;

}

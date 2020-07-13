package de.caritas.cob.uploadservice.api.container;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Parameter object for Rocket.Chat Credentials. */
@Getter
@Setter
@Builder
public class RocketChatCredentials {

  private String rocketChatToken;
  private String rocketChatUserId;
  private String rocketChatUsername;
  private LocalDateTime timeStampCreated;
}

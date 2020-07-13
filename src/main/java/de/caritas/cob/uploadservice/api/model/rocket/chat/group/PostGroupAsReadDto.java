package de.caritas.cob.uploadservice.api.model.rocket.chat.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body object for Rocket.Chat API Call for marking a room/group as read
 * https://rocket.chat/docs/developer-guides/rest-api/subscriptions/read/
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostGroupAsReadDto {

  @JsonProperty("rid")
  private String roomId;
}

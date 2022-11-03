package de.caritas.cob.uploadservice.api.model.rocket.chat.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageResultDTO {

  @JsonProperty("_id")
  private String id;
  private String rid;
  private String msg;
  private String t;
  private String e2e;
  private String org;
  @JsonProperty("ts")
  private Date timestamp;
  @JsonProperty("_updatedAt")
  private Date updatedAt;
}

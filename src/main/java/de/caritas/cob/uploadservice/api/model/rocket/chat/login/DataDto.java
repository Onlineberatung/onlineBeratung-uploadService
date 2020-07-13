package de.caritas.cob.uploadservice.api.model.rocket.chat.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DataDTO for LoginResponseDTO */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataDto {
  private String userId;
  private String authToken;
  private MeDto me;
}

package de.caritas.cob.uploadservice.api.model.rocket.chat.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** EmailsDTO for LoginResponseDTO */
@Getter
@Setter
@NoArgsConstructor
public class EmailsDto {
  private String address;
  private boolean verified;
}

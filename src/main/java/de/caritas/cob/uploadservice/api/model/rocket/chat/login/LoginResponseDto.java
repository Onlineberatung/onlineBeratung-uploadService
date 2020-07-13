package de.caritas.cob.uploadservice.api.model.rocket.chat.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body object for Rocket.Chat API Call login
 * https://rocket.chat/docs/developer-guides/rest-api/authentication/login
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
  private String status;
  private DataDto data;
}

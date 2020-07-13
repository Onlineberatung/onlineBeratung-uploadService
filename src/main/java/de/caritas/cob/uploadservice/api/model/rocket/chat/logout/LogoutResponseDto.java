package de.caritas.cob.uploadservice.api.model.rocket.chat.logout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body object for Rocket.Chat API Call logout
 * https://rocket.chat/docs/developer-guides/rest-api/authentication/logout/
 *
 * <p>Please note: On error the LogoutResponseDTO.message property is set. On success the
 * LogoutResponseDTO.DataDTO.message property is set.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponseDto {

  private String status;
  // This property is set on error
  private String message;
  // This property is set on success
  private DataDto data;
}

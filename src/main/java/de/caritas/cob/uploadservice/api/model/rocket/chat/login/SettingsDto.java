package de.caritas.cob.uploadservice.api.model.rocket.chat.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** SettingsDTO for LoginResponseDTO */
@Getter
@Setter
@NoArgsConstructor
public class SettingsDto {
  private PreferencesDto preferences;
}

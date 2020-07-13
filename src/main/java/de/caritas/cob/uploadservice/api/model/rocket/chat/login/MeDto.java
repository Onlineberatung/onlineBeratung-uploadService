package de.caritas.cob.uploadservice.api.model.rocket.chat.login;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** MeDTO for LoginResponseDTO */
@Getter
@Setter
@NoArgsConstructor
public class MeDto {
  private String username;
  private String _id;
  private String status;
  private SettingsDto settings;
  private List<String> roles;
  private String name;
  private String active;
  private String utcOffset;
  private List<EmailsDto> emails;
  private String statusConnection;
}

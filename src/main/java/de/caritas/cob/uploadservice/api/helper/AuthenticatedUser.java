package de.caritas.cob.uploadservice.api.helper;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Representation of the via Keyclcoak authentificated user
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticatedUser {

  @NonNull
  private String userId;

  @NonNull
  private String username;

  private Set<String> roles;

  @NonNull
  private String accessToken;

  private Set<String> grantedAuthorities;
}

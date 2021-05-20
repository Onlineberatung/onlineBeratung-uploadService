package de.caritas.cob.uploadservice.api.authorization;

import static de.caritas.cob.uploadservice.api.authorization.Authorities.Authority.ANONYMOUS_DEFAULT;
import static de.caritas.cob.uploadservice.api.authorization.Authorities.Authority.CONSULTANT_DEFAULT;
import static de.caritas.cob.uploadservice.api.authorization.Authorities.Authority.TECHNICAL_DEFAULT;
import static de.caritas.cob.uploadservice.api.authorization.Authorities.Authority.USER_DEFAULT;
import static de.caritas.cob.uploadservice.api.authorization.Authorities.Authority.USE_FEEDBACK;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Definition of all authorities and of the role-authority-mapping
 */
@Getter
@AllArgsConstructor
public enum Authorities {

  USER(UserRole.USER, singletonList(USER_DEFAULT)),
  CONSULTANT(UserRole.CONSULTANT, singletonList(CONSULTANT_DEFAULT)),
  U25_CONSULTANT(UserRole.U25_CONSULTANT, singletonList(USE_FEEDBACK)),
  TECHNICAL(UserRole.TECHNICAL, singletonList(TECHNICAL_DEFAULT)),
  ANONYMOUS(UserRole.ANONYMOUS, singletonList(ANONYMOUS_DEFAULT));

  private final UserRole userRole;
  private final List<String> grantedAuthorities;

  /**
   * Get all authorities for a specific role.
   *
   * @param userRole the user role
   * @return the related authorities
   */
  public static List<String> getAuthoritiesByUserRole(UserRole userRole) {
    Optional<Authorities> authorityByUserRole = Stream.of(values())
        .filter(authority -> authority.userRole.equals(userRole))
        .findFirst();

    return authorityByUserRole.isPresent() ? authorityByUserRole.get().getGrantedAuthorities()
        : emptyList();
  }

  public static final class Authority {

    private Authority() {
    }

    public static final String PREFIX = "AUTHORIZATION_";

    public static final String CONSULTANT_DEFAULT = PREFIX + "CONSULTANT_DEFAULT";
    public static final String USER_DEFAULT = PREFIX + "USER_DEFAULT";
    public static final String USE_FEEDBACK = PREFIX + "USE_FEEDBACK";
    public static final String TECHNICAL_DEFAULT = PREFIX + "TECHNICAL_DEFAULT";
    public static final String ANONYMOUS_DEFAULT = PREFIX + "ANONYMOUS_DEFAULT";

  }

}

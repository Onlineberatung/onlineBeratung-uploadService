package de.caritas.cob.uploadservice.api.authorization;

import static de.caritas.cob.uploadservice.api.authorization.Authority.AuthorityValue.ANONYMOUS_DEFAULT;
import static de.caritas.cob.uploadservice.api.authorization.Authority.AuthorityValue.CONSULTANT_DEFAULT;
import static de.caritas.cob.uploadservice.api.authorization.Authority.AuthorityValue.TECHNICAL_DEFAULT;
import static de.caritas.cob.uploadservice.api.authorization.Authority.AuthorityValue.USER_DEFAULT;
import static de.caritas.cob.uploadservice.api.authorization.Authority.AuthorityValue.USE_FEEDBACK;
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
public enum Authority {

  USER(UserRole.USER, singletonList(USER_DEFAULT)),
  CONSULTANT(UserRole.CONSULTANT, singletonList(CONSULTANT_DEFAULT)),
  U25_CONSULTANT(UserRole.PEER_CONSULTANT, singletonList(USE_FEEDBACK)),
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
    Optional<Authority> authorityByUserRole = Stream.of(values())
        .filter(authority -> authority.userRole.equals(userRole))
        .findFirst();

    return authorityByUserRole.isPresent() ? authorityByUserRole.get().getGrantedAuthorities()
        : emptyList();
  }

  public static final class AuthorityValue {

    private AuthorityValue() {
    }

    public static final String PREFIX = "AUTHORIZATION_";

    public static final String CONSULTANT_DEFAULT = PREFIX + "CONSULTANT_DEFAULT";
    public static final String USER_DEFAULT = PREFIX + "USER_DEFAULT";
    public static final String USE_FEEDBACK = PREFIX + "USE_FEEDBACK";
    public static final String TECHNICAL_DEFAULT = PREFIX + "TECHNICAL_DEFAULT";
    public static final String ANONYMOUS_DEFAULT = PREFIX + "ANONYMOUS_DEFAULT";

  }

}

package de.caritas.cob.uploadservice.api.helper;

import de.caritas.cob.uploadservice.api.authorization.UserRole;
import java.util.Arrays;
import java.util.Set;

/** Helper methodes for {@link AuthenticatedUser}. */
public class AuthenticatedUserHelper {

  private AuthenticatedUserHelper() {}

  /**
   * Check, if {@link AuthenticatedUser} is consultant.
   *
   * @param authenticatedUser the {@link AuthenticatedUser} instance
   * @return true, if {@link AuthenticatedUser} is consultant.
   */
  public static boolean isConsultant(AuthenticatedUser authenticatedUser) {
    Set<String> roles = authenticatedUser.getRoles();
    return userRolesContainAnyRoleOf(roles, UserRole.CONSULTANT.getValue());
  }

  private static boolean userRolesContainAnyRoleOf(Set<String> userRoles, String... roles) {
    return Arrays.stream(roles).anyMatch(userRoles::contains);
  }
}

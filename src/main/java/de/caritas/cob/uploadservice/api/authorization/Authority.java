package de.caritas.cob.uploadservice.api.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Definition of all authorities and of the role-authority-mapping */
public class Authority {

  public static final String PREFIX = "AUTHORIZATION_";

  public static final String CONSULTANT_DEFAULT = PREFIX + "CONSULTANT_DEFAULT";
  public static final String USER_DEFAULT = PREFIX + "USER_DEFAULT";
  public static final String USE_FEEDBACK = PREFIX + "USE_FEEDBACK";
  public static final String TECHNICAL_DEFAULT = PREFIX + "TECHNICAL_DEFAULT";

  private static final Map<UserRole, List<String>> authorizationRoleMapping =
      new HashMap<UserRole, List<String>>() {

        private static final long serialVersionUID = -4293306706967206011L;

        {
          put(UserRole.USER, Arrays.asList(USER_DEFAULT));
          put(UserRole.CONSULTANT, Arrays.asList(CONSULTANT_DEFAULT));
          put(UserRole.U25_CONSULTANT, Arrays.asList(USE_FEEDBACK));
          put(UserRole.TECHNICAL, Arrays.asList(TECHNICAL_DEFAULT));
        }
      };

  /**
   * Get all authorities for a specific role
   *
   * @param userRole
   * @return
   */
  public static List<String> getAuthoritiesByUserRole(UserRole userRole) {
    if (authorizationRoleMapping.containsKey(userRole)) {
      return authorizationRoleMapping.get(userRole);
    } else {
      return new ArrayList<String>();
    }
  }
}

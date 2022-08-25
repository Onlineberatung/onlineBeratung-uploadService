package de.caritas.cob.uploadservice.api.tenant;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.stereotype.Component;

@Component
public class TechnicalUserTenantResolver implements TenantResolver {

  @Override
  public Optional<Long> resolve(HttpServletRequest request) {
    return isTechnicalUserRole(request) ? Optional.of(0L) : Optional.empty();
  }

  private boolean isTechnicalUserRole(HttpServletRequest request) {
    AccessToken token = ((KeycloakAuthenticationToken) request.getUserPrincipal()).getAccount()
        .getKeycloakSecurityContext().getToken();
    return hasRoles(token) && token.getRealmAccess().getRoles().contains("technical");
  }

  private boolean hasRoles(AccessToken accessToken) {
    return accessToken.getRealmAccess() != null && accessToken.getRealmAccess().getRoles() != null;
  }

  @Override
  public boolean canResolve(HttpServletRequest request) {
    return resolve(request).isPresent();
  }
}

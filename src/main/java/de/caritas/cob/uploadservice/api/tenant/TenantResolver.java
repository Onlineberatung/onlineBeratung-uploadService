package de.caritas.cob.uploadservice.api.tenant;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.uploadservice.api.service.TenantService;
import de.caritas.cob.uploadservice.filter.SubdomainExtractor;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnExpression("${multitenancy.enabled:true}")
@Component
public class TenantResolver {

  public static final Long TECHNICAL_TENANT_ID = 0L;
  private static final String TENANT_ID = "tenantId";
  private final @NonNull SubdomainExtractor subdomainExtractor;
  private final @NonNull TenantService tenantService;
  private final @NonNull TenantHeaderSupplier tenantHeaderSupplier;

  public Long resolve(HttpServletRequest request) {
    if (userIsAuthenticated(request)) {
      return resolveForAuthenticatedUser(request);
    } else {
      return resolveForNonAuthenticatedUser();
    }
  }

  private Long resolveForNonAuthenticatedUser() {
    Optional<Long> tenantId = resolveTenantFromHttpRequest();
    if (tenantId.isEmpty()) {
      throw new AccessDeniedException("Tenant id could not be resolved");
    }
    return tenantId.get();
  }

  private Long resolveForAuthenticatedUser(HttpServletRequest request) {
    return isTechnicalUserRole(request) ? TECHNICAL_TENANT_ID
        : resolveForAuthenticatedNonTechnicalUser(request);
  }

  private Long resolveForAuthenticatedNonTechnicalUser(HttpServletRequest request) {
    Optional<Long> tenantId = resolveTenantIdFromTokenClaims(request);
    Optional<Long> tenantIdFromSubdomain = resolveTenantFromHttpRequest();
    if (tenantId.isPresent() && tenantIdFromSubdomain.isPresent()) {
      if (tenantId.get().equals(tenantIdFromSubdomain.get())) {
        return tenantId.get();
      }
      throw new AccessDeniedException("Tenant id from claim and subdomain not same.");
    }
    throw new AccessDeniedException("Tenant id could not be resolved");
  }

  private Optional<Long> resolveTenantFromHttpRequest() {

    Optional<Long> tenantFromHeader = tenantHeaderSupplier.getTenantFromHeader();
    if (tenantFromHeader.isPresent()) {
      return tenantFromHeader;
    }

    Optional<String> currentSubdomain = subdomainExtractor.getCurrentSubdomain();
    if (currentSubdomain.isPresent()) {
      return of(getTenantIdBySubdomain(currentSubdomain.get()));
    } else {
      return empty();
    }
  }

  private Long getTenantIdBySubdomain(String currentSubdomain) {
    return tenantService.getRestrictedTenantDataBySubdomain(currentSubdomain).getId();
  }

  private Optional<Long> getUserTenantIdAttribute(Map<String, Object> claimMap) {
    if (claimMap.containsKey(TENANT_ID)) {
      Integer tenantId = (Integer) claimMap.get(TENANT_ID);
      return Optional.of(Long.valueOf(tenantId));
    } else {
      return Optional.empty();
    }
  }

  private Optional<Long> resolveTenantIdFromTokenClaims(HttpServletRequest request) {
    Map<String, Object> claimMap = getClaimMap(request);
    log.debug("Found tenantId in claim : " + claimMap.toString());
    return getUserTenantIdAttribute(claimMap);
  }

  private boolean isTechnicalUserRole(HttpServletRequest request) {
    AccessToken token = ((KeycloakAuthenticationToken) request.getUserPrincipal()).getAccount()
        .getKeycloakSecurityContext().getToken();
    return hasRoles(token) && token.getRealmAccess().getRoles().contains("technical");
  }

  private boolean hasRoles(AccessToken accessToken) {
    return accessToken.getRealmAccess() != null && accessToken.getRealmAccess().getRoles() != null;
  }

  private boolean userIsAuthenticated(HttpServletRequest request) {
    return request.getUserPrincipal() != null;
  }

  private Map<String, Object> getClaimMap(HttpServletRequest request) {
    KeycloakSecurityContext keycloakSecContext =
        ((KeycloakAuthenticationToken) request.getUserPrincipal()).getAccount()
            .getKeycloakSecurityContext();
    return keycloakSecContext.getToken().getOtherClaims();
  }

}

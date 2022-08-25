package de.caritas.cob.uploadservice.api.tenant;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnExpression("${multitenancy.enabled:true}")
@Component
public class TenantResolverService {

  @NonNull
  CustomHeaderTenantResolver customHeaderTenantResolver;

  @NonNull
  SubdomainTenantResolver subdomainTenantResolver;

  @NonNull
  TechnicalUserTenantResolver technicalUserTenantResolver;

  @NonNull
  AccessTokenTenantResolver accessTokenTenantResolver;

  @Value("${feature.multitenancy.with.single.domain.enabled}")
  private boolean multitenancyWithSingleDomain;

  private List<TenantResolver> nonAuthenticatedTenantResolvers() {
    return newArrayList(customHeaderTenantResolver, subdomainTenantResolver);
  }

  private List<TenantResolver> tenantIdCrossValidationResolvers() {
    return newArrayList(customHeaderTenantResolver, subdomainTenantResolver);
  }

  private ArrayList<TenantResolver> authenticatedTenantResolvers() {
    return newArrayList(technicalUserTenantResolver, accessTokenTenantResolver);
  }

  public Long resolve(HttpServletRequest request) {
    if (userIsAuthenticated(request)) {
      return resolveForAuthenticatedUser(request);
    } else {
      return resolveForNonAuthenticatedUser(request);
    }
  }

  private Long resolveForAuthenticatedUser(HttpServletRequest request) {
    var tenantId = getFirstResolvedTenant(request, authenticatedTenantResolvers());
    if (multitenancyWithSingleDomain) {
      return tenantId.orElseThrow();
    } else {
      if (shouldValidateResolvedTenant(tenantId)) {
        Optional<Long> tenantIdFromCustomHeaderOrSubdomain = getFirstResolvedTenant(request,
            tenantIdCrossValidationResolvers());
        validateResolvedTenantMatch(tenantId, tenantIdFromCustomHeaderOrSubdomain);
      }
      return tenantId.orElseThrow();
    }
  }

  private boolean shouldValidateResolvedTenant(Optional<Long> tenantId) {
    return !isTechnicalTenant(tenantId) && !multitenancyWithSingleDomain;
  }

  private boolean isTechnicalTenant(Optional<Long> tenantId) {
    return tenantId.isPresent() && tenantId.get().equals(0L);
  }

  private Long resolveForNonAuthenticatedUser(HttpServletRequest request) {
    var tenantId = getFirstResolvedTenant(request, nonAuthenticatedTenantResolvers());
    if (tenantId.isEmpty()) {
      throw new AccessDeniedException("Tenant id could not be resolved");
    }
    return tenantId.get();
  }

  private  void validateResolvedTenantMatch(Optional<Long> tenantId,
      Optional<Long> tenantIdFromHeaderOrSubdomain) {
    if (tenantId.isPresent() && tenantIdFromHeaderOrSubdomain.isPresent()) {
      if (!tenantId.get().equals(tenantIdFromHeaderOrSubdomain.get())) {
        throw new AccessDeniedException("Tenant id from claim and subdomain not same.");
      }
    } else {
      throw new AccessDeniedException("Tenant id could not be resolved");
    }
  }

  private Optional<Long> getFirstResolvedTenant(HttpServletRequest request,
      List<TenantResolver> tenantResolvers) {
    for (TenantResolver tenantResolver : tenantResolvers) {
      if (tenantResolver.canResolve(request)) {
        return tenantResolver.resolve(request);
      }
    }
    return Optional.empty();
  }

  private boolean userIsAuthenticated(HttpServletRequest request) {
    return request.getUserPrincipal() != null;
  }
}

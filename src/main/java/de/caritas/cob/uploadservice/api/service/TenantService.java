package de.caritas.cob.uploadservice.api.service;

import de.caritas.cob.uploadservice.config.CacheManagerConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantService {

  private final @NonNull de.caritas.cob.uploadservice.tenantservice.generated.web.TenantControllerApi tenantControllerApi;

  @Cacheable(cacheNames = CacheManagerConfig.TENANT_CACHE, key = "#subdomain")
  public de.caritas.cob.uploadservice.tenantservice.generated.web.model.RestrictedTenantDTO getRestrictedTenantDataBySubdomain(String subdomain) {
    return tenantControllerApi.getRestrictedTenantDataBySubdomainWithHttpInfo(subdomain).getBody();
  }
}

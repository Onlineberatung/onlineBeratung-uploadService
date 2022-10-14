package de.caritas.cob.uploadservice.api.tenant;

import java.util.Optional;

public class TenantContext {

  private TenantContext() {

  }

  private static ThreadLocal<Long> currentTenant = new ThreadLocal<>();

  public static Long getCurrentTenant() {
    return currentTenant.get();
  }

  public static Optional<Long> getCurrentTenantOption() {
    return Optional.ofNullable(getCurrentTenant());
  }

  public static void setCurrentTenant(Long tenant) {
    currentTenant.set(tenant);
  }

  public static void clear() {
    currentTenant.remove();
  }
}

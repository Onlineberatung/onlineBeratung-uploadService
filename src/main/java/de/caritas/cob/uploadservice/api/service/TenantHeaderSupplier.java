package de.caritas.cob.uploadservice.api.service;

import de.caritas.cob.uploadservice.api.tenant.TenantContext;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantHeaderSupplier {

  @Value("${multitenancy.enabled}")
  private boolean multitenancy;

  public void addTenantHeader(HttpHeaders headers, Optional<Long> tenantId) {
    if (multitenancy) {
      var tenantIdNumber = tenantId.orElseGet(TenantContext::getCurrentTenant);
      headers.add("tenantId", tenantIdNumber.toString());
    }
  }

  public Optional<Long> getTenantFromHeader(HttpServletRequest request) {
    try {
      return Optional.of(Long.parseLong(request.getHeader("tenantId")));
    } catch (NumberFormatException exception) {
      log.debug("No tenantId provided via headers.");
      return Optional.empty();
    }
  }
}

package de.caritas.cob.uploadservice.api.tenant;


import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomHeaderTenantResolver implements TenantResolver {
  private final @NonNull TenantHeaderSupplier tenantHeaderSupplier;

  @Override
  public Optional<Long> resolve(HttpServletRequest request) {
    return tenantHeaderSupplier.getTenantFromHeader(request);
  }

  @Override
  public boolean canResolve(HttpServletRequest request) {
    return resolve(request).isPresent();
  }
}

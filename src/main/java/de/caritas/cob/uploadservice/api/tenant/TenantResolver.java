package de.caritas.cob.uploadservice.api.tenant;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public interface TenantResolver {

  Optional<Long> resolve(HttpServletRequest request);

  boolean canResolve(HttpServletRequest request);
}

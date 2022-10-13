package de.caritas.cob.uploadservice.api.service.helper;

import static java.util.Objects.isNull;

import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ServiceHelper {

  @Value("${csrf.header.property}")
  private String csrfHeaderProperty;

  @Value("${csrf.cookie.property}")
  private String csrfCookieProperty;

  private final @NonNull AuthenticatedUser authenticatedUser;
  private final @NonNull TenantHeaderSupplier tenantHeaderSupplier;

  /**
   * Returns {@link HttpHeaders} with CSRF cookie and CSRF header and the provided Keycloak Bearer
   * token.
   *
   * @param accessToken Keycloak Bearer token
   * @param tenantId optional tenant ID
   * @return {@link HttpHeaders}
   */
  public HttpHeaders getKeycloakAndCsrfHttpHeaders(String accessToken, Optional<Long> tenantId) {
    var headers = new HttpHeaders();
    addCsrfValues(headers);
    tenantHeaderSupplier.addTenantHeader(headers, tenantId);
    addAuthorizationHeader(headers, accessToken);

    return headers;
  }

  private void addAuthorizationHeader(HttpHeaders headers, String accessToken) {
    var token = isNull(accessToken) ? authenticatedUser.getAccessToken() : accessToken;
    headers.add("Authorization", "Bearer " + token);
  }

  private HttpHeaders addCsrfValues(HttpHeaders httpHeaders) {
    String csrfToken = UUID.randomUUID().toString();

    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    httpHeaders.add("Cookie", csrfCookieProperty + "=" + csrfToken);
    httpHeaders.add(csrfHeaderProperty, csrfToken);

    return httpHeaders;
  }
}

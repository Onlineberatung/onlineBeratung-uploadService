package de.caritas.cob.uploadservice.api.service.helper;

import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
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

  /**
   * Returns {@link HttpHeaders} with CSRF cookie and CSRF header and the provided Keycloak Bearer
   * token.
   *
   * @param accessToken Keycloak Bearer token
   * @return {@link HttpHeaders}
   */
  public HttpHeaders getKeycloakAndCsrfHttpHeaders(String accessToken) {
    return createKeycloakAuthHeader(accessToken);
  }

  /**
   * Returns {@link HttpHeaders} with CSRF cookie and CSRF header and the Keycloak Bearer token of
   * the currently authenticated user.
   *
   * @return {@link HttpHeaders}
   */
  public HttpHeaders getKeycloakAndCsrfHttpHeaders() {
    return createKeycloakAuthHeader(authenticatedUser.getAccessToken());
  }

  private HttpHeaders createKeycloakAuthHeader(String accessToken) {
    HttpHeaders header = new HttpHeaders();
    header = this.addCsrfValues(header);
    header.add("Authorization", "Bearer " + accessToken);

    return header;
  }

  private HttpHeaders addCsrfValues(HttpHeaders httpHeaders) {
    String csrfToken = UUID.randomUUID().toString();

    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    httpHeaders.add("Cookie", csrfCookieProperty + "=" + csrfToken);
    httpHeaders.add(csrfHeaderProperty, csrfToken);

    return httpHeaders;
  }
}

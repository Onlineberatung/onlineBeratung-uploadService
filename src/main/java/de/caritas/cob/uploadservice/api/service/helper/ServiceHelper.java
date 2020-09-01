package de.caritas.cob.uploadservice.api.service.helper;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ServiceHelper {

  @Value("${csrf.header.property}")
  private String csrfHeaderProperty;

  @Value("${csrf.cookie.property}")
  private String csrfCookieProperty;

  /**
   * Adds the Rocket.Chat user id, token and group id to the given {@link HttpHeaders} object.
   *
   * @return HttpHeaders
   */
  public HttpHeaders getKeycloakAndCsrfHttpHeaders(String accessToken) {
    HttpHeaders header = new HttpHeaders();
    header = this.addCsrfValues(header);

    header.add("Authorization", "Bearer " + accessToken);

    return header;
  }

  /**
   * Adds CSRF cookie and header value to the given {@link HttpHeaders} object.
   *
   * @param httpHeaders the header to add csrf values
   * @return HttpHeaders
   */
  private HttpHeaders addCsrfValues(HttpHeaders httpHeaders) {
    String csrfToken = UUID.randomUUID().toString();

    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    httpHeaders.add("Cookie", csrfCookieProperty + "=" + csrfToken);
    httpHeaders.add(csrfHeaderProperty, csrfToken);

    return httpHeaders;
  }
}

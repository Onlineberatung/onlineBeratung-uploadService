package de.caritas.cob.uploadservice.api.exception.cutomheader;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;

/**
 * Constants to build custom http headers.
 */
@AllArgsConstructor
public enum CustomHttpHeader {

  QUOTA_REACHED("X-Reason", "QUOTA_REACHED");

  private final String headerName;
  private final String headerValue;

  /**
   * Builds the {@link HttpHeaders} for the custom values.
   *
   * @return the {@link HttpHeaders} instance
   */
  public HttpHeaders buildHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(headerName, headerValue);
    return headers;
  }

}

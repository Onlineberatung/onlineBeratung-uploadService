package de.caritas.cob.uploadservice.api.exception.cutomheader;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;

@AllArgsConstructor
public enum CustomHttpHeader {

  QUOTA_REACHED("X-Reason", "QUOTA_REACHED");

  private String headerName;
  private String headerValue;

  public HttpHeaders buildHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(headerName, headerValue);
    return headers;
  }

}

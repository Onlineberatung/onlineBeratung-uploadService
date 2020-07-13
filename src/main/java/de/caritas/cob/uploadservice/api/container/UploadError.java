package de.caritas.cob.uploadservice.api.container;

import lombok.Builder;
import lombok.Getter;

/** Parameter object for Rocket.Chat upload error. */
@Getter
@Builder
public class UploadError {

  private String rcError;
  private String rcErrorType;
  private String errorMessage;
}

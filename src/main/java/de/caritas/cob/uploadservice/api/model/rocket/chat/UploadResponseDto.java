package de.caritas.cob.uploadservice.api.model.rocket.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response object for Rocket.Chat API upload calls which contain a boolean success and error
 * messages.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponseDto {

  private boolean success;
  private String error;
  private String errorType;
}

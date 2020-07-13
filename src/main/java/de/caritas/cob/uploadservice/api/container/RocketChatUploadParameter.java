package de.caritas.cob.uploadservice.api.container;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

/** Parameter object for Rocket.Chat upload parameter. */
@Getter
@Builder
public class RocketChatUploadParameter {

  private String roomId;
  private MultipartFile file;
  private String message;
  private String description;
  private String tmId;
}

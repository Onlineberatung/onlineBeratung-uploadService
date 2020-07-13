package de.caritas.cob.uploadservice.api.helper;

import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import org.springframework.stereotype.Component;

/** Sanitizer for {@link RocketChatUploadParameter} */
@Component
public class RocketChatUploadParameterSanitizer {

  /**
   * Remove Html from message, description, roomId and tmid
   *
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   * @return a {@link RocketChatUploadParameter} instance with sanitized parameters
   */
  public RocketChatUploadParameter sanitize(RocketChatUploadParameter rocketChatUploadParameter) {

    return RocketChatUploadParameter.builder()
        .message(Helper.removeHtmFromText(rocketChatUploadParameter.getMessage()))
        .description(Helper.removeHtmFromText(rocketChatUploadParameter.getDescription()))
        .roomId(Helper.removeHtmFromText(rocketChatUploadParameter.getRoomId()))
        .file(rocketChatUploadParameter.getFile())
        .tmId(Helper.removeHtmFromText(rocketChatUploadParameter.getTmId()))
        .build();
  }
}

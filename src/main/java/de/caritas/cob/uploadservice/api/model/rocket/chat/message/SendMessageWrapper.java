package de.caritas.cob.uploadservice.api.model.rocket.chat.message;

import de.caritas.cob.uploadservice.rocketchat.generated.web.model.FullUploadResponseDtoMessage;
import lombok.Data;

@Data
public class SendMessageWrapper {

  private final FullUploadResponseDtoMessage message;
}

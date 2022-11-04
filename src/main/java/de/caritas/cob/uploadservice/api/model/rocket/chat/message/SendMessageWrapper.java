package de.caritas.cob.uploadservice.api.model.rocket.chat.message;

import de.caritas.cob.uploadservice.rocketchat.generated.web.model.SendMessageDto;
import lombok.Data;

@Data
public class SendMessageWrapper {

  private final SendMessageDto message;
}

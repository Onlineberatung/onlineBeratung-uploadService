package de.caritas.cob.uploadservice.api.helper;

import static de.caritas.cob.uploadservice.helper.TestConstants.RC_DESCRIPTION;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_MESSAGE;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_TMID;
import static de.caritas.cob.uploadservice.helper.TestConstants.UPLOAD_FILE;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.service.EncryptionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RocketChatUploadParameterEncrypterTest {

  @Mock EncryptionService encryptionService;
  @InjectMocks RocketChatUploadParameterEncrypter rocketChatUploadParameterEncrypter;

  @Test
  public void encrypt_Should_EncryptMessageAndDescription() {

    RocketChatUploadParameter rocketChatUploadParameter =
        RocketChatUploadParameter.builder()
            .message(RC_MESSAGE)
            .description(RC_DESCRIPTION)
            .roomId(RC_ROOM_ID)
            .file(UPLOAD_FILE)
            .tmId(RC_TMID)
            .build();

    RocketChatUploadParameter result =
        rocketChatUploadParameterEncrypter.encrypt(rocketChatUploadParameter);

    verify(encryptionService, times(1)).encrypt(RC_MESSAGE, RC_ROOM_ID);
    verify(encryptionService, times(1)).encrypt(RC_DESCRIPTION, RC_ROOM_ID);
  }
}

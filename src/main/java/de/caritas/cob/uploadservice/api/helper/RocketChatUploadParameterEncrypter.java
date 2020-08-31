package de.caritas.cob.uploadservice.api.helper;

import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.exception.CustomCryptoException;
import de.caritas.cob.uploadservice.api.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * /** Encrypter for {@link de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter}
 */
@Component
public class RocketChatUploadParameterEncrypter {

  private final EncryptionService encryptionService;

  @Autowired
  public RocketChatUploadParameterEncrypter(EncryptionService encryptionService) {
    this.encryptionService = encryptionService;
  }

  /**
   * Encrypt message and description
   *
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   * @return a {@link RocketChatUploadParameter} instance with encrypted message and description
   */
  public RocketChatUploadParameter encrypt(RocketChatUploadParameter rocketChatUploadParameter)
      throws CustomCryptoException {

    String encryptedMessage =
        (rocketChatUploadParameter.getMessage() != null)
            ? encryptionService.encrypt(
                rocketChatUploadParameter.getMessage(), rocketChatUploadParameter.getRoomId())
            : null;
    String encryptedDescription =
        (rocketChatUploadParameter.getDescription() != null)
            ? encryptionService.encrypt(
                rocketChatUploadParameter.getDescription(), rocketChatUploadParameter.getRoomId())
            : null;

    return RocketChatUploadParameter.builder()
        .message(encryptedMessage)
        .description(encryptedDescription)
        .roomId(rocketChatUploadParameter.getRoomId())
        .file(rocketChatUploadParameter.getFile())
        .tmId(rocketChatUploadParameter.getTmId())
        .build();
  }
}

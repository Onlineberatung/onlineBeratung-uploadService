package de.caritas.cob.uploadservice.api.facade;

import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.exception.CustomCryptoException;
import de.caritas.cob.uploadservice.api.exception.RocketChatPostMarkGroupAsReadException;
import de.caritas.cob.uploadservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.uploadservice.api.helper.RocketChatUploadParameterEncrypter;
import de.caritas.cob.uploadservice.api.helper.RocketChatUploadParameterSanitizer;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.RocketChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * Facade to encapsulate the steps for uploading a file with an encrypted message
 */
@Component
public class UploadFacade {

  private final RocketChatService rocketChatService;
  private final EmailNotificationFacade emailNotificationFacade;
  private final RocketChatUploadParameterSanitizer rocketChatUploadParameterSanitizer;
  private final RocketChatUploadParameterEncrypter rocketChatUploadParameterEncrypter;

  @Autowired
  public UploadFacade(
      RocketChatService rocketChatService,
      EmailNotificationFacade emailNotificationFacade,
      RocketChatUploadParameterSanitizer rocketChatUploadParameterSanitizer,
      RocketChatUploadParameterEncrypter rocketChatUploadParameterEncrypter) {
    this.rocketChatService = rocketChatService;
    this.emailNotificationFacade = emailNotificationFacade;
    this.rocketChatUploadParameterSanitizer = rocketChatUploadParameterSanitizer;
    this.rocketChatUploadParameterEncrypter = rocketChatUploadParameterEncrypter;
  }

  /**
   * Upload a file with a message to a Rocket.Chat room. The message and the description are
   * encrypted before it is sent to Rocket.Chat.
   *
   * @param rocketChatCredentials {@link RocketChatCredentials} container
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   */
  public void uploadFileToRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter,
      boolean sendNotification) {

    sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
        rocketChatCredentials, rocketChatUploadParameter);
    if (sendNotification) {
      emailNotificationFacade.sendEmailNotification(rocketChatUploadParameter.getRoomId());
    }
  }

  /**
   * Upload a file with a message to a Rocket.Chat feedback room. The message and the description
   * are encrypted before it is sent to Rocket.Chat.
   *
   * @param rocketChatCredentials {@link RocketChatCredentials} container
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   */
  public void uploadFileToFeedbackRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter,
      boolean sendNotification) {

    sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
        rocketChatCredentials, rocketChatUploadParameter);
    if (sendNotification) {
      emailNotificationFacade.sendFeedbackEmailNotification(rocketChatUploadParameter.getRoomId());
    }
  }

  private void sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter) {

    rocketChatUploadParameterSanitizer.sanitize(rocketChatUploadParameter);
    RocketChatUploadParameter encryptedRocketChatUploadParameter;
    try {
      encryptedRocketChatUploadParameter = rocketChatUploadParameterEncrypter
          .encrypt(rocketChatUploadParameter);
    } catch (CustomCryptoException e) {
      throw new InternalServerErrorException(e, LogService::logEncryptionServiceError);
    }

    rocketChatService.roomsUpload(rocketChatCredentials, encryptedRocketChatUploadParameter);
    try {
      rocketChatService.markGroupAsReadForSystemUser(rocketChatUploadParameter.getRoomId());
    } catch (RocketChatPostMarkGroupAsReadException e) {
      throw new InternalServerErrorException(e, LogService::logRocketChatServiceError);
    }
  }
}

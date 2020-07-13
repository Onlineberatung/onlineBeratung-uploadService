package de.caritas.cob.uploadservice.api.facade;

import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.helper.RocketChatUploadParameterEncrypter;
import de.caritas.cob.uploadservice.api.helper.RocketChatUploadParameterSanitizer;
import de.caritas.cob.uploadservice.api.service.RocketChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
   * @return true, if successful
   */
  public HttpStatus uploadFileToRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter,
      boolean sendNotification) {

    sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
        rocketChatCredentials, rocketChatUploadParameter);
    if (sendNotification) {
      emailNotificationFacade.sendEmailNotification(rocketChatUploadParameter.getRoomId());
    }
    return HttpStatus.CREATED;
  }

  /**
   * Upload a file with a message to a Rocket.Chat feedback room. The message and the description
   * are encrypted before it is sent to Rocket.Chat.
   *
   * @param rocketChatCredentials {@link RocketChatCredentials} container
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   * @return true, if successful
   */
  public HttpStatus uploadFileToFeedbackRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter,
      boolean sendNotification) {

    sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
        rocketChatCredentials, rocketChatUploadParameter);
    if (sendNotification) {
      emailNotificationFacade.sendFeedbackEmailNotification(rocketChatUploadParameter.getRoomId());
    }
    return HttpStatus.CREATED;
  }

  /**
   * Cleanup user input and upload to Rocket.Chat
   *
   * @param rocketChatCredentials {@link RocketChatCredentials} container
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   */
  public void sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter) {

    RocketChatUploadParameter cleanedRocketChatUploadParameter =
        rocketChatUploadParameterSanitizer.sanitize(rocketChatUploadParameter);
    RocketChatUploadParameter encryptedRocketChatUploadParameter =
        rocketChatUploadParameterEncrypter.encrypt(rocketChatUploadParameter);

    rocketChatService.roomsUpload(rocketChatCredentials, encryptedRocketChatUploadParameter);
    rocketChatService.markGroupAsReadForSystemUser(rocketChatUploadParameter.getRoomId());
  }
}

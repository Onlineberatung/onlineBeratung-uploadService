package de.caritas.cob.uploadservice.api.facade;

import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.exception.CustomCryptoException;
import de.caritas.cob.uploadservice.api.exception.RocketChatPostMarkGroupAsReadException;
import de.caritas.cob.uploadservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.api.helper.AuthenticatedUserHelper;
import de.caritas.cob.uploadservice.api.helper.RocketChatUploadParameterEncrypter;
import de.caritas.cob.uploadservice.api.helper.RocketChatUploadParameterSanitizer;
import de.caritas.cob.uploadservice.api.service.LiveEventNotificationService;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.RocketChatService;
import de.caritas.cob.uploadservice.api.service.UploadTrackingService;
import de.caritas.cob.uploadservice.api.statistics.StatisticsService;
import de.caritas.cob.uploadservice.api.statistics.event.UploadFileStatisticsEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/*
 * Facade to encapsulate the steps for uploading a file with an encrypted message.
 */
@Component
@RequiredArgsConstructor
public class UploadFacade {

  private final @NonNull RocketChatService rocketChatService;
  private final @NonNull EmailNotificationFacade emailNotificationFacade;
  private final @NonNull RocketChatUploadParameterSanitizer rocketChatUploadParameterSanitizer;
  private final @NonNull RocketChatUploadParameterEncrypter rocketChatUploadParameterEncrypter;
  private final @NonNull LiveEventNotificationService liveEventNotificationService;
  private final @NonNull UploadTrackingService uploadTrackingService;
  private final @NonNull StatisticsService statisticsService;
  private final @NonNull AuthenticatedUser authenticatedUser;

  /**
   * Upload a file with a message to a Rocket.Chat room. The message and the description are
   * encrypted before it is sent to Rocket.Chat.
   *
   * If the statistics function is enabled, the assignment of the enquired is processed as
   * statistical event.
   *
   * @param rocketChatCredentials {@link RocketChatCredentials} container
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   */
  public void uploadFileToRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter,
      boolean sendNotification) {

    this.uploadTrackingService.validateUploadLimit(rocketChatUploadParameter.getRoomId());

    sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
        rocketChatCredentials, rocketChatUploadParameter);
    this.liveEventNotificationService.sendLiveEvent(rocketChatUploadParameter.getRoomId());
    this.uploadTrackingService.trackUploadedFileForUser(rocketChatUploadParameter.getRoomId());

    if (sendNotification) {
      emailNotificationFacade.sendEmailNotification(rocketChatUploadParameter.getRoomId());
    }

    if (AuthenticatedUserHelper.isConsultant(authenticatedUser)) {
      statisticsService.fireEvent(
          new UploadFileStatisticsEvent(authenticatedUser.getUserId(), rocketChatUploadParameter.getRoomId()));
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

    this.uploadTrackingService.validateUploadLimit(rocketChatUploadParameter.getRoomId());
    sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
        rocketChatCredentials, rocketChatUploadParameter);
    this.liveEventNotificationService.sendLiveEvent(rocketChatUploadParameter.getRoomId());
    this.uploadTrackingService.trackUploadedFileForUser(rocketChatUploadParameter.getRoomId());

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

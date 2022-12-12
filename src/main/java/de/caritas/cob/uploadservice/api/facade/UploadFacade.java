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
import de.caritas.cob.uploadservice.api.service.FileService;
import de.caritas.cob.uploadservice.api.service.LiveEventNotificationService;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.MongoDbService;
import de.caritas.cob.uploadservice.api.service.RocketChatService;
import de.caritas.cob.uploadservice.api.service.UploadTrackingService;
import de.caritas.cob.uploadservice.api.statistics.StatisticsService;
import de.caritas.cob.uploadservice.api.statistics.event.CreateMessageStatisticsEvent;
import de.caritas.cob.uploadservice.api.tenant.TenantContext;
import de.caritas.cob.uploadservice.rocketchat.generated.web.model.FullUploadResponseDto;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.UserRole;
import java.io.ByteArrayInputStream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/*
 * Facade to encapsulate the steps for uploading a file with an encrypted message.
 */
@Slf4j
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
  private final @NonNull FileService fileService;
  private final @NonNull MongoDbService mongoDbService;

  /**
   * Upload a file with a message to a Rocket.Chat room. The message and the description are
   * encrypted before it is sent to Rocket.Chat.
   *
   * <p>If the statistics function is enabled, the assignment of the enquired is processed as
   * statistical event.
   *
   * @param rocketChatCredentials     {@link RocketChatCredentials} container
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   */
  public void uploadFileToRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter,
      boolean sendNotification, String type, String fileHeader) {

    this.uploadTrackingService.validateUploadLimit(rocketChatUploadParameter.getRoomId());

    sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
        rocketChatCredentials, rocketChatUploadParameter, type, fileHeader);
    this.liveEventNotificationService.sendLiveEvent(rocketChatUploadParameter.getRoomId(),
        authenticatedUser.getAccessToken(), TenantContext.getCurrentTenantOption());
    this.uploadTrackingService.trackUploadedFileForUser(rocketChatUploadParameter.getRoomId());

    if (sendNotification) {
      emailNotificationFacade.sendEmailNotification(rocketChatUploadParameter.getRoomId());
    }

    statisticsService.fireEvent(
        new CreateMessageStatisticsEvent(
            authenticatedUser.getUserId(),
            resolveUserRole(authenticatedUser),
            rocketChatUploadParameter.getRoomId(),
            true));
  }

  private UserRole resolveUserRole(AuthenticatedUser authenticatedUser) {
    return (AuthenticatedUserHelper.isConsultant(authenticatedUser))
        ? UserRole.CONSULTANT
        : UserRole.ASKER;
  }

  /**
   * Upload a file with a message to a Rocket.Chat feedback room. The message and the description
   * are encrypted before it is sent to Rocket.Chat.
   *
   * @param rocketChatCredentials     {@link RocketChatCredentials} container
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   */
  public void uploadFileToFeedbackRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter,
      boolean sendNotification, String type, String fileHeader) {

    this.uploadTrackingService.validateUploadLimit(rocketChatUploadParameter.getRoomId());

    sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
        rocketChatCredentials, rocketChatUploadParameter, type, fileHeader);
    this.liveEventNotificationService.sendLiveEvent(rocketChatUploadParameter.getRoomId(),
        authenticatedUser.getAccessToken(), TenantContext.getCurrentTenantOption());
    this.uploadTrackingService.trackUploadedFileForUser(rocketChatUploadParameter.getRoomId());

    if (sendNotification) {
      emailNotificationFacade.sendFeedbackEmailNotification(rocketChatUploadParameter.getRoomId());
    }
  }

  private void sanitizeAndEncryptParametersAndUploadToRocketChatRoom(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter, String type, String fileHeader) {

    rocketChatUploadParameterSanitizer.sanitize(rocketChatUploadParameter);

    boolean doAttachmentE2e = StringUtils.hasText(type) && type.equals("e2e");

    if (doAttachmentE2e) {
      JSONArray parsed = new JSONArray(fileHeader);
      byte[] fileHeaderByteList = new byte[parsed.length()];
      for (int i = 0; i < parsed.length(); i++) {
        fileHeaderByteList[i] = parsed.getNumber(i).byteValue();
      }
      fileService.verifyFileHeaderMimeType(new ByteArrayInputStream(fileHeaderByteList));
    } else {
      fileService.verifyMimeType(rocketChatUploadParameter.getFile());
    }

    RocketChatUploadParameter encryptedRocketChatUploadParameter;
    try {
      encryptedRocketChatUploadParameter =
          rocketChatUploadParameterEncrypter.encrypt(rocketChatUploadParameter);
    } catch (CustomCryptoException e) {
      throw new InternalServerErrorException(e, LogService::logEncryptionServiceError);
    }

    FullUploadResponseDto uploadResponse = rocketChatService.roomsUpload(rocketChatCredentials,
        encryptedRocketChatUploadParameter);

    if (doAttachmentE2e) {
      if (uploadResponse.getMessage() == null || !StringUtils.hasText(uploadResponse.getMessage().getId())) {
        throw new InternalServerErrorException(
            new Exception("Upload response message payload or id was empty!"),
            LogService::logInternalServerError);
      }
      mongoDbService.setE2eType(uploadResponse.getMessage().getId());
    }

    try {
      rocketChatService.markGroupAsReadForSystemUser(rocketChatUploadParameter.getRoomId());
    } catch (RocketChatPostMarkGroupAsReadException e) {
      throw new InternalServerErrorException(e, LogService::logRocketChatServiceError);
    }
  }
}

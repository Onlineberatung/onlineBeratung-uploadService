package de.caritas.cob.uploadservice.api.controller;

import de.caritas.cob.uploadservice.api.aspect.TempCleanup;
import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.facade.UploadFacade;
import de.caritas.cob.uploadservice.api.model.MasterKeyDto;
import de.caritas.cob.uploadservice.api.service.EncryptionService;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.generated.api.controller.UploadsApi;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Controller for upload requests. */
@RestController
@Api(tags = "upload-controller")
public class UploadController implements UploadsApi {

  @Autowired UploadFacade uploadFacade;
  @Autowired EncryptionService encryptionService;
  @Autowired LogService logService;

  /**
   * Updates the Master-Key Fragment for the en-/decryption of messages.
   *
   * @param masterKey MasterKeyDTO
   * @return a ResponseEntity instance
   */
  @Override
  public ResponseEntity<Void> updateKey(@Valid @RequestBody MasterKeyDto masterKey) {

    if (!encryptionService.getMasterKey().equals(masterKey.getMasterKey())) {
      encryptionService.updateMasterKey(masterKey.getMasterKey());
      logService.logInfo("MasterKey updated");
      return new ResponseEntity<>(HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.CONFLICT);
  }

  /**
   * Upload a file to a Rocket.Chat room with a text message
   *
   * @param roomId Rocket.Chat room id
   * @param rcToken Rocket.Chat token
   * @param rcUserId Rocket.Chat user id
   * @param file The file object as {@link MultipartFile}
   * @param sendNotification Flag, whether an email notification should be sent or not
   * @param msg The message
   * @param description The description
   * @param tmId Rocket.Chat thread message id
   * @return a {@link ResponseEntity} instance
   */
  @TempCleanup
  @Override
  public ResponseEntity<Void> uploadFileToRoom(
      @NotBlank @NotNull @PathVariable("roomId") String roomId,
      @NotBlank @NotNull @RequestHeader(required = true) String rcToken,
      @NotBlank @NotNull @RequestHeader(required = true) String rcUserId,
      @Valid @RequestPart(required = true) MultipartFile file,
      @Valid @RequestParam(required = true) String sendNotification,
      @Valid @RequestParam(required = false) String msg,
      @Valid @RequestParam(required = false) String description,
      @Valid @RequestParam(required = false) String tmId) {

    RocketChatCredentials rocketChatCredentials =
        RocketChatCredentials.builder().rocketChatUserId(rcUserId).rocketChatToken(rcToken).build();

    RocketChatUploadParameter rocketChatUploadParameter =
        RocketChatUploadParameter.builder()
            .roomId(roomId)
            .description(description)
            .message(msg)
            .file(file)
            .tmId(tmId)
            .build();

    return new ResponseEntity<Void>(
        uploadFacade.uploadFileToRoom(
            rocketChatCredentials, rocketChatUploadParameter, Boolean.valueOf(sendNotification)));
  }

  /**
   * Upload a file to a Rocket.Chat feedback room with a text message
   *
   * @param feedbackRoomId Rocket.Chat feedback room id
   * @param rcToken Rocket.Chat token
   * @param rcUserId Rocket.Chat user id
   * @param file The file object as {@link MultipartFile}
   * @param sendNotification Flag, whether an email notification should be sent or not
   * @param msg The message
   * @param description The description
   * @param tmId Rocket.Chat thread message id
   * @return a ResponseEntity instance
   */
  @TempCleanup
  @Override
  public ResponseEntity<Void> uploadFileToFeedbackRoom(
      @NotBlank @NotNull @PathVariable("feedbackRoomId") String feedbackRoomId,
      @NotBlank @NotNull @RequestHeader(required = true) String rcToken,
      @NotBlank @NotNull @RequestHeader(required = true) String rcUserId,
      @Valid @RequestPart(required = true) MultipartFile file,
      @Valid @RequestParam(required = true) String sendNotification,
      @Valid @RequestParam(required = false) String msg,
      @Valid @RequestParam(required = false) String description,
      @Valid @RequestParam(required = false) String tmId) {

    RocketChatCredentials rocketChatCredentials =
        RocketChatCredentials.builder().rocketChatUserId(rcUserId).rocketChatToken(rcToken).build();

    RocketChatUploadParameter rocketChatUploadParameter =
        RocketChatUploadParameter.builder()
            .roomId(feedbackRoomId)
            .description(description)
            .message(msg)
            .file(file)
            .tmId(tmId)
            .build();

    return new ResponseEntity<Void>(
        uploadFacade.uploadFileToFeedbackRoom(
            rocketChatCredentials, rocketChatUploadParameter, Boolean.valueOf(sendNotification)));
  }
}

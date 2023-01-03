package de.caritas.cob.uploadservice.api.controller;

import static java.lang.Boolean.parseBoolean;

import de.caritas.cob.uploadservice.api.aspect.TempCleanup;
import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.facade.UploadFacade;
import de.caritas.cob.uploadservice.api.model.MasterKeyDto;
import de.caritas.cob.uploadservice.api.service.EncryptionService;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.generated.api.controller.UploadsApi;
import io.swagger.annotations.Api;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for upload requests.
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "upload-controller")
public class UploadController implements UploadsApi {

  private final @NonNull UploadFacade uploadFacade;
  private final @NonNull EncryptionService encryptionService;

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
      LogService.logInfo("MasterKey updated");
      return new ResponseEntity<>(HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.CONFLICT);
  }

  /**
   * Upload a file to a Rocket.Chat room with a text message.
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
      @PathVariable("roomId") String roomId,
      @RequestHeader String rcToken,
      @RequestHeader String rcUserId,
      @RequestPart MultipartFile file,
      @RequestParam String sendNotification,
      @RequestPart(required = false) String t,
      @RequestPart(required = false) String fileHeader,
      @RequestParam(required = false) String msg,
      @RequestParam(required = false) String description,
      @RequestParam(required = false) String tmId) {

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

    uploadFacade.uploadFileToRoom(
        rocketChatCredentials,
        rocketChatUploadParameter,
        parseBoolean(sendNotification),
        t,
        fileHeader
    );

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  /**
   * Upload a file to a Rocket.Chat feedback room with a text message.
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
      @PathVariable("feedbackRoomId") String feedbackRoomId,
      @RequestHeader String rcToken,
      @RequestHeader String rcUserId,
      @RequestPart MultipartFile file,
      @RequestParam String sendNotification,
      @RequestPart(required = false) String t,
      @RequestPart(required = false) String fileHeader,
      @RequestParam(required = false) String msg,
      @RequestParam(required = false) String description,
      @RequestParam(required = false) String tmId) {

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

    uploadFacade.uploadFileToFeedbackRoom(
        rocketChatCredentials,
        rocketChatUploadParameter,
        parseBoolean(sendNotification),
        t,
        fileHeader
    );

    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}

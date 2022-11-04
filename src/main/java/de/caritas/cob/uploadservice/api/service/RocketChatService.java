package de.caritas.cob.uploadservice.api.service;

import static java.util.Objects.nonNull;

import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.container.UploadError;
import de.caritas.cob.uploadservice.api.exception.InvalidFileTypeException;
import de.caritas.cob.uploadservice.api.exception.RocketChatPostMarkGroupAsReadException;
import de.caritas.cob.uploadservice.api.exception.RocketChatUserNotInitializedException;
import de.caritas.cob.uploadservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.uploadservice.api.helper.FileSanitizer;
import de.caritas.cob.uploadservice.api.helper.MultipartInputStreamFileResource;
import de.caritas.cob.uploadservice.api.helper.UploadErrorHelper;
import de.caritas.cob.uploadservice.api.model.rocket.chat.StandardResponseDto;
import de.caritas.cob.uploadservice.api.model.rocket.chat.UploadResponseDto;
import de.caritas.cob.uploadservice.api.model.rocket.chat.group.PostGroupAsReadDto;
import de.caritas.cob.uploadservice.api.model.rocket.chat.message.SendMessageResponseDTO;
import de.caritas.cob.uploadservice.api.model.rocket.chat.message.SendMessageWrapper;
import de.caritas.cob.uploadservice.api.service.dto.StringifiedMessageResponse;
import de.caritas.cob.uploadservice.api.service.helper.RocketChatCredentialsHelper;
import de.caritas.cob.uploadservice.rocketchat.generated.web.model.FullUploadResponseDto;
import de.caritas.cob.uploadservice.rocketchat.generated.web.model.SendMessageDto;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class RocketChatService {

  @Value("${rocket.chat.api.post.group.messages.read.url}")
  private String rcPostGroupMessagesRead;

  @Value("${rocket.chat.api.rooms.upload.url}")
  private String rcRoomsUpload;

  @Value("${rocket.chat.api.rooms.delete.url}")
  private String rcDelete;

  @Value("${rocket.chat.api.send.message.url}")
  private String rcSendMessageUrl;

  @Value("${rocket.chat.api.update.message.url}")
  private String rcUpdateMessageUrl;

  @Value("${rocket.chat.header.auth.token}")
  private String rcHeaderAuthToken;

  @Value("${rocket.chat.header.user.id}")
  private String rcHeaderUserId;

  @Value("${rocket.chat.form.param.msg}")
  private String rcFormParamMsg;

  @Value("${rocket.chat.form.param.description}")
  private String rcFormParamDescription;

  @Value("${rocket.chat.form.param.tmid}")
  private String rcFormParamTmid;

  @Value("${rocket.chat.form.param.file}")
  private String rcFormParamFile;

  public static final String ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE = "error-file-too-large";
  public static final String ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE = "error-invalid-file-type";

  private final @NonNull RestTemplate restTemplate;
  private final @NonNull RocketChatCredentialsHelper rcCredentialHelper;
  private final @NonNull UploadErrorHelper uploadErrorHelper;
  private final @NonNull RocketChatMapper mapper;

  private HttpHeaders getRocketChatHeader(String rcToken, String rcUserId) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(rcHeaderAuthToken, rcToken);
    headers.add(rcHeaderUserId, rcUserId);

    return headers;
  }

  /**
   * Marks the specified Rocket.Chat group as read for the system (message) user.
   *
   * @param rcGroupId String
   */
  public void markGroupAsReadForSystemUser(String rcGroupId)
      throws RocketChatPostMarkGroupAsReadException {

    RocketChatCredentials rocketChatCredentials;
    try {
      rocketChatCredentials = rcCredentialHelper.getSystemUser();
    } catch (RocketChatUserNotInitializedException e) {
      throw new InternalServerErrorException(e, LogService::logInternalServerError);
    }

    if (rocketChatCredentials.getRocketChatToken() != null
        && rocketChatCredentials.getRocketChatUserId() != null) {
      this.markGroupAsRead(
          rocketChatCredentials.getRocketChatToken(),
          rocketChatCredentials.getRocketChatUserId(),
          rcGroupId);

    } else {
      LogService.logRocketChatServiceError(
          String.format("Could not set messages as read for system user in group %s", rcGroupId));
    }
  }

  private void markGroupAsRead(String rcToken, String rcUserId, String rcGroupId)
      throws RocketChatPostMarkGroupAsReadException {

    try {
      HttpHeaders headers = getRocketChatHeader(rcToken, rcUserId);
      PostGroupAsReadDto postGroupAsReadDto = new PostGroupAsReadDto(rcGroupId);
      HttpEntity<PostGroupAsReadDto> request =
          new HttpEntity<>(postGroupAsReadDto, headers);

      restTemplate.postForObject(rcPostGroupMessagesRead, request, StandardResponseDto.class);
    } catch (Exception ex) {
      throw new RocketChatPostMarkGroupAsReadException(ex);
    }
  }

  /**
   * Upload a file with a message and a description to Rocket.Chat.
   *
   * @param rocketChatCredentials {@link RocketChatCredentials} container
   * @param rocketChatUploadParameter {@link RocketChatUploadParameter} container
   */
  public FullUploadResponseDto roomsUpload(
      RocketChatCredentials rocketChatCredentials,
      RocketChatUploadParameter rocketChatUploadParameter) {

    FullUploadResponseDto response;
    String uploadUrl = rcRoomsUpload + "/" + rocketChatUploadParameter.getRoomId();

    try {
      HttpHeaders headers =
          getRocketChatHeader(
              rocketChatCredentials.getRocketChatToken(),
              rocketChatCredentials.getRocketChatUserId());
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      MultiValueMap<String, Object> map =
          getParameterMapForUploadRequest(
              rocketChatUploadParameter, rocketChatCredentials.getRocketChatUserId());
      HttpEntity<MultiValueMap<String, Object>> request =
          new HttpEntity<>(map, headers);

      response = restTemplate.postForObject(uploadUrl, request, FullUploadResponseDto.class);

    } catch (HttpStatusCodeException httpStatusCodeException) {
      UploadResponseDto errorResponse =
          uploadErrorHelper.getParsedErrorResponse(
              httpStatusCodeException.getResponseBodyAsString());
      UploadError uploadError =
          uploadErrorHelper.getErrorFromUploadResponse(
              errorResponse,
              rocketChatUploadParameter.getRoomId(),
              rocketChatCredentials.getRocketChatUserId());
      if (uploadError.getRcErrorType().equals(ROCKET_CHAT_ERROR_TYPE_FILE_TOO_LARGE)) {
        throw new MaxUploadSizeExceededException(-1, httpStatusCodeException);
      } else if (uploadError.getRcErrorType().equals(ROCKET_CHAT_ERROR_TYPE_INVALID_FILE_TYPE)) {
        throw new InvalidFileTypeException(uploadError.getErrorMessage(), httpStatusCodeException);
      } else {
        throw new MultipartException(uploadError.getErrorMessage(), httpStatusCodeException);
      }
    }

    if (response == null || !response.getSuccess()) {
      throw new MultipartException(
          String.format(
              "Could not upload file to room with id %s for user with id %s",
              rocketChatUploadParameter.getRoomId(), rocketChatCredentials.getRocketChatUserId()));
    }
    return response;
  }

  public SendMessageResponseDTO sendGroupMessage(RocketChatCredentials rocketChatCredentials,
      SendMessageDto sendMessage) {
    var headers = getRocketChatHeader(rocketChatCredentials.getRocketChatToken(),
        rocketChatCredentials.getRocketChatUserId());
    var payload = new SendMessageWrapper(sendMessage);
    var request = new HttpEntity<>(payload, headers);

    try {
      return restTemplate.postForObject(rcSendMessageUrl, request, SendMessageResponseDTO.class);
    } catch (Exception ex) {
      LogService.logRocketChatServiceError(
          "Request body which caused the error was " + request.getBody());
      throw new InternalServerErrorException(ex, LogService::logRocketChatServiceError);
    }
  }

  public boolean setE2eType(RocketChatCredentials rocketChatCredentials, String messageId) {
    String updateMessageUrl = rcUpdateMessageUrl;
    var updateMessage = mapper.e2eUpdateMessage(messageId);
    var entity = new HttpEntity<>(updateMessage,
        getRocketChatHeader(rocketChatCredentials.getRocketChatToken(),
            rocketChatCredentials.getRocketChatUserId()));

    try {
      var response = restTemplate.postForEntity(updateMessageUrl, entity,
          StringifiedMessageResponse.class);
      return isSuccessful(response);
    } catch (HttpClientErrorException exception) {
      log.error("Updating message with attachment E2E type failed.", exception);
      return false;
    }
  }

  public boolean deleteMessage(RocketChatCredentials rocketChatCredentials, String messageId) {
    String deleteUrl = rcDelete;
    var deleteMessage = mapper.deleteMessageOf(messageId);
    var entity = new HttpEntity<>(deleteMessage,
        getRocketChatHeader(rocketChatCredentials.getRocketChatToken(),
            rocketChatCredentials.getRocketChatUserId()));

    try {
      var response = restTemplate.postForEntity(deleteUrl, entity,
          StringifiedMessageResponse.class);
      return isSuccessful(response);
    } catch (HttpClientErrorException exception) {
      log.error("Deleting message failed.", exception);
      return false;
    }
  }

  private boolean isSuccessful(ResponseEntity<StringifiedMessageResponse> response) {
    var body = response.getBody();

    return nonNull(body) && body.getSuccess() && !body.getMessage().contains("\"error\"");
  }

  private MultiValueMap<String, Object> getParameterMapForUploadRequest(
      RocketChatUploadParameter rocketChatUploadParameter, String rcUserId) {

    MultiValueMap<String, Object> parameterMap = new LinkedMultiValueMap<>();
    parameterMap.add(rcFormParamMsg, rocketChatUploadParameter.getMessage());
    parameterMap.add(rcFormParamDescription, rocketChatUploadParameter.getDescription());
    parameterMap.add(rcFormParamTmid, rocketChatUploadParameter.getTmId());

    try {
      MultipartFile file = rocketChatUploadParameter.getFile();
      parameterMap.add(
          rcFormParamFile,
          new MultipartInputStreamFileResource(
              file.getInputStream(), FileSanitizer.sanitizeFileName(file.getOriginalFilename())));
    } catch (IOException ex) {
      throw new InternalServerErrorException(
          String.format(
              "Could not access file to upload to room with id %s for user with id %s",
              rocketChatUploadParameter.getRoomId(), rcUserId), ex,
          LogService::logInternalServerError);
    }

    return parameterMap;
  }
}

package de.caritas.cob.uploadservice.api.helper;

import de.caritas.cob.uploadservice.api.container.UploadError;
import de.caritas.cob.uploadservice.api.model.rocket.chat.UploadResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Helper for extracting errors from {@link UploadResponseDto} */
@Component
public class UploadErrorHelper {

  /**
   * Get a {@link UploadResponseDto} from the Rocket.Chat error response.
   *
   * @param responseBody The responseBody as {@link String}
   * @return a {@link UploadResponseDto} instance
   */
  public UploadResponseDto getParsedErrorResponse(String responseBody) {
    return new JsonStringToObjectConverter<UploadResponseDto>()
        .convert(responseBody, UploadResponseDto.class);
  }

  /**
   * Get a container with error information.
   *
   * @param errorResponse The error response as {@link UploadResponseDto}
   * @param roomId Rocket.Chat room id
   * @param rcUserId Rocket.Chat user id
   * @return an {@link UploadError} instance
   */
  public UploadError getErrorFromUploadResponse(
      UploadResponseDto errorResponse, String roomId, String rcUserId) {

    String rcError = getErrorFromUploadResponse(errorResponse);
    String rcErrorType = getErrorTypeFromUploadResponse(errorResponse);
    String errorMessage =
        String.format(
            "Could not upload file to room with id %s for user with id %s. "
                + "Response from Rocket.Chat: %s (%s)",
            roomId, rcUserId, rcError, rcErrorType);
    return UploadError.builder()
        .rcError(rcError)
        .rcErrorType(rcErrorType)
        .errorMessage(errorMessage)
        .build();
  }

  /**
   * Get the error message null safe from the {@link UploadResponseDto}
   *
   * @param errorResponse The error response as {@link UploadResponseDto}
   * @return the error message (if given) or a general error message
   */
  private String getErrorFromUploadResponse(UploadResponseDto errorResponse) {
    return (errorResponse != null && !StringUtils.isEmpty(errorResponse.getError()))
        ? errorResponse.getError()
        : "no response available";
  }

  /**
   * Get the error type null safe from the {@link UploadResponseDto}
   *
   * @param errorResponse The error response as {@link UploadResponseDto}
   * @return the error message (if given) or a general error type
   */
  private String getErrorTypeFromUploadResponse(UploadResponseDto errorResponse) {
    return (errorResponse != null && !StringUtils.isEmpty(errorResponse.getErrorType()))
        ? errorResponse.getErrorType()
        : "unknown-error";
  }
}

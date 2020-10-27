package de.caritas.cob.uploadservice.api.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.caritas.cob.uploadservice.api.service.helper.ServiceHelper;
import de.caritas.cob.uploadservice.userservice.generated.ApiClient;
import de.caritas.cob.uploadservice.userservice.generated.web.LiveproxyControllerApi;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * Service class to provide live event triggers to the live proxy endpoint in user service.
 */
@Service
@RequiredArgsConstructor
public class LiveEventNotificationService {

  private final @NonNull LiveproxyControllerApi liveproxyControllerApi;
  private final @NonNull ServiceHelper serviceHelper;

  /**
   * Triggers a live event to proxy endpoint of user service.
   *
   * @param rcGroupId the rocket chat group id
   */
  public void sendLiveEvent(String rcGroupId) {
    if (isNotBlank(rcGroupId)) {
      addDefaultHeaders(this.liveproxyControllerApi.getApiClient());
      try {
        this.liveproxyControllerApi.sendLiveEvent(rcGroupId);
      } catch (RestClientException e) {
        LogService.logInternalServerError(
            String.format("Unable to trigger live event for rc group id %s", rcGroupId), e);
      }
    }
  }

  private void addDefaultHeaders(ApiClient apiClient) {
    HttpHeaders headers = this.serviceHelper.getKeycloakAndCsrfHttpHeaders();
    headers.forEach((key, value) -> apiClient.addDefaultHeader(key, value.iterator().next()));
  }

}
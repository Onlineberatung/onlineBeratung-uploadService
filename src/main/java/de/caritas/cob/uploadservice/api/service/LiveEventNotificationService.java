package de.caritas.cob.uploadservice.api.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.caritas.cob.uploadservice.api.service.helper.ServiceHelper;
import de.caritas.cob.uploadservice.config.apiclient.LiveProxyApiControllerFactory;
import de.caritas.cob.uploadservice.userservice.generated.ApiClient;
import de.caritas.cob.uploadservice.userservice.generated.web.LiveproxyControllerApi;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * Service class to provide live event triggers to the live proxy endpoint in user service.
 */
@Service
@RequiredArgsConstructor
public class LiveEventNotificationService {

  private final @NonNull LiveProxyApiControllerFactory liveProxyApiControllerFactory;
  private final @NonNull ServiceHelper serviceHelper;
  private final @NonNull TenantHeaderSupplier tenantHeaderSupplier;

  /**
   * Triggers a live event to proxy endpoint of user service.
   *
   * @param rcGroupId the rocket chat group id
   */
  @Async
  public void sendLiveEvent(String rcGroupId, String accessToken, Optional<Long> tenantId) {
    if (isNotBlank(rcGroupId)) {
      LiveproxyControllerApi liveproxyControllerApi = liveProxyApiControllerFactory.createControllerApi();
      addDefaultHeaders(liveproxyControllerApi.getApiClient(), accessToken, tenantId);
      try {
        liveproxyControllerApi.sendLiveEvent(rcGroupId);
      } catch (RestClientException e) {
        LogService.logInternalServerError(
            String.format("Unable to trigger live event for rc group id %s", rcGroupId), e);
      }
    }
  }

  private void addDefaultHeaders(ApiClient apiClient, String accessToken, Optional<Long> tenantId) {
    var headers = serviceHelper.getKeycloakAndCsrfHttpHeaders(accessToken, tenantId);
    headers.forEach((key, value) -> apiClient.addDefaultHeader(key, value.iterator().next()));
  }

}

package de.caritas.cob.uploadservice.api.helper;

import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.uploadservice.api.service.helper.ServiceHelper;
import de.caritas.cob.uploadservice.api.tenant.TenantContext;
import de.caritas.cob.uploadservice.userservice.generated.web.model.NewMessageNotificationDTO;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/*
 * Helper for sending an email notification via the MailService
 */
@Component
public class EmailNotificationHelper {

  private final ServiceHelper serviceHelper;
  private final TenantHeaderSupplier tenantHeaderSupplier;
  private final de.caritas.cob.uploadservice.userservice.generated.web.UserControllerApi userControllerApi;

  @Autowired
  public EmailNotificationHelper(ServiceHelper serviceHelper, TenantHeaderSupplier tenantHeaderSupplier, de.caritas.cob.uploadservice.userservice.generated.web.UserControllerApi userControllerApi) {
    this.serviceHelper = serviceHelper;
    this.tenantHeaderSupplier = tenantHeaderSupplier;
    this.userControllerApi = userControllerApi;
  }

  /**
   * Send an email via the UserService
   *  @param rcGroupId
   * @param currentTenant
   */
  @Async
  public void sendEmailNotificationViaUserService(
      String rcGroupId, String accessToken,
      Optional<Long> currentTenant) {
    sendEmailNotificationCallingMethod(rcGroupId, accessToken, currentTenant,
        userControllerApi::sendNewMessageNotification);
  }

  @Async
  public void sendEmailFeedbackNotificationViaUserService(
      String rcGroupId, String accessToken,
      Optional<Long> currentTenant) {
    sendEmailNotificationCallingMethod(rcGroupId, accessToken, currentTenant,
        userControllerApi::sendNewFeedbackMessageNotification);
  }

  private void sendEmailNotificationCallingMethod(String rcGroupId, String accessToken, Optional<Long> currentTenant, Consumer<NewMessageNotificationDTO> newMessageNotificationConsumerMethod) {
    try {
      NewMessageNotificationDTO notificationDto = new NewMessageNotificationDTO().rcGroupId(
          rcGroupId);
      addDefaultHeaders(userControllerApi.getApiClient(), accessToken, currentTenant);
      newMessageNotificationConsumerMethod.accept(notificationDto);
      TenantContext.clear();
    } catch (RestClientException ex) {
      LogService.logUserServiceHelperError(ex);
    }
  }

  private void addDefaultHeaders(
      de.caritas.cob.uploadservice.userservice.generated.ApiClient apiClient, String accessToken, Optional<Long> currentTenant) {
    HttpHeaders headers = this.serviceHelper.getKeycloakAndCsrfHttpHeaders(accessToken);
    addTenantHeaderIfPresent(currentTenant, headers);
    headers.forEach((key, value) -> apiClient.addDefaultHeader(key, value.iterator().next()));
  }

  private void addTenantHeaderIfPresent(Optional<Long> currentTenant, HttpHeaders headers) {
    if (currentTenant.isPresent()) {
      TenantContext.setCurrentTenant(currentTenant.get());
      tenantHeaderSupplier.addTenantHeader(headers);
    }
  }
}

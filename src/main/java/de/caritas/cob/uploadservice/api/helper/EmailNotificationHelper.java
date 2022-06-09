package de.caritas.cob.uploadservice.api.helper;

import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.uploadservice.api.service.helper.ServiceHelper;
import de.caritas.cob.uploadservice.api.tenant.TenantContext;
import java.util.Optional;
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
   * @param userServiceApiSendNewMessageNotificationUrl
   * @param currentTenant
   */
  @Async
  public void sendEmailNotificationViaUserService(
      String rcGroupId, String userServiceApiSendNewMessageNotificationUrl, String accessToken,
      Optional<Long> currentTenant) {

    try {
      de.caritas.cob.uploadservice.userservice.generated.web.model.NewMessageNotificationDTO notificationDto = new de.caritas.cob.uploadservice.userservice.generated.web.model.NewMessageNotificationDTO().rcGroupId(rcGroupId);
      addDefaultHeaders(userControllerApi.getApiClient(), accessToken, currentTenant);
      userControllerApi.sendNewMessageNotification(notificationDto);
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

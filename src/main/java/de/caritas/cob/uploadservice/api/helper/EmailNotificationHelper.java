package de.caritas.cob.uploadservice.api.helper;

import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.uploadservice.api.service.helper.ServiceHelper;
import de.caritas.cob.uploadservice.api.tenant.TenantContext;
import de.caritas.cob.uploadservice.config.apiclient.UserServiceApiControllerFactory;
import de.caritas.cob.uploadservice.userservice.generated.ApiClient;
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
  private final UserServiceApiControllerFactory userServiceApiControllerFactory;

  @Autowired
  public EmailNotificationHelper(ServiceHelper serviceHelper,
      TenantHeaderSupplier tenantHeaderSupplier,
      UserServiceApiControllerFactory userServiceApiControllerFactory) {
    this.serviceHelper = serviceHelper;
    this.tenantHeaderSupplier = tenantHeaderSupplier;
    this.userServiceApiControllerFactory = userServiceApiControllerFactory;
  }

  /**
   * Send an email via the UserService
   *
   * @param rcGroupId
   * @param currentTenant
   */
  @Async
  public void sendEmailNotificationViaUserService(
      String rcGroupId, String accessToken,
      Optional<Long> currentTenant) {
    var userControllerApi = userServiceApiControllerFactory.createControllerApi();
    addDefaultHeaders(userControllerApi.getApiClient(), accessToken, currentTenant);
    sendEmailNotificationCallingMethod(rcGroupId, userControllerApi::sendNewMessageNotification);
  }

  @Async
  public void sendEmailFeedbackNotificationViaUserService(
      String rcGroupId, String accessToken,
      Optional<Long> currentTenant) {
    var userControllerApi = userServiceApiControllerFactory.createControllerApi();
    addDefaultHeaders(userControllerApi.getApiClient(), accessToken, currentTenant);
    sendEmailNotificationCallingMethod(rcGroupId,
        userControllerApi::sendNewFeedbackMessageNotification);
  }

  private void sendEmailNotificationCallingMethod(String rcGroupId, Consumer<NewMessageNotificationDTO> newMessageNotificationConsumerMethod) {
    try {
      NewMessageNotificationDTO notificationDto = new NewMessageNotificationDTO().rcGroupId(
          rcGroupId);
      newMessageNotificationConsumerMethod.accept(notificationDto);
      TenantContext.clear();
    } catch (RestClientException ex) {
      LogService.logUserServiceHelperError(ex);
    }
  }

  private void addDefaultHeaders(ApiClient apiClient, String accessToken,
      Optional<Long> currentTenant) {
    HttpHeaders headers = this.serviceHelper.getKeycloakAndCsrfHttpHeaders(accessToken,
        currentTenant);
    addTenantHeaderIfPresent(currentTenant, headers);
    headers.forEach((key, value) -> apiClient.addDefaultHeader(key, value.iterator().next()));
  }

  private void addTenantHeaderIfPresent(Optional<Long> currentTenant, HttpHeaders headers) {
    if (currentTenant.isPresent()) {
      TenantContext.setCurrentTenant(currentTenant.get());
      tenantHeaderSupplier.addTenantHeader(headers, currentTenant);
    }
  }
}

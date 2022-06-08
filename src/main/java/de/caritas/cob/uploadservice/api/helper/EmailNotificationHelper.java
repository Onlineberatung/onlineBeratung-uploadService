package de.caritas.cob.uploadservice.api.helper;

import de.caritas.cob.uploadservice.api.model.NewMessageNotificationDto;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.uploadservice.api.service.helper.ServiceHelper;
import de.caritas.cob.uploadservice.api.tenant.TenantContext;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/*
 * Helper for sending an email notification via the MailService
 */
@Component
public class EmailNotificationHelper {

  private final RestTemplate restTemplate;
  private final ServiceHelper serviceHelper;
  private final TenantHeaderSupplier tenantHeaderSupplier;

  @Autowired
  public EmailNotificationHelper(RestTemplate restTemplate, ServiceHelper serviceHelper, TenantHeaderSupplier tenantHeaderSupplier) {
    this.restTemplate = restTemplate;
    this.serviceHelper = serviceHelper;
    this.tenantHeaderSupplier = tenantHeaderSupplier;
  }

  @Async
  public void sendEmailNotificationViaUserService(
      String rcGroupId, String userServiceApiSendNewMessageNotificationUrl, String accessToken) {

    try {
      HttpHeaders header = serviceHelper.getKeycloakAndCsrfHttpHeaders(accessToken);
      NewMessageNotificationDto notificationDto = new NewMessageNotificationDto(rcGroupId);
      HttpEntity<NewMessageNotificationDto> request = new HttpEntity<>(notificationDto, header);

      restTemplate.exchange(
          userServiceApiSendNewMessageNotificationUrl, HttpMethod.POST, request, Void.class);

    } catch (RestClientException ex) {
      LogService.logUserServiceHelperError(ex);
    }
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
      HttpHeaders header = serviceHelper.getKeycloakAndCsrfHttpHeaders(accessToken);
      NewMessageNotificationDto notificationDto = new NewMessageNotificationDto(rcGroupId);
      addTenantHeaderIfPresent(currentTenant, header);
      HttpEntity<NewMessageNotificationDto> request = new HttpEntity<>(notificationDto, header);
      restTemplate.exchange(
          userServiceApiSendNewMessageNotificationUrl, HttpMethod.POST, request, Void.class);

    } catch (RestClientException ex) {
      LogService.logUserServiceHelperError(ex);
    }
  }

  private void addTenantHeaderIfPresent(Optional<Long> currentTenant, HttpHeaders header) {
    if (currentTenant.isPresent()) {
      TenantContext.setCurrentTenant(currentTenant.get());
      tenantHeaderSupplier.addTenantHeader(header);
      TenantContext.clear();
    }
  }
}

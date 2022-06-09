package de.caritas.cob.uploadservice.api.facade;

import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.api.helper.EmailNotificationHelper;
import de.caritas.cob.uploadservice.api.tenant.TenantContext;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*
 * Facade to encapsulate the steps for sending an email notification
 */
@Component
public class EmailNotificationFacade {

  private final EmailNotificationHelper emailNotificationHelper;
  private final AuthenticatedUser authenticatedUser;

  @Value("${user.service.api.new.message.notification}")
  private String userServiceApiSendNewMessageNotificationUrl;

  @Value("${user.service.api.new.feedback.message.notification}")
  private String userServiceApiSendNewFeedbackMessageNotificationUrl;

  @Value("${multitenancy.enabled}")
  private boolean multitenancy;

  @Autowired
  public EmailNotificationFacade(
      EmailNotificationHelper emailNotificationHelper, AuthenticatedUser authenticatedUser) {
    this.emailNotificationHelper = emailNotificationHelper;
    this.authenticatedUser = authenticatedUser;
  }

  /**
   * Sends a new message notification via the UserService (user data needed for sending the mail
   * will be read by the UserService, which in turn calls the UploadService).
   *
   * @param rcGroupId
   */
  public void sendEmailNotification(String rcGroupId) {
    emailNotificationHelper.sendEmailNotificationViaUserService(
        rcGroupId,
        authenticatedUser.getAccessToken(),
        Optional.ofNullable(TenantContext.getCurrentTenant()));
  }

  /**
   * Sends a new feedback message notification via the UserService (user data needed for sending the
   * mail will be read by the UserService, which in turn calls the UploadService).
   *
   * @param rcGroupId
   */
  public void sendFeedbackEmailNotification(String rcGroupId) {
    emailNotificationHelper.sendEmailFeedbackNotificationViaUserService(
        rcGroupId,
        authenticatedUser.getAccessToken(), Optional.ofNullable(TenantContext.getCurrentTenant()));
  }
}

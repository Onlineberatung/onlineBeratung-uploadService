package de.caritas.cob.uploadservice.api.facade;

import static de.caritas.cob.uploadservice.helper.TestConstants.KEYCLOAK_ACCESS_TOKEN;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.api.helper.EmailNotificationHelper;
import de.caritas.cob.uploadservice.api.tenant.TenantContext;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationFacadeTest {

  private static final String FIELD_NAME_NEW_MESSAGE_NOTIFICATION =
      "userServiceApiSendNewMessageNotificationUrl";
  private static final String NOTIFICATION_API_URL =
      "http://caritas.local/service/users/mails/messages/new";
  private static final String FIELD_NAME_NEW_FEEDBACK_MESSAGE_NOTIFICATION =
      "userServiceApiSendNewFeedbackMessageNotificationUrl";
  private static final String FEEDBACK_NOTIFICATION_API_URL =
      "http://caritas.local/service/users/mails/messages/feedback/new";
  private static final String RC_GROUP_ID = "fR2Rz7dmWmHdXE8uz";

  @Mock private EmailNotificationHelper emailNotificationHelper;
  @Mock private AuthenticatedUser authenticatedUser;
  @InjectMocks private EmailNotificationFacade emailNotificationFacade;

  @Before
  public void setup() throws NoSuchFieldException, SecurityException {
    ReflectionTestUtils.setField(emailNotificationFacade, FIELD_NAME_NEW_MESSAGE_NOTIFICATION, NOTIFICATION_API_URL);
    ReflectionTestUtils.setField(emailNotificationFacade, FIELD_NAME_NEW_FEEDBACK_MESSAGE_NOTIFICATION, FEEDBACK_NOTIFICATION_API_URL);
  }

  @Test
  public void sendEmailNotification_Should_PassSendNewMessageNotificationUrl2NotificationHelper() {

    when(authenticatedUser.getAccessToken()).thenReturn(KEYCLOAK_ACCESS_TOKEN);

    emailNotificationFacade.sendEmailNotification(RC_GROUP_ID);

    verify(emailNotificationHelper, times(1))
        .sendEmailNotificationViaUserService(
            RC_GROUP_ID, KEYCLOAK_ACCESS_TOKEN, Optional.ofNullable((TenantContext.getCurrentTenant())));
  }

  @Test
  public void
      sendFeedbackEmailNotification_Should_PassNewFeedbMsgNotificationUrl2NotificationHelper() {

    when(authenticatedUser.getAccessToken()).thenReturn(KEYCLOAK_ACCESS_TOKEN);

    emailNotificationFacade.sendFeedbackEmailNotification(RC_GROUP_ID);

    verify(emailNotificationHelper, times(1))
        .sendEmailFeedbackNotificationViaUserService(
            RC_GROUP_ID, KEYCLOAK_ACCESS_TOKEN, Optional.ofNullable(TenantContext.getCurrentTenant()));
  }
}

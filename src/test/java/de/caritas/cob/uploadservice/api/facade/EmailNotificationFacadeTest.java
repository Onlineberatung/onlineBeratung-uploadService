package de.caritas.cob.uploadservice.api.facade;

import static de.caritas.cob.uploadservice.helper.TestConstants.KEYCLOAK_ACCESS_TOKEN;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.api.helper.EmailNotificationHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

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
    FieldSetter.setField(
        emailNotificationFacade,
        emailNotificationFacade.getClass().getDeclaredField(FIELD_NAME_NEW_MESSAGE_NOTIFICATION),
        NOTIFICATION_API_URL);
    FieldSetter.setField(
        emailNotificationFacade,
        emailNotificationFacade
            .getClass()
            .getDeclaredField(FIELD_NAME_NEW_FEEDBACK_MESSAGE_NOTIFICATION),
        FEEDBACK_NOTIFICATION_API_URL);
  }

  @Test
  public void sendEmailNotification_Should_PassSendNewMessageNotificationUrl2NotificationHelper() {

    when(authenticatedUser.getAccessToken()).thenReturn(KEYCLOAK_ACCESS_TOKEN);

    emailNotificationFacade.sendEmailNotification(RC_GROUP_ID);

    verify(emailNotificationHelper, times(1))
        .sendEmailNotificationViaUserService(
            RC_GROUP_ID, NOTIFICATION_API_URL, KEYCLOAK_ACCESS_TOKEN);
  }

  @Test
  public void
      sendFeedbackEmailNotification_Should_PassNewFeedbMsgNotificationUrl2NotificationHelper() {

    when(authenticatedUser.getAccessToken()).thenReturn(KEYCLOAK_ACCESS_TOKEN);

    emailNotificationFacade.sendFeedbackEmailNotification(RC_GROUP_ID);

    verify(emailNotificationHelper, times(1))
        .sendEmailNotificationViaUserService(
            RC_GROUP_ID, FEEDBACK_NOTIFICATION_API_URL, KEYCLOAK_ACCESS_TOKEN);
  }
}

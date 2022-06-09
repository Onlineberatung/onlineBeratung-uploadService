package de.caritas.cob.uploadservice.api.helper;

import static de.caritas.cob.uploadservice.helper.TestConstants.KEYCLOAK_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.uploadservice.api.service.helper.ServiceHelper;
import de.caritas.cob.uploadservice.api.tenant.TenantContext;
import de.caritas.cob.uploadservice.userservice.generated.web.model.NewMessageNotificationDTO;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import de.caritas.cob.uploadservice.userservice.generated.web.UserControllerApi;

@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationHelperTest {

  private static final String RC_GROUP_ID = "fR2Rz7dmWmHdXE8uz";
  private static final String USER_SERVICE_API_SEND_NEW_MESSAGE_NOTIFICATION_URL =
      "http://caritas.local/service/user/mails/new";
  private static final String ERROR = "error";

  @Mock private RestTemplate restTemplate;

  @Mock private UserControllerApi userControllerApi;
  @Mock private de.caritas.cob.uploadservice.userservice.generated.ApiClient apiClient;
  @Mock private ServiceHelper serviceHelper;
  @Mock private Logger logger;
  @Mock private TenantHeaderSupplier tenantHeaderSupplier;
  @InjectMocks private EmailNotificationHelper emailNotificationHelper;

  @Before
  public void setup() {
    setInternalState(LogService.class, "LOGGER", logger);
  }

  @Test
  public void sendEmailNotificationViaUserService_Should_LogException_OnError()
      throws RestClientException {

    // given
    RestClientException exception = new RestClientException(ERROR);
    when(userControllerApi.getApiClient()).thenReturn(apiClient);
    when(serviceHelper.getKeycloakAndCsrfHttpHeaders(Mockito.anyString())).thenReturn(new HttpHeaders());
    doThrow(exception).when(userControllerApi).sendNewMessageNotification(Mockito.any(NewMessageNotificationDTO.class));

    // when
    emailNotificationHelper.sendEmailNotificationViaUserService(
        RC_GROUP_ID, USER_SERVICE_API_SEND_NEW_MESSAGE_NOTIFICATION_URL, KEYCLOAK_ACCESS_TOKEN,
        Optional.ofNullable(TenantContext.getCurrentTenant()));

    // then
    verify(logger, times(1)).error(anyString(), anyString());
  }

  @Test
  public void sendEmailNotificationViaUserService_Should_CallUserServiceWithGiveUrl() {
    // given
    when(userControllerApi.getApiClient()).thenReturn(apiClient);
    when(serviceHelper.getKeycloakAndCsrfHttpHeaders(Mockito.anyString())).thenReturn(new HttpHeaders());

    // when
    emailNotificationHelper.sendEmailNotificationViaUserService(
        RC_GROUP_ID, USER_SERVICE_API_SEND_NEW_MESSAGE_NOTIFICATION_URL, KEYCLOAK_ACCESS_TOKEN,
        Optional.ofNullable(TenantContext.getCurrentTenant()));

    // then
    verify(userControllerApi, times(1))
        .sendNewMessageNotification(Mockito.any(NewMessageNotificationDTO.class));
  }
}

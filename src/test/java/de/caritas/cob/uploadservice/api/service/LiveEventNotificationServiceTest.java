package de.caritas.cob.uploadservice.api.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import de.caritas.cob.uploadservice.api.service.helper.ServiceHelper;
import de.caritas.cob.uploadservice.userservice.generated.ApiClient;
import de.caritas.cob.uploadservice.userservice.generated.web.LiveproxyControllerApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;

@RunWith(MockitoJUnitRunner.class)
public class LiveEventNotificationServiceTest {

  @InjectMocks
  private LiveEventNotificationService liveEventNotificationService;

  @Mock
  private LiveproxyControllerApi liveproxyControllerApi;

  @Mock
  private ServiceHelper serviceHelper;

  @Mock
  private Logger logger;

  @Before
  public void setup() {
    setInternalState(LogService.class, "LOGGER", logger);
  }

  @Test
  public void sendLiveEvent_Should_notTriggerLiveEvent_When_rcGroupIdIsNull() {
    this.liveEventNotificationService.sendLiveEvent(null);

    verifyNoMoreInteractions(this.liveproxyControllerApi);
    verifyNoMoreInteractions(this.serviceHelper);
  }

  @Test
  public void sendLiveEvent_Should_notTriggerLiveEvent_When_rcGroupIdIsEmpty() {
    this.liveEventNotificationService.sendLiveEvent("");

    verifyNoMoreInteractions(this.liveproxyControllerApi);
    verifyNoMoreInteractions(this.serviceHelper);
  }

  @Test
  public void sendLiveEvent_Should_triggerLiveEventWithHeaders_When_rcGroupIdIsValid() {
    ApiClient apiClient = mock(ApiClient.class);
    when(this.liveproxyControllerApi.getApiClient()).thenReturn(apiClient);
    HttpHeaders headers = new HttpHeaders();
    headers.add("header 1", "value 1");
    headers.add("header 2", "value 2");
    when(this.serviceHelper.getKeycloakAndCsrfHttpHeaders()).thenReturn(headers);

    this.liveEventNotificationService.sendLiveEvent("valid");

    verify(this.liveproxyControllerApi, times(1)).sendLiveEvent("valid");
    verify(this.serviceHelper, times(1)).getKeycloakAndCsrfHttpHeaders();
    verify(apiClient, times(2)).addDefaultHeader(anyString(), anyString());
  }

  @Test
  public void sendLiveEvent_Should_logError_When_apiClientThrowsRestClientException() {
    doThrow(new RestClientException("")).when(this.liveproxyControllerApi)
        .sendLiveEvent(anyString());
    when(this.liveproxyControllerApi.getApiClient()).thenReturn(mock(ApiClient.class));
    when(this.serviceHelper.getKeycloakAndCsrfHttpHeaders()).thenReturn(new HttpHeaders());

    this.liveEventNotificationService.sendLiveEvent("valid");

    verify(this.logger, times(1)).error(anyString(), anyString(), anyString());
  }

}

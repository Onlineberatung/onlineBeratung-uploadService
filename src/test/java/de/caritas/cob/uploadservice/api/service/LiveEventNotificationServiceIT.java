package de.caritas.cob.uploadservice.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.config.SecurityConfig;
import de.caritas.cob.uploadservice.userservice.generated.ApiClient;
import de.caritas.cob.uploadservice.userservice.generated.web.LiveproxyControllerApi;
import java.lang.management.ManagementFactory;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

@SpringBootTest
@ActiveProfiles("testing")
@DirtiesContext
class LiveEventNotificationServiceIT {

  @Autowired
  private LiveEventNotificationService underTest;

  @MockBean
  @Qualifier("liveproxyControllerApi")
  @SuppressWarnings("unused")
  private LiveproxyControllerApi liveproxyControllerApi;


  @MockBean
  private AuthenticatedUser authenticatedUser;

  @MockBean
  private ApiClient apiClient;

  @MockBean
  private DocumentationPluginsBootstrapper documentationPluginsBootstrapper;


  @Test
  void sendLiveEventShouldRunInAnotherThread() {
    when(liveproxyControllerApi.getApiClient()).thenReturn(apiClient);
    var threadCount = ManagementFactory.getThreadMXBean().getThreadCount();

    underTest.sendLiveEvent(
        RandomStringUtils.randomAlphanumeric(16),
        RandomStringUtils.randomAlphanumeric(16),
        Optional.of(Long.valueOf(RandomStringUtils.randomNumeric(1)))
    );

    assertEquals(threadCount + 1, ManagementFactory.getThreadMXBean().getThreadCount());
  }

  @Test
  void sendLiveEventShouldNeverCallAuthenticatedUserMethodsWhenAccessTokenGiven() {
    when(liveproxyControllerApi.getApiClient()).thenReturn(apiClient);

    underTest.sendLiveEvent(
        RandomStringUtils.randomAlphanumeric(16),
        RandomStringUtils.randomAlphanumeric(16),
        Optional.of(Long.valueOf(RandomStringUtils.randomNumeric(1)))
    );

    verify(authenticatedUser, timeout(1000).times(0))
        .getAccessToken();
  }

  @Test
  void sendLiveEventShouldCallAuthenticatedUserMethodsWhenAccessTokenMissing() {
    when(liveproxyControllerApi.getApiClient()).thenReturn(apiClient);

    underTest.sendLiveEvent(
        RandomStringUtils.randomAlphanumeric(16),
        null,
        Optional.of(Long.valueOf(RandomStringUtils.randomNumeric(1)))
    );

    verify(authenticatedUser, timeout(1000)).getAccessToken();
  }
}

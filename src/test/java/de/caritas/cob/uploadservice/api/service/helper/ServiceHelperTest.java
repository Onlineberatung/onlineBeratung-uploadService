package de.caritas.cob.uploadservice.api.service.helper;

import static de.caritas.cob.uploadservice.helper.FieldConstants.CSRF_TOKEN_COOKIE_VALUE;
import static de.caritas.cob.uploadservice.helper.FieldConstants.CSRF_TOKEN_HEADER_VALUE;
import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_NAME_CSRF_TOKEN_COOKIE_PROPERTY;
import static de.caritas.cob.uploadservice.helper.FieldConstants.FIELD_NAME_CSRF_TOKEN_HEADER_PROPERTY;
import static de.caritas.cob.uploadservice.helper.TestConstants.AUTHORIZATION;
import static de.caritas.cob.uploadservice.helper.TestConstants.BEARER;
import static de.caritas.cob.uploadservice.helper.TestConstants.KEYCLOAK_ACCESS_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RunWith(MockitoJUnitRunner.class)
public class ServiceHelperTest {

  @InjectMocks private ServiceHelper serviceHelper;

  @Mock private AuthenticatedUser authenticatedUser;

  @Mock
  @SuppressWarnings("unused")
  private TenantHeaderSupplier tenantHeaderSupplier;

  @Before
  public void setup() throws NoSuchFieldException, SecurityException {
    FieldSetter.setField(
        serviceHelper,
        serviceHelper.getClass().getDeclaredField(FIELD_NAME_CSRF_TOKEN_HEADER_PROPERTY),
        CSRF_TOKEN_HEADER_VALUE);
    FieldSetter.setField(
        serviceHelper,
        serviceHelper.getClass().getDeclaredField(FIELD_NAME_CSRF_TOKEN_COOKIE_PROPERTY),
        CSRF_TOKEN_COOKIE_VALUE);
  }

  /**
   * Tests for method: getKeycloakAndCsrfHttpHeaders()
   */
  @Test
  public void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithCorrectContentType() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(
        RandomStringUtils.randomAlphanumeric(16), Optional.empty()
    );
    assertEquals(MediaType.APPLICATION_JSON_UTF8, result.getContentType());
  }

  @Test
  public void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithCookiePropertyNameFromProperties() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(
        RandomStringUtils.randomAlphanumeric(16), Optional.empty()
    );
    assertTrue(result.get("Cookie").toString().startsWith("[" + CSRF_TOKEN_COOKIE_VALUE + "="));
  }

  @Test
  public void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithPropertyNameFromProperties() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(
        RandomStringUtils.randomAlphanumeric(16), Optional.empty()
    );
    assertNotNull(result.get(CSRF_TOKEN_HEADER_VALUE));
  }

  @Test
  public void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithBearerTokenForCurrentlyAuthenticatedUser() {

    when(authenticatedUser.getAccessToken()).thenReturn(KEYCLOAK_ACCESS_TOKEN);

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(
        null, Optional.empty()
    );
    assertNotNull(result.get(AUTHORIZATION));
    assertEquals(BEARER + KEYCLOAK_ACCESS_TOKEN, result.get(AUTHORIZATION).get(0));
  }

  /**
   * Test for method: getKeycloakAndCsrfHttpHeaders(String accessToken)
   */
  @Test
  public void getKeycloakAndCsrfHttpHeadersWithTokenParam_Should_Return_HeaderWithCorrectContentType() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(KEYCLOAK_ACCESS_TOKEN, Optional.empty());
    assertEquals(MediaType.APPLICATION_JSON_UTF8, result.getContentType());
  }

  @Test
  public void getKeycloakAndCsrfHttpHeadersWithTokenParam_Should_Return_HeaderWithCookiePropertyNameFromProperties() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(KEYCLOAK_ACCESS_TOKEN, Optional.empty());
    assertTrue(result.get("Cookie").toString().startsWith("[" + CSRF_TOKEN_COOKIE_VALUE + "="));
  }

  @Test
  public void getKeycloakAndCsrfHttpHeadersWithTokenParam_Should_Return_HeaderWithPropertyNameFromProperties() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(KEYCLOAK_ACCESS_TOKEN, Optional.empty());
    assertNotNull(result.get(CSRF_TOKEN_HEADER_VALUE));
  }

  @Test
  public void getKeycloakAndCsrfHttpHeadersWithTokenParam_Should_Return_HeaderWithBearerTokenForCurrentlyAuthenticatedUser() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(KEYCLOAK_ACCESS_TOKEN, Optional.empty());
    assertNotNull(result.get(AUTHORIZATION));
    assertEquals(BEARER + KEYCLOAK_ACCESS_TOKEN, result.get(AUTHORIZATION).get(0));
  }
}

package de.caritas.cob.uploadservice.api.service.helper;

import static de.caritas.cob.uploadservice.helper.TestConstants.KEYCLOAK_ACCESS_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
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

  private static final String FIELD_NAME_CSRF_TOKEN_HEADER_PROPERTY = "csrfHeaderProperty";
  private static final String FIELD_NAME_CSRF_TOKEN_COOKIE_PROPERTY = "csrfCookieProperty";
  private static final String CSRF_TOKEN_HEADER_VALUE = "X-CSRF-TOKEN";
  private static final String CSRF_TOKEN_COOKIE_VALUE = "CSRF-TOKEN";
  private static final String AUTHORIZATION = "Authorization";

  @Mock private AuthenticatedUser authenticatedUser;
  @InjectMocks private ServiceHelper serviceHelper;

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

  /** Tests for method: getKeycloakAndCsrfHttpHeaders */
  @Test
  public void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithCorrectContentType() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(KEYCLOAK_ACCESS_TOKEN);
    assertEquals(MediaType.APPLICATION_JSON_UTF8, result.getContentType());
  }

  @Test
  public void
      getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithCookiePropertyNameFromProperties() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(KEYCLOAK_ACCESS_TOKEN);
    assertTrue(result.get("Cookie").toString().startsWith("[" + CSRF_TOKEN_COOKIE_VALUE + "="));
  }

  @Test
  public void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithPropertyNameFromProperties() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(KEYCLOAK_ACCESS_TOKEN);
    assertNotNull(result.get(CSRF_TOKEN_HEADER_VALUE));
  }

  @Test
  public void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithBearerAuthorization() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfHttpHeaders(KEYCLOAK_ACCESS_TOKEN);
    assertNotNull(result.get(AUTHORIZATION));
  }
}

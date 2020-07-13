package de.caritas.cob.uploadservice.api.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatedUserTest {

  @Test(expected = NullPointerException.class)
  public void AuthenticatedUser_Should_ThrowNullPointerExceptionWhenArgumentsAreNull()
      throws Exception {
    new AuthenticatedUser(null, null, null, null, null);
  }

  @Test(expected = NullPointerException.class)
  public void AuthenticatedUser_Should_ThrowNullPointerExceptionWhenUserIdIsNull()
      throws Exception {
    AuthenticatedUser authenticatedUser = new AuthenticatedUser();
    authenticatedUser.setUserId(null);
  }

  @Test(expected = NullPointerException.class)
  public void AuthenticatedUser_Should_ThrowNullPointerExceptionWhenUsernameIsNull()
      throws Exception {
    AuthenticatedUser authenticatedUser = new AuthenticatedUser();
    authenticatedUser.setUsername(null);
  }

  @Test(expected = NullPointerException.class)
  public void AuthenticatedUser_Should_ThrowNullPointerExceptionWhenAccessTokenIsNull()
      throws Exception {
    AuthenticatedUser authenticatedUser = new AuthenticatedUser();
    authenticatedUser.setAccessToken(null);
  }
}

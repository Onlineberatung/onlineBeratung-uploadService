package de.caritas.cob.uploadservice.api.helper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import de.caritas.cob.uploadservice.api.authorization.UserRole;
import org.apache.commons.collections4.SetUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatedUserHelperTest {

  @Test
  public void isConsultant_Should_ReturnTrue_IfAuthenticatedUserIsConsultant() {

    AuthenticatedUser authenticatedUser = new AuthenticatedUser();
    authenticatedUser.setRoles(SetUtils.unmodifiableSet(UserRole.CONSULTANT.getValue()));

    boolean result = AuthenticatedUserHelper.isConsultant(authenticatedUser);

    assertThat(result, is(true));
  }

  @Test
  public void isConsultant_Should_ReturnFalse_IfAuthenticatedUserIsNotConsultant() {

    AuthenticatedUser authenticatedUser = new AuthenticatedUser();
    authenticatedUser.setRoles(SetUtils.unmodifiableSet(UserRole.USER.getValue()));

    boolean result = AuthenticatedUserHelper.isConsultant(authenticatedUser);

    assertThat(result, is(false));
  }

}

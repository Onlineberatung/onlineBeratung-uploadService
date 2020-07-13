package de.caritas.cob.uploadservice.api.authorization;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthorityTest {

  @Test
  public void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleConsultant() {

    List<String> result = Authority.getAuthoritiesByUserRole(UserRole.CONSULTANT);

    assertNotNull(result);
    assertTrue(result.contains(Authority.CONSULTANT_DEFAULT));
    assertTrue(result.size() == 1);
  }

  @Test
  public void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleUser() {

    List<String> result = Authority.getAuthoritiesByUserRole(UserRole.USER);

    assertNotNull(result);
    assertTrue(result.contains(Authority.USER_DEFAULT));
    assertTrue(result.size() == 1);
  }

  @Test
  public void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleU25Consultant() {

    List<String> result = Authority.getAuthoritiesByUserRole(UserRole.U25_CONSULTANT);

    assertNotNull(result);
    assertTrue(result.contains(Authority.USE_FEEDBACK));
    assertTrue(result.size() == 1);
  }

  @Test
  public void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleTechnical() {

    List<String> result = Authority.getAuthoritiesByUserRole(UserRole.TECHNICAL);

    assertNotNull(result);
    assertTrue(result.contains(Authority.TECHNICAL_DEFAULT));
    assertTrue(result.size() == 1);
  }

}

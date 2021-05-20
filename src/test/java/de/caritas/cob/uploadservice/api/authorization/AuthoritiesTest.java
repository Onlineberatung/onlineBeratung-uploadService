package de.caritas.cob.uploadservice.api.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.caritas.cob.uploadservice.api.authorization.Authorities.Authority;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthoritiesTest {

  @Test
  public void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleConsultant() {

    List<String> result = Authorities.getAuthoritiesByUserRole(UserRole.CONSULTANT);

    assertNotNull(result);
    assertTrue(result.contains(Authority.CONSULTANT_DEFAULT));
    assertEquals(1, result.size());
  }

  @Test
  public void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleUser() {

    List<String> result = Authorities.getAuthoritiesByUserRole(UserRole.USER);

    assertNotNull(result);
    assertTrue(result.contains(Authority.USER_DEFAULT));
    assertEquals(1, result.size());
  }

  @Test
  public void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleU25Consultant() {

    List<String> result = Authorities.getAuthoritiesByUserRole(UserRole.U25_CONSULTANT);

    assertNotNull(result);
    assertTrue(result.contains(Authority.USE_FEEDBACK));
    assertEquals(1, result.size());
  }

  @Test
  public void getAuthoritiesByRoleName_Should_ReturnCorrectRoles_ForKeycloakRoleTechnical() {

    List<String> result = Authorities.getAuthoritiesByUserRole(UserRole.TECHNICAL);

    assertNotNull(result);
    assertTrue(result.contains(Authority.TECHNICAL_DEFAULT));
    assertEquals(1, result.size());
  }

}

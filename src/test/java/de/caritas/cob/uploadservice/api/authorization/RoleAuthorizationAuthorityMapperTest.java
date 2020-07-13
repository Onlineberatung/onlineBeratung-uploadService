package de.caritas.cob.uploadservice.api.authorization;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RunWith(MockitoJUnitRunner.class)
public class RoleAuthorizationAuthorityMapperTest {

  private KeycloakAuthenticationProvider provider = new KeycloakAuthenticationProvider();
  private Set<String> roles =
      Sets.newSet(
          UserRole.USER.getValue(),
          UserRole.CONSULTANT.getValue(),
          UserRole.U25_CONSULTANT.getValue());

  @Test
  public void roleAuthorizationAuthorityMapper_Should_GrantCorrectAuthorities() throws Exception {

    Principal principal = mock(Principal.class);
    RefreshableKeycloakSecurityContext securityContext =
        mock(RefreshableKeycloakSecurityContext.class);
    KeycloakAccount account = new SimpleKeycloakAccount(principal, roles, securityContext);

    KeycloakAuthenticationToken token = new KeycloakAuthenticationToken(account, false);

    RoleAuthorizationAuthorityMapper roleAuthorizationAuthorityMapper =
        new RoleAuthorizationAuthorityMapper();
    provider.setGrantedAuthoritiesMapper(roleAuthorizationAuthorityMapper);

    Authentication result = provider.authenticate(token);

    List<SimpleGrantedAuthority> expectedGrantendAuthorities =
        new ArrayList<SimpleGrantedAuthority>();
    roles.forEach(
        roleName -> {
          expectedGrantendAuthorities.addAll(
              Authority.getAuthoritiesByUserRole(UserRole.getRoleByValue(roleName).get()).stream()
                  .map(authority -> new SimpleGrantedAuthority(authority))
                  .collect(Collectors.toList()));
        });

    assertThat(expectedGrantendAuthorities, containsInAnyOrder(result.getAuthorities().toArray()));
  }
}

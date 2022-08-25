package de.caritas.cob.uploadservice.api.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessTokenTenantResolverTest {
  @Mock
  HttpServletRequest authenticatedRequest;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  KeycloakAuthenticationToken token;

  @InjectMocks
  AccessTokenTenantResolver accessTokenTenantResolver;

  @Test
  void resolve_Should_ResolveTenantId_When_TenantIdInAccessTokenClaim() {
    // given
    when(authenticatedRequest.getUserPrincipal()).thenReturn(token);

    HashMap<String, Object> claimMap = givenClaimMapContainingTenantId(1);
    when(token.getAccount().getKeycloakSecurityContext().getToken().getOtherClaims())
        .thenReturn(claimMap);

    // when
    Optional<Long> resolvedTenantId = accessTokenTenantResolver.resolve(authenticatedRequest);

    // then
    assertThat(resolvedTenantId).isEqualTo(Optional.of(1L));
  }

  private HashMap<String, Object> givenClaimMapContainingTenantId(Integer tenantId) {
    HashMap<String, Object> claimMap = Maps.newHashMap();
    claimMap.put("tenantId", tenantId);
    return claimMap;
  }
}
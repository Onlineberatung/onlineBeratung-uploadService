package de.caritas.cob.uploadservice.api.tenant;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.google.common.collect.Maps;
import de.caritas.cob.uploadservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.uploadservice.api.service.TenantService;
import de.caritas.cob.uploadservice.filter.SubdomainExtractor;
import java.util.HashMap;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class TenantResolverTest {

  public static final long TECHNICAL_CONTEXT = 0L;
  @Mock
  SubdomainExtractor subdomainExtractor;

  @Mock
  TenantService tenantService;

  @Mock
  TenantHeaderSupplier tenantHeaderSupplier;

  @Mock
  HttpServletRequest authenticatedRequest;

  @Mock
  HttpServletRequest nonAuthenticatedRequest;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  KeycloakAuthenticationToken token;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  AccessToken accessToken;

  @Mock
  Access access;

  @InjectMocks
  TenantResolver tenantResolver;

  @Mock
  private ServletRequestAttributes requestAttributes;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Test
  void resolve_Should_ResolveFromAccessTokenForAuthenticatedUser() {
    // given
    when(authenticatedRequest.getUserPrincipal()).thenReturn(token);
    when(subdomainExtractor.getCurrentSubdomain()).thenReturn(Optional.of("mucoviscidose"));
    when(tenantService.getRestrictedTenantDataBySubdomain("mucoviscidose")).thenReturn(
        new de.caritas.cob.uploadservice.tenantservice.generated.web.model.RestrictedTenantDTO()
            .id(1L));
    HashMap<String, Object> claimMap = givenClaimMapContainingTenantId(1);
    when(token.getAccount().getKeycloakSecurityContext().getToken().getOtherClaims())
        .thenReturn(claimMap);

    // when
    Long resolvedTenantId = tenantResolver.resolve(authenticatedRequest);

    // then
    assertThat(resolvedTenantId).isEqualTo(1L);
  }

  @Test
  void resolve_Should_ThrowAccessDeniedExceptionForAuthenticatedUser_IfThereIsNoTenantIdClaim() {
    // given
    when(authenticatedRequest.getUserPrincipal()).thenReturn(token);
    when(token.getAccount().getKeycloakSecurityContext().getToken().getOtherClaims())
        .thenReturn(Maps.newHashMap());
    // when, then
    assertThrows(AccessDeniedException.class, () -> tenantResolver.resolve(authenticatedRequest));
  }

  @Test
  void resolve_Should_ThrowAccessDeniedExceptionForNotAuthenticatedUser_IfSubdomainCouldNotBeDetermined() {
    // given
    when(subdomainExtractor.getCurrentSubdomain()).thenReturn(Optional.empty());
    // when, then
    assertThrows(AccessDeniedException.class,
        () -> tenantResolver.resolve(nonAuthenticatedRequest));
  }

  @Test
  void resolve_Should_ThrowAccessDeniedException_IfTokenDoesNotHaveRealmTechnicalRoleAndTenantIdNotExistInClaims() {
    // given, when
    when(authenticatedRequest.getUserPrincipal()).thenReturn(token);
    when(token.getAccount()
        .getKeycloakSecurityContext().getToken()).thenReturn(accessToken);

    when(accessToken.getRealmAccess().getRoles()).thenReturn(Sets.newLinkedHashSet("anyOtherRole"));
    // then
    assertThrows(AccessDeniedException.class, () -> tenantResolver.resolve(authenticatedRequest));
  }

  @Test
  void resolve_Should_ResolveTenantId_IfSubdomainCouldBeDetermined() {
    // given
    when(subdomainExtractor.getCurrentSubdomain()).thenReturn(Optional.of("mucoviscidose"));
    when(tenantService.getRestrictedTenantDataBySubdomain("mucoviscidose")).thenReturn(
        new de.caritas.cob.uploadservice.tenantservice.generated.web.model.RestrictedTenantDTO()
            .id(1L));
    // when
    Long resolved = tenantResolver.resolve(nonAuthenticatedRequest);
    // then
    assertThat(resolved).isEqualTo(1L);
  }

  @Test
  void resolve_Should_ResolveTenantId_ForTechnicalUserRole() {
    // given
    when(authenticatedRequest.getUserPrincipal()).thenReturn(token);
    when(token.getAccount()
        .getKeycloakSecurityContext().getToken()).thenReturn(accessToken);
    when(accessToken.getRealmAccess().getRoles()).thenReturn(Sets.newLinkedHashSet("technical"));
    Long resolved = tenantResolver.resolve(authenticatedRequest);
    // then
    assertThat(resolved).isEqualTo(TECHNICAL_CONTEXT);
  }

  @Test
  void resolve_Should_ResolveTenantId_FromHeader() {
    // given
    when(tenantHeaderSupplier.getTenantFromHeader()).thenReturn(Optional.of(2L));
    Long resolved = tenantResolver.resolve(authenticatedRequest);
    // then
    assertThat(resolved).isEqualTo(2L);
  }

  private HashMap<String, Object> givenClaimMapContainingTenantId(Integer tenantId) {
    HashMap<String, Object> claimMap = Maps.newHashMap();
    claimMap.put("tenantId", tenantId);
    return claimMap;
  }

}
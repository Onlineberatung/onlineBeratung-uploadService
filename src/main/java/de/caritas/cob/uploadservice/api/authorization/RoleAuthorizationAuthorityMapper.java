package de.caritas.cob.uploadservice.api.authorization;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;

/**
 * Own implementation of the Spring GrantedAuthoritiesMapper.
 */
@Component
public class RoleAuthorizationAuthorityMapper implements GrantedAuthoritiesMapper {

  @Override
  public Collection<? extends GrantedAuthority> mapAuthorities(
      Collection<? extends GrantedAuthority> authorities) {
    Set<String> roleNames =
        authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

    return mapAuthorities(roleNames);
  }

  private Set<GrantedAuthority> mapAuthorities(Set<String> roleNames) {
    return roleNames.parallelStream()
        .map(UserRole::getRoleByValue)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(Authority::getAuthoritiesByUserRole)
        .flatMap(Collection::parallelStream)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
  }
}

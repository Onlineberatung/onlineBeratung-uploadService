package de.caritas.cob.uploadservice.api.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;

/** Own implementation of the Spring GrantedAuthoritiesMapper */
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

    HashSet<GrantedAuthority> mapped = new HashSet<>();
    mapped.addAll(mapAuthorities(roleNames));

    return mapped;
  }

  private Set<GrantedAuthority> mapAuthorities(Set<String> roleNames) {
    List<SimpleGrantedAuthority> grantendAuthorities = new ArrayList<SimpleGrantedAuthority>();
    roleNames.forEach(
        roleName -> {
          Optional<UserRole> userRoleOptional = UserRole.getRoleByValue(roleName);
          if (userRoleOptional.isPresent()) {
            grantendAuthorities.addAll(
                Authority.getAuthoritiesByUserRole(userRoleOptional.get()).stream()
                    .map(authority -> new SimpleGrantedAuthority(authority))
                    .collect(Collectors.toList()));
          }
        });
    return new HashSet<>(grantendAuthorities);
  }
}

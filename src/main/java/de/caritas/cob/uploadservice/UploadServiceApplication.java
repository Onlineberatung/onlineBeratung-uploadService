package de.caritas.cob.uploadservice;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import de.caritas.cob.uploadservice.api.exception.KeycloakException;
import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.media.MimeTypeDetector;
import de.caritas.cob.uploadservice.media.TikaMimeTypeDetector;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.tika.Tika;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class UploadServiceApplication {

  @Value("${thread.executor.corePoolSize}")
  private int threadCorePoolSize;

  @Value("${thread.executor.maxPoolSize}")
  private int threadMaxPoolSize;

  @Value("${thread.executor.queueCapacity}")
  private int threadQueueCapacity;

  @Value("${thread.executor.threadNamePrefix}")
  private String threadNamePrefix;

  private static final String CLAIM_NAME_USER_ID = "userId";
  private static final String CLAIM_NAME_USERNAME = "username";

  public static void main(String[] args) {
    SpringApplication.run(UploadServiceApplication.class, args);
  }

  /**
   * Returns the @KeycloakAuthenticationToken which represents the token for a Keycloak
   * authentication.
   *
   * @return KeycloakAuthenticationToken
   */
  @Bean
  @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public KeycloakAuthenticationToken getAccessToken() {
    return (KeycloakAuthenticationToken) getRequest().getUserPrincipal();
  }

  /**
   * Returns the @KeycloakSecurityContext.
   *
   * @return KeycloakSecurityContext
   */
  @Bean
  @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public KeycloakSecurityContext getKeycloakSecurityContext() {
    return ((KeycloakAuthenticationToken) getRequest()
        .getUserPrincipal())
        .getAccount()
        .getKeycloakSecurityContext();
  }

  /**
   * Returns the Keycloak user id of the authenticated user.
   *
   * @return {@link AuthenticatedUser}
   */
  @Bean
  @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public AuthenticatedUser getAuthenticatedUser() {

    // Get current KeycloakSecurityContext
    KeycloakSecurityContext keycloakSecContext =
        ((KeycloakAuthenticationToken) getRequest().getUserPrincipal())
            .getAccount()
            .getKeycloakSecurityContext();

    Map<String, Object> claimMap = keycloakSecContext.getToken().getOtherClaims();

    AuthenticatedUser authenticatedUser = new AuthenticatedUser();

    if (claimMap.containsKey(CLAIM_NAME_USER_ID)) {
      authenticatedUser.setUserId(claimMap.get(CLAIM_NAME_USER_ID).toString());
    } else {
      throw new KeycloakException("Keycloak user attribute '" + CLAIM_NAME_USER_ID + "' not found.");
    }

    if (claimMap.containsKey(CLAIM_NAME_USERNAME)) {
      authenticatedUser.setUsername(claimMap.get(CLAIM_NAME_USERNAME).toString());
    }

    // Set user roles
    AccessToken.Access realmAccess =
        ((KeycloakAuthenticationToken) getRequest().getUserPrincipal())
            .getAccount()
            .getKeycloakSecurityContext()
            .getToken()
            .getRealmAccess();
    Set<String> roles = realmAccess.getRoles();
    if (isNotEmpty(roles)) {
      authenticatedUser.setRoles(roles);
    } else {
      throw new KeycloakException(
          "Keycloak roles null or not set for user: " + authenticatedUser.getUserId() != null
              ? authenticatedUser.getUserId() : "unknown");
    }

    // Set granted authorities
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    authenticatedUser.setGrantedAuthorities(
        authentication.getAuthorities().stream()
            .map(Object::toString)
            .collect(Collectors.toSet()));

    // Set Keycloak token to authenticated user object
    if (keycloakSecContext.getTokenString() != null) {
      authenticatedUser.setAccessToken(keycloakSecContext.getTokenString());
    } else {
      throw new KeycloakException("No valid Keycloak access token string found.");
    }

    return authenticatedUser;
  }

  private HttpServletRequest getRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
        .getRequest();
  }

  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    /*
     * This will create 10 threads at the time of initialization. If all 10 threads are busy and new
     * task comes up, then It will keep tasks in queue. If queue is full it will create 11th thread
     * and will go till 15. Then will throw TaskRejected Exception.
     */
    executor.setCorePoolSize(threadCorePoolSize);
    executor.setMaxPoolSize(threadMaxPoolSize);
    executor.setQueueCapacity(threadQueueCapacity);
    executor.setThreadNamePrefix(threadNamePrefix);
    executor.initialize();
    return executor;
  }

  @Bean
  public MultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver();
  }

  @Bean
  public MimeTypeDetector mimeTypeDetector() {
    return new TikaMimeTypeDetector(new Tika());
  }
}

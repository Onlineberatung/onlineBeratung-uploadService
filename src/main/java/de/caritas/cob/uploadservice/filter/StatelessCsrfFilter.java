package de.caritas.cob.uploadservice.filter;

import static de.caritas.cob.uploadservice.config.SpringFoxConfig.WHITE_LIST;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This custom filter checks CSRF cookie and header token for equality
 */
public class StatelessCsrfFilter extends OncePerRequestFilter {

  private final RequestMatcher requireCsrfProtectionMatcher;
  private final AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();
  private final String csrfCookieProperty;
  private final String csrfHeaderProperty;

  public StatelessCsrfFilter(String cookieProperty, String headerProperty,
      String csrfWhitelistHeaderProperty) {
    this.csrfCookieProperty = cookieProperty;
    this.csrfHeaderProperty = headerProperty;
    this.requireCsrfProtectionMatcher = new DefaultRequiresCsrfMatcher(csrfWhitelistHeaderProperty);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (requireCsrfProtectionMatcher.matches(request)) {
      final String csrfTokenValue = request.getHeader(csrfHeaderProperty);
      final Cookie[] cookies = request.getCookies();

      String csrfCookieValue = null;
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (cookie.getName().equals(csrfCookieProperty)) {
            csrfCookieValue = cookie.getValue();
          }
        }
      }

      if (csrfTokenValue == null || !csrfTokenValue.equals(csrfCookieValue)) {
        accessDeniedHandler.handle(
            request, response, new AccessDeniedException("Missing or non-matching CSRF-token"));
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  @RequiredArgsConstructor
  public static final class DefaultRequiresCsrfMatcher implements RequestMatcher {

    private final Pattern allowedMethods = Pattern.compile("^(HEAD|TRACE|OPTIONS)$");
    private final @NonNull String csrfWhitelistHeaderProperty;

    @Override
    public boolean matches(HttpServletRequest request) {
      return !(isWhiteListUrl(request) || isWhiteListHeader(request) || isAllowedMehod(request));
    }

    private boolean isWhiteListUrl(HttpServletRequest request) {
      return Arrays.asList(WHITE_LIST).parallelStream()
          .anyMatch(request.getRequestURI().toLowerCase()::contains);
    }

    private boolean isWhiteListHeader(HttpServletRequest request) {
      return isNotBlank(request.getHeader(this.csrfWhitelistHeaderProperty));
    }

    private boolean isAllowedMehod(HttpServletRequest request) {
      return allowedMethods.matcher(request.getMethod()).matches();
    }
  }
}

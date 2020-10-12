package de.caritas.cob.uploadservice.api.service.helper;

import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.exception.RocketChatLoginException;
import de.caritas.cob.uploadservice.api.exception.RocketChatUserNotInitializedException;
import de.caritas.cob.uploadservice.api.model.rocket.chat.login.LoginResponseDto;
import de.caritas.cob.uploadservice.api.model.rocket.chat.logout.LogoutResponseDto;
import de.caritas.cob.uploadservice.api.service.LogService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RocketChatCredentialsHelper {

  @Value("${rocket.systemuser.username}")
  private String systemUsername;

  @Value("${rocket.systemuser.password}")
  private String systemPassword;

  @Value("${rocket.chat.api.user.login}")
  private String rocketChatApiUserLogin;

  @Value("${rocket.chat.api.user.logout}")
  private String rocketChatApiUserLogout;

  @Value("${rocket.chat.header.auth.token}")
  private String rocketChatHeaderAuthToken;

  @Value("${rocket.chat.header.user.id}")
  private String rocketChatHeaderUserId;

  @Autowired private RestTemplate restTemplate;

  // Tokens
  private RocketChatCredentials systemUser_A;
  private RocketChatCredentials systemUser_B;

  /**
   * Get a valid system user
   *
   * @return
   */
  public RocketChatCredentials getSystemUser() throws RocketChatUserNotInitializedException {
    // If both are uninitialized throw Exception
    if (systemUser_A == null && systemUser_B == null) {
      throw new RocketChatUserNotInitializedException("No system user was initialized");
    }

    if (systemUser_A == null) {
      return systemUser_B;
    }
    if (systemUser_B == null) {
      return systemUser_A;
    }

    if (systemUser_A.getTimeStampCreated().isAfter(systemUser_B.getTimeStampCreated())) {
      return systemUser_A;
    } else {
      return systemUser_B;
    }
  }

  /** Update the Credentials */
  public void updateCredentials() throws RocketChatLoginException {

    if (systemUser_A != null && systemUser_B != null) {
      if (systemUser_A.getTimeStampCreated().isBefore(systemUser_B.getTimeStampCreated())) {
        logoutUser(systemUser_A);
        systemUser_A = null;
      } else {
        logoutUser(systemUser_B);
        systemUser_B = null;
      }
    }

    if (systemUser_A == null && systemUser_B == null) {
      systemUser_A = loginUserServiceUser(systemUsername, systemPassword);
    } else {
      if (systemUser_A == null) {
        systemUser_A = loginUserServiceUser(systemUsername, systemPassword);
      }

      if (systemUser_B == null) {
        systemUser_B = loginUserServiceUser(systemUsername, systemPassword);
      }
    }
  }

  /**
   * Login a system user and receive a RocketChatCredentials-Object
   *
   * @param username
   * @param password
   * @return
   */
  public RocketChatCredentials loginUserServiceUser(String username, String password)
      throws RocketChatLoginException {

    RocketChatCredentials rcc =
        RocketChatCredentials.builder()
            .timeStampCreated(LocalDateTime.now())
            .rocketChatUsername(username)
            .build();

    try {

      ResponseEntity<LoginResponseDto> response = loginUser(username, password);

      rcc.setRocketChatToken(response.getBody().getData().getAuthToken());
      rcc.setRocketChatUserId(response.getBody().getData().getUserId());

    } catch (Exception ex) {
      LogService.logRocketChatServiceError(
          "Could not login " + username + " user in Rocket.Chat", ex);
      throw new RocketChatLoginException(ex);
    }

    if (rcc.getRocketChatToken() == null || rcc.getRocketChatUserId() == null) {
      String error =
          "Could not login "
              + username
              + " user in Rocket.Chat correctly, no authToken or UserId received.";
      LogService.logRocketChatServiceError(error);
      throw new RocketChatLoginException(error);
    }

    return rcc;
  }

  /**
   * Performs a login with the given credentials and returns the Result
   *
   * @param username
   * @param password
   * @return
   */
  public ResponseEntity<LoginResponseDto> loginUser(String username, String password)
      throws RocketChatLoginException {

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add("username", username);
      map.add("password", password);

      HttpEntity<MultiValueMap<String, String>> request =
          new HttpEntity<>(map, headers);

      return restTemplate.postForEntity(rocketChatApiUserLogin, request, LoginResponseDto.class);
    } catch (Exception ex) {
      LogService.logRocketChatServiceError(
          String.format("Could not login user (%s) in Rocket.Chat", username), ex);
      throw new RocketChatLoginException(ex);
    }
  }

  /**
   * Performs a logout with the given credentials and returns true on success.
   *
   * @param rcUserId
   * @param rcAuthToken
   * @return
   */
  public boolean logoutUser(String rcUserId, String rcAuthToken) {

    try {
      HttpHeaders headers = getStandardHttpHeaders(rcAuthToken, rcUserId);

      HttpEntity<Void> request = new HttpEntity<Void>(headers);

      ResponseEntity<LogoutResponseDto> response =
          restTemplate.postForEntity(rocketChatApiUserLogout, request, LogoutResponseDto.class);

      return response != null && response.getStatusCode() == HttpStatus.OK ? true : false;

    } catch (Exception ex) {
      LogService.logRocketChatServiceError(
          String.format("Could not log out user id (%s) from Rocket.Chat", rcUserId), ex);

      return false;
    }
  }

  /**
   * Logout a RocketChatCredentials-User
   *
   * @param user
   */
  private void logoutUser(RocketChatCredentials user) {
    this.logoutUser(user.getRocketChatUserId(), user.getRocketChatToken());
  }

  /**
   * Returns a HttpHeaders instance with standard settings (Rocket.Chat-Token, Rocket.Chat-User-ID,
   * MediaType)
   *
   * @param rcToken
   * @param rcUserId
   * @return a HttpHeaders instance with the standard settings
   */
  private HttpHeaders getStandardHttpHeaders(String rcToken, String rcUserId) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    httpHeaders.add(rocketChatHeaderAuthToken, rcToken);
    httpHeaders.add(rocketChatHeaderUserId, rcUserId);
    return httpHeaders;
  }
}

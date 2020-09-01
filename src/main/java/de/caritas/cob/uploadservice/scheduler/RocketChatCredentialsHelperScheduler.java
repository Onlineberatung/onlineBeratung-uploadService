package de.caritas.cob.uploadservice.scheduler;

import de.caritas.cob.uploadservice.api.exception.RocketChatLoginException;
import de.caritas.cob.uploadservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.helper.RocketChatCredentialsHelper;
import javax.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!testing")
public class RocketChatCredentialsHelperScheduler {

  private final @NonNull RocketChatCredentialsHelper rcCredentialsHelper;

  @PostConstruct
  public void postConstructInitializer() {
    LogService.logDebug("RocketChatCredentialsHelperScheduler - initialize tokens");
    try {
      rcCredentialsHelper.updateCredentials();
    } catch (RocketChatLoginException e) {
      throw new InternalServerErrorException(e, LogService::logInternalServerError);
    }
  }

  @Scheduled(cron = "${rocket.credentialscheduler.cron}")
  public void scheduledRotateToken() {
    LogService.logDebug("RocketChatCredentialsHelperScheduler - rotating tokens");
    try {
      rcCredentialsHelper.updateCredentials();
    } catch (RocketChatLoginException e) {
      throw new InternalServerErrorException(e, LogService::logInternalServerError);
    }
  }
}

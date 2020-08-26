package de.caritas.cob.uploadservice.scheduler;

import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.helper.RocketChatCredentialsHelper;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!testing")
public class RocketChatCredentialsHelperScheduler {

  @Autowired private RocketChatCredentialsHelper rcCredentialsHelper;

  @PostConstruct
  public void postConstructInitializer() {
    LogService.logDebug("RocketChatCredentialsHelperScheduler - initialize tokens");
    rcCredentialsHelper.updateCredentials();
  }

  @Scheduled(cron = "${rocket.credentialscheduler.cron}")
  public void scheduledRotateToken() {
    LogService.logDebug("RocketChatCredentialsHelperScheduler - rotating tokens");
    rcCredentialsHelper.updateCredentials();
  }
}

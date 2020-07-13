package de.caritas.cob.uploadservice.scheduler;

import de.caritas.cob.uploadservice.api.service.helper.RocketChatCredentialsHelper;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!testing")
public class RocketChatCredentialsHelperScheduler {

  @Autowired private RocketChatCredentialsHelper rcCredentialsHelper;

  @PostConstruct
  public void postConstructInitializer() {
    log.debug("RocketChatCredentialsHelperScheduler - initialize tokens");
    rcCredentialsHelper.updateCredentials();
  }

  @Scheduled(cron = "${rocket.credentialscheduler.cron}")
  public void scheduledRotateToken() {
    log.debug("RocketChatCredentialsHelperScheduler - rotating tokens");
    rcCredentialsHelper.updateCredentials();
  }
}

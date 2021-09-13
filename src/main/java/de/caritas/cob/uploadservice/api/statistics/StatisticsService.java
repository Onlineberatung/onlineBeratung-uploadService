package de.caritas.cob.uploadservice.api.statistics;

import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.statistics.event.StatisticsEvent;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Service class for the processing of statistical events. */
@Service
@RequiredArgsConstructor
public class StatisticsService {

  private final @NotNull AmqpTemplate amqpTemplate;

  @Value("${statistics.enabled}")
  private boolean statisticsEnabled;

  @Value("${statistics.rabbitmq.exchange.name}")
  private String rabbitMqExchangeName;

  /**
   * Entry point to write statistics event data to the statistics queue.
   *
   * @param statisticsEvent the concrete {@link StatisticsEvent}
   */
  @Async
  public void fireEvent(StatisticsEvent statisticsEvent) {

    if (statisticsEnabled) {
      statisticsEvent
          .getPayload()
          .ifPresentOrElse(
              payload ->
                  amqpTemplate.convertAndSend(
                      rabbitMqExchangeName, statisticsEvent.getEventType().toString(), payload),
              () ->
                  LogService.logStatisticsEventWarning(
                      String.format(
                          "Empty statistics event message payload for type %s received",
                          statisticsEvent.getClass().getSimpleName())));
    }
  }
}

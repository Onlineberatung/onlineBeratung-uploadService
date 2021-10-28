package de.caritas.cob.uploadservice.api.statistics.event;

import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.EventType;
import java.util.Optional;

/** Interface for statistics event. */
public interface StatisticsEvent {

  /**
   * Provides the payload for the statistics event message.
   *
   * @return the payload as {@link Optional}
   */
  Optional<String> getPayload();

  /**
   * The event type of the statistics event.
   *
   * @return the {@link EventType}
   */
  EventType getEventType();
}

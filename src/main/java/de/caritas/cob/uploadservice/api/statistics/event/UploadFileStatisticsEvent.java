package de.caritas.cob.uploadservice.api.statistics.event;

import de.caritas.cob.uploadservice.api.helper.CustomLocalDateTime;
import de.caritas.cob.uploadservice.api.helper.JsonHelper;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.EventType;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.UploadFileStatisticsEventMessage;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;



/** Upload file statistics event. */
@RequiredArgsConstructor
public class UploadFileStatisticsEvent implements StatisticsEvent {

  private static final EventType EVENT_TYPE = EventType.UPLOAD_FILE;
  private static final String TIMESTAMP = CustomLocalDateTime.nowAsFullQualifiedTimestamp();

  private @NonNull String consultantId;
  private @NonNull String rcGroupId;

  /** {@inheritDoc} */
  @Override
  public Optional<String> getPayload() {
    return JsonHelper.serialize(
        createUploadFileStatisticsEventMessage(), LogService::logInternalServerError);
  }

  /** {@inheritDoc} */
  @Override
  public EventType getEventType() {
    return EVENT_TYPE;
  }

  private de.caritas.cob.uploadservice.statisticsservice.generated.web.model
          .UploadFileStatisticsEventMessage
      createUploadFileStatisticsEventMessage() {
    return new UploadFileStatisticsEventMessage()
        .eventType(EVENT_TYPE)
        .consultantId(consultantId)
        .rcGroupId(rcGroupId)
        .timestamp(TIMESTAMP);
  }
}

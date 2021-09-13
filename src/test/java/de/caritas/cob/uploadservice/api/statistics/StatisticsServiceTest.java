package de.caritas.cob.uploadservice.api.statistics;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.statistics.event.UploadFileStatisticsEvent;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.EventType;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceTest {

  private static final String FIELD_NAME_STATISTICS_ENABLED = "statisticsEnabled";
  private static final String FIELD_NAME_RABBIT_EXCHANGE_NAME = "rabbitMqExchangeName";
  private static final String RABBIT_EXCHANGE_NAME = "exchange";
  private static final String PAYLOAD = "payload";
  @Mock Logger logger;
  private UploadFileStatisticsEvent uploadFileStatisticsEvent;
  private EventType eventType = EventType.ASSIGN_SESSION;
  @InjectMocks private StatisticsService statisticsService;
  @Mock private AmqpTemplate amqpTemplate;

  @Before
  public void setup() {
    uploadFileStatisticsEvent = Mockito.mock(UploadFileStatisticsEvent.class);
    when(uploadFileStatisticsEvent.getEventType()).thenReturn(eventType);
    when(uploadFileStatisticsEvent.getPayload()).thenReturn(Optional.of(PAYLOAD));
    setInternalState(LogService.class, "LOGGER", logger);
    setField(statisticsService, FIELD_NAME_RABBIT_EXCHANGE_NAME, RABBIT_EXCHANGE_NAME);
  }

  @Test
  public void fireEvent_Should_NotSendStatisticsMessage_WhenStatisticsIsDisabled() {

    setField(statisticsService, FIELD_NAME_STATISTICS_ENABLED, false);
    statisticsService.fireEvent(uploadFileStatisticsEvent);
    verify(amqpTemplate, times(0))
        .convertAndSend(eq(RABBIT_EXCHANGE_NAME), anyString(), anyString());
  }

  @Test
  public void fireEvent_Should_SendStatisticsMessage_WhenStatisticsIsEnabled() {

    setField(statisticsService, FIELD_NAME_STATISTICS_ENABLED, true);
    when(uploadFileStatisticsEvent.getEventType()).thenReturn(eventType);
    when(uploadFileStatisticsEvent.getPayload()).thenReturn(Optional.of(PAYLOAD));

    statisticsService.fireEvent(uploadFileStatisticsEvent);
    verify(amqpTemplate, times(1))
        .convertAndSend(eq(RABBIT_EXCHANGE_NAME), anyString(), anyString());
  }

  @Test
  public void fireEvent_Should_LogWarning_WhenPayloadIsEmpty() {

    setField(statisticsService, FIELD_NAME_STATISTICS_ENABLED, true);
    when(uploadFileStatisticsEvent.getPayload()).thenReturn(Optional.empty());
    statisticsService.fireEvent(uploadFileStatisticsEvent);
    verify(logger, times(1)).warn(anyString(), anyString());
  }

  @Test
  public void fireEvent_Should_UseEventTypeAsTopicAndSendPayloadOfEvent() {

    setField(statisticsService, FIELD_NAME_STATISTICS_ENABLED, true);
    statisticsService.fireEvent(uploadFileStatisticsEvent);
    verify(amqpTemplate, times(1))
        .convertAndSend(RABBIT_EXCHANGE_NAME, eventType.toString(), PAYLOAD);
  }
}

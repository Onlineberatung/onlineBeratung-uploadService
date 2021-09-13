package de.caritas.cob.uploadservice.api.statistics;

import static de.caritas.cob.uploadservice.helper.TestConstants.CONSULTANT_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.uploadservice.UploadServiceApplication;
import de.caritas.cob.uploadservice.api.statistics.event.UploadFileStatisticsEvent;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.EventType;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.UploadFileStatisticsEventMessage;
import de.caritas.cob.uploadservice.testconfig.RabbitMqTestConfig;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@SpringBootTest(classes = UploadServiceApplication.class)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class StatisticsServiceIT {

  private static final long MAX_TIMEOUT_MILLIS = 5000;
  private static final String TIMESTAMP_FIELD_NAME = "TIMESTAMP";

  @Autowired
  StatisticsService statisticsService;
  @Autowired
  AmqpTemplate amqpTemplate;

  @Test
  public void fireEvent_Should_Send_ExpectedUploadFileStatisticsEventMessageToQueue()
      throws IOException {

    UploadFileStatisticsEvent uploadFileStatisticsEvent =
        new UploadFileStatisticsEvent(CONSULTANT_ID, RC_ROOM_ID);
    String staticTimestamp =
        Objects.requireNonNull(
                ReflectionTestUtils.getField(
                    uploadFileStatisticsEvent,
                    UploadFileStatisticsEvent.class,
                    TIMESTAMP_FIELD_NAME))
            .toString();
    UploadFileStatisticsEventMessage uploadFileStatisticsEventMessage =
        new UploadFileStatisticsEventMessage()
            .eventType(EventType.UPLOAD_FILE)
            .consultantId(CONSULTANT_ID)
            .rcGroupId(RC_ROOM_ID)
            .timestamp(staticTimestamp);

    statisticsService.fireEvent(uploadFileStatisticsEvent);
    Message message =
        amqpTemplate.receive(RabbitMqTestConfig.QUEUE_NAME_ASSIGN_SESSION, MAX_TIMEOUT_MILLIS);
    assert message != null;
    assertThat(
        extractBodyFromAmpQMessage(message),
        jsonEquals(new ObjectMapper().writeValueAsString(uploadFileStatisticsEventMessage)));
  }

  private String extractBodyFromAmpQMessage(Message message) throws IOException {
    return IOUtils.toString(message.getBody(), UTF_8);
  }

}

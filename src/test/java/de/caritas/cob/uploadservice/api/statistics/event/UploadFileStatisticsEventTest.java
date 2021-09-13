package de.caritas.cob.uploadservice.api.statistics.event;

import static de.caritas.cob.uploadservice.helper.TestConstants.CONSULTANT_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.EventType;
import java.util.Objects;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class UploadFileStatisticsEventTest {

  private static final String TIMESTAMP_FIELD_NAME = "TIMESTAMP";
  private UploadFileStatisticsEvent uploadFileStatisticsEvent;
  private String staticTimestamp;

  @Before
  public void setup() throws NoSuchFieldException, IllegalAccessException {
    uploadFileStatisticsEvent = new UploadFileStatisticsEvent(CONSULTANT_ID, RC_ROOM_ID);
    staticTimestamp =
        Objects.requireNonNull(
                ReflectionTestUtils.getField(
                    uploadFileStatisticsEvent,
                    UploadFileStatisticsEvent.class,
                    TIMESTAMP_FIELD_NAME))
            .toString();
  }

  @Test
  public void getEventType_Should_ReturnEventTypeCreateMessage() {

    assertThat(this.uploadFileStatisticsEvent.getEventType(), is(EventType.UPLOAD_FILE));
  }

  @Test
  public void getPayload_Should_ReturnValidJsonPayload() {

    String expectedJson =
        "{"
            + "  \"rcGroupId\":\""
            + RC_ROOM_ID
            + "\","
            + "  \"consultantId\":\""
            + CONSULTANT_ID
            + "\","
            + "  \"timestamp\":\""
            + staticTimestamp
            + "\","
            + "  \"eventType\":\""
            + EventType.UPLOAD_FILE
            + "\""
            + "}";

    Optional<String> result = uploadFileStatisticsEvent.getPayload();

    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), jsonEquals(expectedJson));
  }
}

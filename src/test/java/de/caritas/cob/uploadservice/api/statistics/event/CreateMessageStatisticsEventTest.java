package de.caritas.cob.uploadservice.api.statistics.event;

import static de.caritas.cob.uploadservice.helper.TestConstants.CONSULTANT_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import de.caritas.cob.uploadservice.api.helper.CustomOffsetDateTime;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.EventType;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class CreateMessageStatisticsEventTest {

  private CreateMessageStatisticsEvent createMessageStatisticsEvent;

  @Before
  public void setup() throws NoSuchFieldException, IllegalAccessException {
    createMessageStatisticsEvent = new CreateMessageStatisticsEvent(CONSULTANT_ID, RC_ROOM_ID, true);
  }

  @Test
  public void getEventType_Should_ReturnEventTypeCreateMessage() {

    assertThat(this.createMessageStatisticsEvent.getEventType(), is(EventType.CREATE_MESSAGE));
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
            + CustomOffsetDateTime.nowInUtc()
            + "\","
            + "  \"eventType\":\""
            + EventType.CREATE_MESSAGE
            + "\","
            + "  \"hasAttachment\": true"
            + "}";

    Optional<String> result = createMessageStatisticsEvent.getPayload();

    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), jsonEquals(expectedJson).whenIgnoringPaths("timestamp"));
  }
}

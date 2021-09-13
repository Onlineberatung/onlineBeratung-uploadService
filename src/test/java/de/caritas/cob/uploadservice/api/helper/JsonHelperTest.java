package de.caritas.cob.uploadservice.api.helper;

import static de.caritas.cob.uploadservice.helper.TestConstants.CONSULTANT_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.CreateMessageStatisticsEventMessage;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.EventType;
import java.util.Optional;
import org.junit.Test;
import org.mockito.Mockito;

public class JsonHelperTest {

  @Test
  public void serialize_Should_returnOptionalWithSerializedObject() throws JsonProcessingException {

    CreateMessageStatisticsEventMessage createMessageStatisticsEventMessage =
        new CreateMessageStatisticsEventMessage()
            .eventType(EventType.CREATE_MESSAGE)
            .rcGroupId(RC_ROOM_ID)
            .consultantId(CONSULTANT_ID)
            .hasAttachment(true)
            .timestamp(CustomLocalDateTime.nowAsFullQualifiedTimestamp());

    Optional<String> result =
        JsonHelper.serialize(createMessageStatisticsEventMessage, LogService::logStatisticsEventError);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), jsonEquals(new ObjectMapper().writeValueAsString(createMessageStatisticsEventMessage)));

  }

  @Test
  public void serialize_Should_returnOptionalEmpty_When_jsonStringCanNotBeConverted()
      throws JsonProcessingException {

    ObjectMapper om = Mockito.spy(new ObjectMapper());
    Mockito.when(om.writeValueAsString(Object.class)).thenThrow(new JsonProcessingException("") {});

    Optional<String> result =
        JsonHelper.serialize(new Object(), LogService::logInternalServerError);

    assertThat(result.isPresent(), is(false));
  }

}

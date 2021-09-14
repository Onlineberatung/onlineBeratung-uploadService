package de.caritas.cob.uploadservice.api.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.caritas.cob.uploadservice.api.helper.json.OffsetDateTimeToStringSerializer;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Consumer;

/** Helper class for JSON specific tasks. */
public class JsonHelper {

  private JsonHelper() {}

  /**
   * Serialize a object with specific json serializers.
   *
   * @param object an object to serialize
   * @param loggingMethod the method being used to log errors
   * @return {@link Optional} of serialized object as {@link String}
   */
  public static Optional<String> serializeWithOffsetDateTimeAsString(
      Object object, Consumer<Exception> loggingMethod) {
    try {
      return Optional.of(buildObjectMapper().writeValueAsString(object));
    } catch (JsonProcessingException jsonProcessingException) {
      loggingMethod.accept(jsonProcessingException);
    }
    return Optional.empty();
  }

  private static ObjectMapper buildObjectMapper() {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(buildSimpleModule());
    return objectMapper;
  }

  private static SimpleModule buildSimpleModule() {
    return new SimpleModule()
        .addSerializer(OffsetDateTime.class, new OffsetDateTimeToStringSerializer());
  }
}

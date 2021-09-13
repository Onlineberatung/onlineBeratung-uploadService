package de.caritas.cob.uploadservice.api.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.function.Consumer;

/** Helper class for JSON specific tasks. */
public class JsonHelper {

  private JsonHelper() {}

  /**
   * Serialize a object.
   *
   * @param o an object to serialize
   * @param loggingMethod the method being used to log errors
   * @return {@link Optional} of serialized object as {@link String}
   */
  public static Optional<String> serialize(Object o, Consumer<Exception> loggingMethod) {
    try {
      return Optional.of(new ObjectMapper().writeValueAsString(o));
    } catch (JsonProcessingException jsonProcessingException) {
      loggingMethod.accept(jsonProcessingException);
    }
    return Optional.empty();
  }
}

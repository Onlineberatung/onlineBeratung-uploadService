package de.caritas.cob.uploadservice.api.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.uploadservice.api.service.LogService;
import java.io.IOException;

/**
 * Helper to convert a JSON string to an object
 *
 * @param <T> generic Type
 */
public class JsonStringToObjectConverter<T> {

  private LogService logService;

  public JsonStringToObjectConverter() {
    this.logService = new LogService();
  }

  /**
   * Convert a JSON string to an object
   *
   * @param jsonAsString JSON as String
   * @param classType Class type
   * @return if success, instance of classType or null, otherwise null
   */
  public T convert(String jsonAsString, Class<T> classType) {
    ObjectMapper objectMapper = new ObjectMapper();
    T jsonObject = null;
    try {
      jsonObject = objectMapper.readValue(jsonAsString, classType);
    } catch (IOException | NullPointerException exception) {
      logService.logInternalServerError(
          String.format(
              "Error while converting JSON string to generic object type %s", classType.toString()),
          exception);
    }
    return jsonObject;
  }
}

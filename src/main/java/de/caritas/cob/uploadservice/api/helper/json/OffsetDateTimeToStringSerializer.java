package de.caritas.cob.uploadservice.api.helper.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/** Special {@link OffsetDateTime} serializer with ISO_DATE_TIME format. */
public class OffsetDateTimeToStringSerializer extends JsonSerializer<OffsetDateTime> {

  @Override
  public void serialize(
      OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeObject(offsetDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
  }
}

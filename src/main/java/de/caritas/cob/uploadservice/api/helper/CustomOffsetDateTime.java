package de.caritas.cob.uploadservice.api.helper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/** Local offset date time class providing now with zone offset utc. */
public class CustomOffsetDateTime {

  private CustomOffsetDateTime() {}

  /**
   * Creates a current {@link OffsetDateTime} instance with {@link ZoneOffset} utc.
   *
   * @return the {@link OffsetDateTime} instance
   */
  public static OffsetDateTime nowInUtc() {
    return OffsetDateTime.now(ZoneOffset.UTC);
  }

}

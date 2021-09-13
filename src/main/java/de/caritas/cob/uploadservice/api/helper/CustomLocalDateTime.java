package de.caritas.cob.uploadservice.api.helper;

import static java.util.Objects.nonNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/** Local date time class providing now and epoch seconds with zone offset utc. */
public class CustomLocalDateTime {

  private static final String ISO_DATE_FORMAT = "uuuu-MM-dd'T'HH:mm:ssX";
  private static final String FULL_QUALIFIED_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  private CustomLocalDateTime() {}

  /**
   * Creates a current {@link LocalDateTime} instance with {@link ZoneOffset} utc.
   *
   * @return the {@link LocalDateTime} instance
   */
  public static LocalDateTime nowInUtc() {
    return LocalDateTime.now(ZoneOffset.UTC);
  }

  /**
   * Converts the given {@link LocalDateTime} to unix time.
   *
   * @param localDateTime {@link LocalDateTime}
   * @return unix time
   */
  public static long toUnixTime(LocalDateTime localDateTime) {
    return nonNull(localDateTime)
        ? LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC)
        : 0;
  }

  /**
   * Converts the given {@link LocalDateTime} to ISO 8601 string.
   *
   * @param localDateTime {@link LocalDateTime}
   * @return converted time in ISO 8601
   */
  public static String toIsoTime(LocalDateTime localDateTime) {
    return localDateTime
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern(ISO_DATE_FORMAT));
  }

  /**
   * Converts the given {@link LocalDateTime} string to {@link Date}.
   *
   * @param localDateTime the local date time as string
   * @return converted time in {@link Date}
   */
  public static Date toDate(String localDateTime) {
    return Date.from(
        LocalDateTime.parse(localDateTime.replace("Z", "")).atOffset(ZoneOffset.UTC).toInstant());
  }

  /**
   * Creates a full qualified timestamp.
   *
   * @return Full qualified timestamp
   */
  public static String nowAsFullQualifiedTimestamp() {
    return LocalDateTime.now(ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern(FULL_QUALIFIED_TIMESTAMP_FORMAT));
  }
}

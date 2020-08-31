package de.caritas.cob.uploadservice.api.helper;

import de.caritas.cob.uploadservice.api.exception.HelperException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserHelper {

  private static final String BASE32_PLACEHOLDER = "=";
  private static final String BASE32_PLACEHOLDER_REPLACE_STRING = ".";
  private static final String ENCODING_PREFIX = "enc.";
  private Base32 base32 = new Base32();

  /**
   * Returns the Base32 encoded username. The padding char "=" of the Base32 String will be replaced
   * by a dot "." to support Rocket.Chat special chars.
   *
   * @param username
   * @return encoded username
   */
  private String base32EncodeUsername(String username) {
    try {
      return ENCODING_PREFIX
          + base32
              .encodeAsString(username.getBytes())
              .replace(BASE32_PLACEHOLDER, BASE32_PLACEHOLDER_REPLACE_STRING);
    } catch (Exception exception) {
      // Catch generic exception because of lack of base32 documentation
      throw new HelperException(String.format("Could not encode username %s", username), exception);
    }
  }

  /**
   * Returns the Base32 decoded username. Placeholder dot "." (to support Rocket.Chat special chars)
   * will be replaced by the Base32 padding symbol "=".
   *
   * @param username
   * @return
   */
  private String base32DecodeUsername(String username) {
    try {
      return new String(
          base32.decode(
              username
                  .replace(ENCODING_PREFIX, StringUtils.EMPTY)
                  .toUpperCase()
                  .replace(BASE32_PLACEHOLDER_REPLACE_STRING, BASE32_PLACEHOLDER)));
    } catch (Exception exception) {
      // Catch generic exception because of lack of base32 documentation
      throw new HelperException(String.format("Could not decode username %s", username), exception);
    }
  }

  /**
   * Encodes the given username if it isn't already encoded
   *
   * @param username
   * @return encoded username
   */
  public String encodeUsername(String username) {
    return username.startsWith(ENCODING_PREFIX) ? username : base32EncodeUsername(username);
  }

  /**
   * Descodes the given username if it isn't already decoded
   *
   * @param username
   * @return
   */
  public String decodeUsername(String username) {
    return username.startsWith(ENCODING_PREFIX) ? base32DecodeUsername(username) : username;
  }
}

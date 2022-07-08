package de.caritas.cob.uploadservice.api.service.helper;

import static org.apache.commons.lang3.Validate.notNull;


public class HttpUrlUtils {

  private HttpUrlUtils() {}

  public static String removeHttpPrefix(String site) {
    notNull(site);
    return site.replace("https://", "").replace("http://", "");
  }
}

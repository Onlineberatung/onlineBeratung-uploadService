package de.caritas.cob.uploadservice.media;

import static java.util.Objects.isNull;

import java.io.InputStream;
import java.util.Optional;
import org.apache.tika.Tika;

public class TikaMimeTypeDetector implements MimeTypeDetector {

  private final Tika tika;

  public TikaMimeTypeDetector(Tika tika) {
    this.tika = tika;
  }

  @Override
  public Optional<String> detect(InputStream input) {
    if (isNull(input)) {
      return Optional.empty();
    }
    try {
      return Optional.ofNullable(tika.detect(input));
    } catch (Exception e) {
      throw new RuntimeException("failed to detect media type from input stream", e);
    }
  }
}

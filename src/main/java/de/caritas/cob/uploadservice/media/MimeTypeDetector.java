package de.caritas.cob.uploadservice.media;

import java.io.InputStream;
import java.util.Optional;

public interface MimeTypeDetector {

  Optional<String> detect(InputStream input);

}

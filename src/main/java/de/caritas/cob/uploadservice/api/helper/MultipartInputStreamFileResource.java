package de.caritas.cob.uploadservice.api.helper;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.InputStreamResource;

public class MultipartInputStreamFileResource extends InputStreamResource {

  private final String filename;

  /**
   * Create a new MultipartInputStreamFileResource
   *
   * @param inputStream The {@link InputStream}
   * @param filename The filename
   */
  public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
    super(inputStream);
    this.filename = filename;
  }

  /** {@inheritDoc } */
  @Override
  public String getFilename() {
    return this.filename;
  }

  /** {@inheritDoc } */
  @Override
  public long contentLength() throws IOException {
    return -1; // we do not want to generally read the whole stream into memory ...
  }
}

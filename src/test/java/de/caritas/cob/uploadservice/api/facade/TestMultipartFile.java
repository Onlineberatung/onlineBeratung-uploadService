package de.caritas.cob.uploadservice.api.facade;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public class TestMultipartFile implements MultipartFile {

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getOriginalFilename() {
    return null;
  }

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public long getSize() {
    return 0;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return new byte[0];
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(getBytes());
  }

  @Override
  public void transferTo(File file) throws IOException, IllegalStateException {

  }
}

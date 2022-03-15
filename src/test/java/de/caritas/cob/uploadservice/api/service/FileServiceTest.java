package de.caritas.cob.uploadservice.api.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.caritas.cob.uploadservice.api.exception.InvalidFileTypeException;
import de.caritas.cob.uploadservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.uploadservice.api.facade.TestMultipartFile;
import de.caritas.cob.uploadservice.media.MimeTypeDetector;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class FileServiceTest {

  private MimeTypeDetector mimeTypeDetector;
  private FileService fileService;

  @Before
  public void setUp() throws Exception {
    mimeTypeDetector = mock(MimeTypeDetector.class);
    fileService = new FileService(mimeTypeDetector, Set.of("application/jpeg"));
  }

  @Test
  public void verifyMimeType_should_verify_that_mime_type_is_whitelisted() {
    when(mimeTypeDetector.detect(any())).thenReturn(Optional.of("application/jpeg"));

    fileService.verifyMimeType(new TestMultipartFile());
  }

  @Test(expected = InvalidFileTypeException.class)
  public void verifyMimeType_should_fail_when_mime_type_of_file_is_not_whitelisted() {
    when(mimeTypeDetector.detect(any())).thenReturn(Optional.of("application/octet-stream"));

    fileService.verifyMimeType(new TestMultipartFile());
  }

  @Test(expected = InvalidFileTypeException.class)
  public void verifyMimeType_should_fail_when_mime_type_is_empty() {
    when(mimeTypeDetector.detect(any())).thenReturn(Optional.empty());

    fileService.verifyMimeType(new TestMultipartFile());
  }

  @Test(expected = InternalServerErrorException.class)
  public void verifyMimeType_should_fail_if_input_reading_fails() {
    doThrow(RuntimeException.class).when(mimeTypeDetector).detect(any());

    fileService.verifyMimeType(new TestMultipartFile());
  }

  @Test
  public void verifyMimeType_should_do_nothing_if_file_is_null() {
    fileService.verifyMimeType(null);
  }
}
package de.caritas.cob.uploadservice.api.helper;

import static de.caritas.cob.uploadservice.helper.TestConstants.FILE_NAME_DEFAULT;
import static de.caritas.cob.uploadservice.helper.TestConstants.FILE_NAME_ONLY_SPECIAL_CHARS;
import static de.caritas.cob.uploadservice.helper.TestConstants.FILE_NAME_SANITIZED;
import static de.caritas.cob.uploadservice.helper.TestConstants.FILE_NAME_SANITIZED_WITH_SPACES;
import static de.caritas.cob.uploadservice.helper.TestConstants.FILE_NAME_UNSANITIZED;
import static de.caritas.cob.uploadservice.helper.TestConstants.FILE_NAME_UNSANITIZED_WITH_SPACES;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileSanitizerTest {

  @Test
  public void sanitizeFileName_Should_ReturnSanitizedFileName() {
    assertEquals(FILE_NAME_SANITIZED, FileSanitizer.sanitizeFileName(FILE_NAME_UNSANITIZED));
  }

  @Test
  public void sanitizeFileName_Should_RemoveLeadingAndTrailingSpacesFromFilename() {
    assertEquals(FILE_NAME_SANITIZED_WITH_SPACES,
        FileSanitizer.sanitizeFileName(FILE_NAME_UNSANITIZED_WITH_SPACES));
  }

  @Test
  public void sanitizeFileName_Should_ReturnDefaultFileName_When_SanitationRemovesAllChars() {
    assertEquals(FILE_NAME_DEFAULT, FileSanitizer.sanitizeFileName(FILE_NAME_ONLY_SPECIAL_CHARS));
  }

  @Test
  public void sanitizeFileName_Should_ReturnNull_When_ProvidedFileNameIsNull() {
    assertEquals(null, FileSanitizer.sanitizeFileName(null));
  }

  @Test
  public void sanitizeFileName_Should_ReturnNull_When_ProvidedFileNameIsEmpty() {
    assertEquals(null, FileSanitizer.sanitizeFileName(""));
  }
}

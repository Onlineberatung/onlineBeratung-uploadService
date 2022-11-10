package de.caritas.cob.uploadservice.api.service;

import static java.util.Objects.isNull;

import de.caritas.cob.uploadservice.api.exception.InvalidFileTypeException;
import de.caritas.cob.uploadservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.uploadservice.media.MimeTypeDetector;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileService {

  private final MimeTypeDetector mimeTypeDetector;
  @Value("#{${mime-type-whitelist}}")
  private final Set<String> mimeTypeWhitelist;

  public void verifyMimeType(MultipartFile multipartFile) {
    if (isNull(multipartFile)) {
      return;
    }

    Optional<String> mimeType;
    try {
      mimeType = mimeTypeDetector.detect(multipartFile.getInputStream());
    } catch (Exception e) {
      throw new InternalServerErrorException(
          "failed to detect mime type of file " + multipartFile.getOriginalFilename(), e,
          LogService::logInternalServerError);
    }
    if (mimeType.isEmpty() || !mimeTypeWhitelist.contains(mimeType.get())) {
      throw new InvalidFileTypeException(
          "invalid mime type " + mimeType + " of file " + multipartFile.getOriginalFilename());
    }
  }

  public void verifyFileHeaderMimeType(InputStream fileHeaderInputStream) {
    if (isNull(fileHeaderInputStream)) {
      return;
    }

    Optional<String> mimeType;
    try {
      mimeType = mimeTypeDetector.detect(fileHeaderInputStream);
    } catch (Exception e) {
      throw new InternalServerErrorException(
          "failed to detect mime type of fileHeader " + fileHeaderInputStream, e,
          LogService::logInternalServerError);
    }
    if (mimeType.isEmpty() || !mimeTypeWhitelist.contains(mimeType.get())) {
      throw new InvalidFileTypeException(
          "invalid mime type " + mimeType + " of fileHeader " + fileHeaderInputStream);
    }
  }
}

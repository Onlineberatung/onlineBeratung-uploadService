package de.caritas.cob.uploadservice.api.service;

import de.caritas.cob.uploadservice.api.exception.httpresponses.QuotaReachedException;
import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.api.model.UploadByUser;
import de.caritas.cob.uploadservice.api.repository.UploadByUserRepository;
import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service to track uploaded files per user and validate against configurated limit.
 */
@Service
@RequiredArgsConstructor
public class UploadTrackingService {

  @Value("${upload.file.perday.limit}")
  private int uploadLimit;

  private final @NonNull UploadByUserRepository uploadByUserRepository;
  private final @NonNull AuthenticatedUser authenticatedUser;

  /**
   * Validates the upload limit of files for given user and throws a
   * {@link QuotaReachedException} if limit for day is reached.
   */
  public void validateUploadLimit() {
    String userId = this.authenticatedUser.getUserId();
    Integer uploadCount = this.uploadByUserRepository.countAllByUserId(userId);
    if (uploadCount >= this.uploadLimit) {
      throw new QuotaReachedException(LogService::logWarning);
    }
  }

  /**
   * Stores an {@link UploadByUser} entry for given user.
   */
  public void trackUploadedFileForUser() {
    String userId = this.authenticatedUser.getUserId();
    UploadByUser uploadByUser = UploadByUser.builder()
        .userId(userId)
        .createDate(LocalDateTime.now())
        .build();

    this.uploadByUserRepository.save(uploadByUser);
  }

  /**
   * Cleans all stored file limits.
   */
  @Scheduled(cron = "${upload.file.cleanup.cron}")
  public void cleanUpFileLimits() {
    this.uploadByUserRepository.deleteAll();
    LogService.logInfo("File restrictions are reset!");
  }

}

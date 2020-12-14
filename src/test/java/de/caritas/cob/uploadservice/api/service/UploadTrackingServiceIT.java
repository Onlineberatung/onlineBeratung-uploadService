package de.caritas.cob.uploadservice.api.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import de.caritas.cob.uploadservice.UploadServiceApplication;
import de.caritas.cob.uploadservice.api.exception.httpresponses.QuotaReachedException;
import de.caritas.cob.uploadservice.api.helper.AuthenticatedUser;
import de.caritas.cob.uploadservice.api.model.UploadByUser;
import de.caritas.cob.uploadservice.api.repository.UploadByUserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UploadServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class UploadTrackingServiceIT {

  @Autowired
  private UploadTrackingService uploadTrackingService;

  @Autowired
  private UploadByUserRepository uploadByUserRepository;

  @MockBean
  private AuthenticatedUser authenticatedUser;

  @Mock
  private Logger logger;

  @Before
  public void setup() {
    when(this.authenticatedUser.getUserId()).thenReturn("userId");
    setInternalState(LogService.class, "LOGGER", logger);
  }

  @After
  public void cleanDatabase() {
    this.uploadByUserRepository.deleteAll();
  }

  @Test
  public void trackUploadedFileForUser_Should_persistTrackingEntry() {
    trackUploadedFile(1);

    assertThat(this.uploadByUserRepository.count(), is(1L));
    UploadByUser persistedEntry = this.uploadByUserRepository.findAll().iterator().next();
    assertThat(persistedEntry.getId(), notNullValue());
    assertThat(persistedEntry.getUserId(), is("userId"));
    assertThat(persistedEntry.getCreateDate(), notNullValue());
  }

  private void trackUploadedFile(Integer amount) {
    for (int i = 0; i < amount; i++) {
      this.uploadTrackingService.trackUploadedFileForUser();
    }
  }

  @Test
  public void trackUploadedFileForUser_Should_persistTrackingEntries_When_trackMultipleTimes() {
    trackUploadedFile(10);

    assertThat(this.uploadByUserRepository.count(), is(10L));
    assertThat(this.uploadByUserRepository.countAllByUserId("userId"), is(10));
  }

  @Test
  public void validateUploadLimit_Should_notThrowQuotaException_When_limitIsNotReached() {
    trackUploadedFile(5);

    try {
      this.uploadTrackingService.validateUploadLimit();
    } catch (QuotaReachedException e) {
      fail("Exception should not be thrown");
    }
  }

  @Test(expected = QuotaReachedException.class)
  public void validateUploadLimit_Should_throwQuotaReachedException_When_limitIsReached() {
    trackUploadedFile(7);

    this.uploadTrackingService.validateUploadLimit();
  }

  @Test
  public void cleanUpFileLimits_Should_deleteAllTrackingEntries() {
    trackUploadedFile(10);

    assertThat(this.uploadByUserRepository.count(), is(10L));

    this.uploadTrackingService.cleanUpFileLimits();

    assertThat(this.uploadByUserRepository.count(), is(0L));
  }

  @Test
  public void cleanUpFileLimits_Should_logExpectedInfoMessage() {
    this.uploadTrackingService.cleanUpFileLimits();

    verify(this.logger, times(1)).info(eq("File restrictions are reset!"));
  }

}

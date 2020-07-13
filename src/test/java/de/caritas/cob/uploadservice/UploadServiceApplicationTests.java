package de.caritas.cob.uploadservice;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UploadServiceApplicationTests {

  @MockBean UploadServiceApplication uploadServiceApplication;

  @Test
  public void getAuthenticatedUser_Should_ReturnNullWhenNoUserSessionActive() {
    assertNull(uploadServiceApplication.getAuthenticatedUser());
  }
}

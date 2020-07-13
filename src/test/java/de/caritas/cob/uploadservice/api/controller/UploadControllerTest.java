package de.caritas.cob.uploadservice.api.controller;

import static de.caritas.cob.uploadservice.helper.MethodAndParameterConstants.UPLOAD_FILE_TO_FEEDBACK_ROOM_METHOD_NAME;
import static de.caritas.cob.uploadservice.helper.MethodAndParameterConstants.UPLOAD_FILE_TO_FEEDBACK_ROOM_METHOD_PARAMS;
import static de.caritas.cob.uploadservice.helper.MethodAndParameterConstants.UPLOAD_FILE_TO_ROOM_METHOD_NAME;
import static de.caritas.cob.uploadservice.helper.MethodAndParameterConstants.UPLOAD_FILE_TO_ROOM_METHOD_PARAMS;
import static org.junit.Assert.assertNotNull;

import de.caritas.cob.uploadservice.api.aspect.TempCleanup;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UploadControllerTest {

  @Test
  public void test_Should_Fail_WhenMethodUploadFileToRoomDoesNotHaveTempCleanupAnnotation()
      throws NoSuchMethodException {

    UploadController uploadController = new UploadController();
    Class classToTest = uploadController.getClass();
    Method methodToTest =
        classToTest.getMethod(UPLOAD_FILE_TO_ROOM_METHOD_NAME, UPLOAD_FILE_TO_ROOM_METHOD_PARAMS);
    TempCleanup annotation = methodToTest.getAnnotation(TempCleanup.class);

    assertNotNull(annotation);
  }

  @Test
  public void test_Should_Fail_WhenMethodUploadFileToFeedbackRoomDoesNotHaveTempCleanupAnnotation()
      throws NoSuchMethodException {

    UploadController uploadController = new UploadController();
    Class classToTest = uploadController.getClass();
    Method methodToTest =
        classToTest.getMethod(
            UPLOAD_FILE_TO_FEEDBACK_ROOM_METHOD_NAME, UPLOAD_FILE_TO_FEEDBACK_ROOM_METHOD_PARAMS);
    TempCleanup annotation = methodToTest.getAnnotation(TempCleanup.class);

    assertNotNull(annotation);
  }
}

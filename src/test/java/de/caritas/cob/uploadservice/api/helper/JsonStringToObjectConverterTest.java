package de.caritas.cob.uploadservice.api.helper;

import static de.caritas.cob.uploadservice.helper.TestConstants.FIELD_NAME_LOGSERVICE;
import static de.caritas.cob.uploadservice.helper.TestConstants.INVALID_JSON_BODY;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_RESPONSE_BODY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.caritas.cob.uploadservice.api.model.rocket.chat.UploadResponseDto;
import de.caritas.cob.uploadservice.api.service.LogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JsonStringToObjectConverterTest {

  @Mock private LogService logService;

  @Test
  public void convert_Should_ReturnCorrectType() {

    Object result =
        new JsonStringToObjectConverter<UploadResponseDto>()
            .convert(RC_UPLOAD_ERROR_RESPONSE_BODY, UploadResponseDto.class);
    assertTrue(result instanceof UploadResponseDto);
  }

  @Test
  public void convert_Should_ReturnInitializedObjectWithCorrectValues() {

    UploadResponseDto result =
        new JsonStringToObjectConverter<UploadResponseDto>()
            .convert(RC_UPLOAD_ERROR_RESPONSE_BODY, UploadResponseDto.class);
    assertEquals(false, result.isSuccess());
    assertEquals("Invalid room [error-invalid-room]", result.getError());
    assertEquals("error-invalid-room", result.getErrorType());
  }

  @Test
  public void convert_Should_ReturnNullOnError() {

    UploadResponseDto result =
        new JsonStringToObjectConverter<UploadResponseDto>()
            .convert(INVALID_JSON_BODY, UploadResponseDto.class);
    assertNull(result);
  }

  @Test
  public void convert_Should_LogOnError() throws NoSuchFieldException, SecurityException {

    JsonStringToObjectConverter<UploadResponseDto> jsonStringToObjectConverter =
        new JsonStringToObjectConverter<UploadResponseDto>();

    FieldSetter.setField(
        jsonStringToObjectConverter,
        jsonStringToObjectConverter.getClass().getDeclaredField(FIELD_NAME_LOGSERVICE),
        logService);

    jsonStringToObjectConverter.convert(INVALID_JSON_BODY, UploadResponseDto.class);

    verify(logService, times(1))
        .logInternalServerError(Mockito.anyString(), Mockito.any(Exception.class));
  }
}

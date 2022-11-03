package de.caritas.cob.uploadservice.api.helper;

import static de.caritas.cob.uploadservice.helper.TestConstants.INVALID_JSON_BODY;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_UPLOAD_ERROR_RESPONSE_BODY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.reflect.Whitebox.setInternalState;

import de.caritas.cob.uploadservice.api.model.rocket.chat.UploadResponseDto;
import de.caritas.cob.uploadservice.api.service.LogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class JsonStringToObjectConverterTest {

  @Mock private Logger logger;

  @Before
  public void setup() {
    setInternalState(LogService.class, "LOGGER", logger);
  }

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
        new JsonStringToObjectConverter<>();

    jsonStringToObjectConverter.convert(INVALID_JSON_BODY, UploadResponseDto.class);

    verify(logger, times(1)).error(anyString(), anyString(), anyString());
  }
}

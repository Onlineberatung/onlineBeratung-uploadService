package de.caritas.cob.uploadservice.api.facade;

import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.helper.RocketChatUploadParameterEncrypter;
import de.caritas.cob.uploadservice.api.helper.RocketChatUploadParameterSanitizer;
import de.caritas.cob.uploadservice.api.service.RocketChatService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class UploadFacadeTest {

  @Mock RocketChatService rocketChatService;
  @Mock EmailNotificationFacade emailNotificationFacade;
  @Mock RocketChatUploadParameterSanitizer rocketChatUploadParameterSanitizer;
  @Mock RocketChatUploadParameterEncrypter rocketChatUploadParameterEncrypter;
  @InjectMocks UploadFacade uploadFacade;

  @Mock RocketChatCredentials rocketChatCredentials;
  @Mock RocketChatUploadParameter rocketChatUploadParameter;

  /** Method: uploadFileToRoom */
  @Before
  public void setup() {
    when(rocketChatUploadParameter.getRoomId()).thenReturn(RC_ROOM_ID);
    when(rocketChatUploadParameterSanitizer.sanitize(rocketChatUploadParameter))
        .thenReturn(rocketChatUploadParameter);
    when(rocketChatUploadParameterEncrypter.encrypt(rocketChatUploadParameter))
        .thenReturn(rocketChatUploadParameter);
  }

  @Test
  public void uploadFileToRoom_Should_ReturnHttpStatusCreated_WhenNoExceptionIsThrown() {

    HttpStatus result =
        uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    assertEquals(HttpStatus.CREATED, result);
  }

  @Test
  public void uploadFileToRoom_Should_sendEmailNotification_WhenParamIsTrue() {

    HttpStatus result =
        uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, true);

    verify(emailNotificationFacade, times(1)).sendEmailNotification(RC_ROOM_ID);
  }

  @Test
  public void uploadFileToRoom_Should_markGroupAsReadForSystemUser() {

    HttpStatus result =
        uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatService, times(1)).markGroupAsReadForSystemUser(RC_ROOM_ID);
  }

  @Test
  public void uploadFileToRoom_Should_uploadToRocketChat() {

    HttpStatus result =
        uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatService, times(1))
        .roomsUpload(
            Mockito.any(RocketChatCredentials.class), Mockito.any(RocketChatUploadParameter.class));
  }

  @Test
  public void uploadFileToRoom_Should_EncryptRocketChatParameters() {

    HttpStatus result =
        uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterEncrypter, times(1)).encrypt(rocketChatUploadParameter);
  }

  @Test
  public void uploadFileToRoom_Should_SanitizeRocketChatParameter() {

    HttpStatus result =
        uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterSanitizer, times(1)).sanitize(rocketChatUploadParameter);
  }

  /** Method: uploadFileToFeedbackRoom */
  @Test
  public void uploadFileToFeedbackRoom_Should_ReturnHttpStatusCreated_WhenNoExceptionIsThrown() {

    HttpStatus result =
        uploadFacade.uploadFileToFeedbackRoom(
            rocketChatCredentials, rocketChatUploadParameter, false);

    assertEquals(HttpStatus.CREATED, result);
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_sendEmailNotification_WhenParamIsTrue() {

    HttpStatus result =
        uploadFacade.uploadFileToFeedbackRoom(
            rocketChatCredentials, rocketChatUploadParameter, true);

    verify(emailNotificationFacade, times(1)).sendFeedbackEmailNotification(RC_ROOM_ID);
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_markGroupAsReadForSystemUser() {

    HttpStatus result =
        uploadFacade.uploadFileToFeedbackRoom(
            rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatService, times(1)).markGroupAsReadForSystemUser(RC_ROOM_ID);
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_uploadToRocketChat() {

    HttpStatus result =
        uploadFacade.uploadFileToFeedbackRoom(
            rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatService, times(1))
        .roomsUpload(
            Mockito.any(RocketChatCredentials.class), Mockito.any(RocketChatUploadParameter.class));
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_EncryptRocketChatParameter() {

    HttpStatus result =
        uploadFacade.uploadFileToFeedbackRoom(
            rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterEncrypter, times(1)).encrypt(rocketChatUploadParameter);
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_SanitizeRocketChatParameter() {

    HttpStatus result =
        uploadFacade.uploadFileToFeedbackRoom(
            rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterSanitizer, times(1)).sanitize(rocketChatUploadParameter);
  }
}

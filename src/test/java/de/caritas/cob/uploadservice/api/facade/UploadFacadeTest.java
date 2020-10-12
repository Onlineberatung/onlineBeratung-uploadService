package de.caritas.cob.uploadservice.api.facade;

import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.exception.CustomCryptoException;
import de.caritas.cob.uploadservice.api.exception.RocketChatPostMarkGroupAsReadException;
import de.caritas.cob.uploadservice.api.exception.httpresponses.InternalServerErrorException;
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

@RunWith(MockitoJUnitRunner.class)
public class UploadFacadeTest {

  @Mock
  RocketChatService rocketChatService;
  @Mock
  EmailNotificationFacade emailNotificationFacade;
  @Mock
  RocketChatUploadParameterSanitizer rocketChatUploadParameterSanitizer;
  @Mock
  RocketChatUploadParameterEncrypter rocketChatUploadParameterEncrypter;
  @InjectMocks
  UploadFacade uploadFacade;

  @Mock
  RocketChatCredentials rocketChatCredentials;
  @Mock
  RocketChatUploadParameter rocketChatUploadParameter;

  /**
   * Method: uploadFileToRoom
   */
  @Before
  public void setup() throws CustomCryptoException {
    when(rocketChatUploadParameter.getRoomId()).thenReturn(RC_ROOM_ID);
    when(rocketChatUploadParameterSanitizer.sanitize(rocketChatUploadParameter))
        .thenReturn(rocketChatUploadParameter);
    when(rocketChatUploadParameterEncrypter.encrypt(rocketChatUploadParameter))
        .thenReturn(rocketChatUploadParameter);
  }

  @Test
  public void uploadFileToRoom_Should_UseServicesCorrectly_WhenNoExceptionIsThrown()
      throws Exception {

    uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterSanitizer, times(1)).sanitize(eq(rocketChatUploadParameter));
    verify(rocketChatUploadParameterEncrypter, times(1)).encrypt(eq(rocketChatUploadParameter));
    verify(rocketChatService, times(1)).roomsUpload(eq(rocketChatCredentials),
        eq(rocketChatUploadParameter));
    verify(rocketChatService, times(1)).markGroupAsReadForSystemUser(
        eq(rocketChatUploadParameter.getRoomId()));
  }

  @Test
  public void uploadFileToRoom_Should_sendEmailNotification_WhenParamIsTrue() {

    uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, true);

    verify(emailNotificationFacade, times(1)).sendEmailNotification(RC_ROOM_ID);
  }

  @Test
  public void uploadFileToRoom_Should_markGroupAsReadForSystemUser()
      throws RocketChatPostMarkGroupAsReadException {

    uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatService, times(1)).markGroupAsReadForSystemUser(RC_ROOM_ID);
  }

  @Test
  public void uploadFileToRoom_Should_uploadToRocketChat() {

    uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatService, times(1))
        .roomsUpload(
            Mockito.any(RocketChatCredentials.class), Mockito.any(RocketChatUploadParameter.class));
  }

  @Test
  public void uploadFileToRoom_Should_EncryptRocketChatParameters() throws CustomCryptoException {

    uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterEncrypter, times(1)).encrypt(rocketChatUploadParameter);
  }

  @Test
  public void uploadFileToRoom_Should_SanitizeRocketChatParameter() {

    uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterSanitizer, times(1)).sanitize(rocketChatUploadParameter);
  }

  /**
   * Method: uploadFileToFeedbackRoom
   */
  @Test
  public void uploadFileToFeedbackRoom_Should_CallServicesCorrectly_WhenNoExceptionIsThrown()
      throws Exception {

    uploadFacade.uploadFileToFeedbackRoom(
        rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterSanitizer, times(1)).sanitize(eq(rocketChatUploadParameter));
    verify(rocketChatUploadParameterEncrypter, times(1)).encrypt(eq(rocketChatUploadParameter));
    verify(rocketChatService, times(1)).roomsUpload(eq(rocketChatCredentials),
        eq(rocketChatUploadParameter));
    verify(rocketChatService, times(1)).markGroupAsReadForSystemUser(
        eq(rocketChatUploadParameter.getRoomId()));
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_sendEmailNotification_WhenParamIsTrue() {

    uploadFacade.uploadFileToFeedbackRoom(
        rocketChatCredentials, rocketChatUploadParameter, true);

    verify(emailNotificationFacade, times(1)).sendFeedbackEmailNotification(RC_ROOM_ID);
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_markGroupAsReadForSystemUser()
      throws RocketChatPostMarkGroupAsReadException {

    uploadFacade.uploadFileToFeedbackRoom(
        rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatService, times(1)).markGroupAsReadForSystemUser(RC_ROOM_ID);
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_uploadToRocketChat() {

    uploadFacade.uploadFileToFeedbackRoom(
        rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatService, times(1))
        .roomsUpload(
            Mockito.any(RocketChatCredentials.class), Mockito.any(RocketChatUploadParameter.class));
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_EncryptRocketChatParameter()
      throws CustomCryptoException {

    uploadFacade.uploadFileToFeedbackRoom(
        rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterEncrypter, times(1)).encrypt(rocketChatUploadParameter);
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_SanitizeRocketChatParameter() {

    uploadFacade.uploadFileToFeedbackRoom(
        rocketChatCredentials, rocketChatUploadParameter, false);

    verify(rocketChatUploadParameterSanitizer, times(1)).sanitize(rocketChatUploadParameter);
  }

  @Test(expected = InternalServerErrorException.class)
  public void uploadFileToRoom_Should_ThrowInternalServerErrorException_WhenCustomCryptoExceptionIsThrown()
      throws Exception {

    when(rocketChatUploadParameterEncrypter.encrypt(any()))
        .thenThrow(mock(CustomCryptoException.class));

    uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);
  }

  @Test(expected = InternalServerErrorException.class)
  public void uploadFileToRoom_Should_ThrowInternalServerErrorException_WheRocketChatPostMarkGroupAsReadExceptionIsThrown()
      throws Exception {

    doThrow(mock(RocketChatPostMarkGroupAsReadException.class)).when(rocketChatService)
        .markGroupAsReadForSystemUser(anyString());

    uploadFacade.uploadFileToRoom(rocketChatCredentials, rocketChatUploadParameter, false);
  }
}

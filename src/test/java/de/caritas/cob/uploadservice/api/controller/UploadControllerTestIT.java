package de.caritas.cob.uploadservice.api.controller;

import static de.caritas.cob.uploadservice.helper.PathConstants.PATH_UPDATE_KEY;
import static de.caritas.cob.uploadservice.helper.PathConstants.PATH_UPLOAD_FILE_TO_FEEDBACK_ROOM;
import static de.caritas.cob.uploadservice.helper.PathConstants.PATH_UPLOAD_FILE_TO_ROOM;
import static de.caritas.cob.uploadservice.helper.TestConstants.FORM_PARAM_DESCRIPTION;
import static de.caritas.cob.uploadservice.helper.TestConstants.FORM_PARAM_DESCRIPTION_VALUE;
import static de.caritas.cob.uploadservice.helper.TestConstants.FORM_PARAM_FILE;
import static de.caritas.cob.uploadservice.helper.TestConstants.FORM_PARAM_MESSAGE;
import static de.caritas.cob.uploadservice.helper.TestConstants.FORM_PARAM_MESSAGE_VALUE;
import static de.caritas.cob.uploadservice.helper.TestConstants.FORM_PARAM_SEND_NOTIFICATION;
import static de.caritas.cob.uploadservice.helper.TestConstants.FORM_PARAM_SEND_NOTIFICATION_TRUE;
import static de.caritas.cob.uploadservice.helper.TestConstants.FORM_PARAM_TMID;
import static de.caritas.cob.uploadservice.helper.TestConstants.FORM_PARAM_TMID_VALUE;
import static de.caritas.cob.uploadservice.helper.TestConstants.MASTER_KEY_1;
import static de.caritas.cob.uploadservice.helper.TestConstants.MASTER_KEY_DTO_KEY_1;
import static de.caritas.cob.uploadservice.helper.TestConstants.MASTER_KEY_DTO_KEY_2;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_TOKEN;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_TOKEN_HEADER_PARAMETER_NAME;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_USER_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_USER_ID_HEADER_PARAMETER_NAME;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.uploadservice.api.container.RocketChatCredentials;
import de.caritas.cob.uploadservice.api.container.RocketChatUploadParameter;
import de.caritas.cob.uploadservice.api.facade.UploadFacade;
import de.caritas.cob.uploadservice.api.service.EncryptionService;
import de.caritas.cob.uploadservice.api.service.LogService;
import de.caritas.cob.uploadservice.api.service.RocketChatService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(UploadController.class)
@AutoConfigureMockMvc(secure = false)
@TestPropertySource(properties = "spring.profiles.active=testing")
public class UploadControllerTestIT {

  @Autowired private MockMvc mvc;

  @MockBean private RocketChatService rocketChatService;

  @MockBean private EncryptionService encryptionService;

  @MockBean private LogService logService;

  @MockBean private UploadFacade uploadFacade;

  /** 404 - Not found */
  @Test
  public void uploadFileToRoom_Should_ReturnNotFound_WhenRoomIdIsMissing() throws Exception {

    MockPart fileToUpload = new MockPart(FORM_PARAM_FILE, "fileToUpload", "content".getBytes());

    mvc.perform(
            multipart(PATH_UPLOAD_FILE_TO_ROOM)
                .part(fileToUpload)
                .param(FORM_PARAM_SEND_NOTIFICATION, FORM_PARAM_SEND_NOTIFICATION_TRUE)
                .param(FORM_PARAM_DESCRIPTION, FORM_PARAM_DESCRIPTION_VALUE)
                .param(FORM_PARAM_MESSAGE, FORM_PARAM_MESSAGE_VALUE)
                .param(FORM_PARAM_TMID, FORM_PARAM_TMID_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(RC_TOKEN_HEADER_PARAMETER_NAME, RC_TOKEN)
                .header(RC_USER_ID_HEADER_PARAMETER_NAME, RC_USER_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_ReturnNotFound_WhenRoomIdIsMissing()
      throws Exception {

    MockPart fileToUpload = new MockPart(FORM_PARAM_FILE, "fileToUpload", "content".getBytes());

    mvc.perform(
            multipart(PATH_UPLOAD_FILE_TO_FEEDBACK_ROOM)
                .part(fileToUpload)
                .param(FORM_PARAM_SEND_NOTIFICATION, FORM_PARAM_SEND_NOTIFICATION_TRUE)
                .param(FORM_PARAM_DESCRIPTION, FORM_PARAM_DESCRIPTION_VALUE)
                .param(FORM_PARAM_MESSAGE, FORM_PARAM_MESSAGE_VALUE)
                .param(FORM_PARAM_TMID, FORM_PARAM_TMID_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(RC_TOKEN_HEADER_PARAMETER_NAME, RC_TOKEN)
                .header(RC_USER_ID_HEADER_PARAMETER_NAME, RC_USER_ID))
        .andExpect(status().isNotFound());
  }

  /** 400 - Bad Request tests */
  @Test
  public void uploadFileToRoom_Should_ReturnBadRequest_WhenFileIsMissing() throws Exception {

    mvc.perform(
            multipart(PATH_UPLOAD_FILE_TO_ROOM + "/" + RC_ROOM_ID)
                .param(FORM_PARAM_SEND_NOTIFICATION, FORM_PARAM_SEND_NOTIFICATION_TRUE)
                .param(FORM_PARAM_DESCRIPTION, FORM_PARAM_DESCRIPTION_VALUE)
                .param(FORM_PARAM_MESSAGE, FORM_PARAM_MESSAGE_VALUE)
                .param(FORM_PARAM_TMID, FORM_PARAM_TMID_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(RC_TOKEN_HEADER_PARAMETER_NAME, RC_TOKEN)
                .header(RC_USER_ID_HEADER_PARAMETER_NAME, RC_USER_ID))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void uploadFileToRoom_Should_ReturnBadRequest_WhenSendNotificationIsMissing()
      throws Exception {

    MockPart fileToUpload = new MockPart(FORM_PARAM_FILE, "fileToUpload", "content".getBytes());

    mvc.perform(
            multipart(PATH_UPLOAD_FILE_TO_ROOM + "/" + RC_ROOM_ID)
                .part(fileToUpload)
                .param(FORM_PARAM_DESCRIPTION, FORM_PARAM_DESCRIPTION_VALUE)
                .param(FORM_PARAM_MESSAGE, FORM_PARAM_MESSAGE_VALUE)
                .param(FORM_PARAM_TMID, FORM_PARAM_TMID_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(RC_TOKEN_HEADER_PARAMETER_NAME, RC_TOKEN)
                .header(RC_USER_ID_HEADER_PARAMETER_NAME, RC_USER_ID))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_ReturnBadRequest_WhenFileIsMissing()
      throws Exception {

    mvc.perform(
            multipart(PATH_UPLOAD_FILE_TO_FEEDBACK_ROOM + "/" + RC_ROOM_ID)
                .param(FORM_PARAM_SEND_NOTIFICATION, FORM_PARAM_SEND_NOTIFICATION_TRUE)
                .param(FORM_PARAM_DESCRIPTION, FORM_PARAM_DESCRIPTION_VALUE)
                .param(FORM_PARAM_MESSAGE, FORM_PARAM_MESSAGE_VALUE)
                .param(FORM_PARAM_TMID, FORM_PARAM_TMID_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(RC_TOKEN_HEADER_PARAMETER_NAME, RC_TOKEN)
                .header(RC_USER_ID_HEADER_PARAMETER_NAME, RC_USER_ID))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_ReturnBadRequest_WhenSendNotificationIsMissing()
      throws Exception {

    MockPart fileToUpload = new MockPart(FORM_PARAM_FILE, "fileToUpload", "content".getBytes());

    mvc.perform(
            multipart(PATH_UPLOAD_FILE_TO_FEEDBACK_ROOM + "/" + RC_ROOM_ID)
                .part(fileToUpload)
                .param(FORM_PARAM_DESCRIPTION, FORM_PARAM_DESCRIPTION_VALUE)
                .param(FORM_PARAM_MESSAGE, FORM_PARAM_MESSAGE_VALUE)
                .param(FORM_PARAM_TMID, FORM_PARAM_TMID_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(RC_TOKEN_HEADER_PARAMETER_NAME, RC_TOKEN)
                .header(RC_USER_ID_HEADER_PARAMETER_NAME, RC_USER_ID))
        .andExpect(status().isBadRequest());
  }

  /** 200 - OK & 201 CREATED tests */
  @Test
  public void uploadFileToRoom_Should_ReturnOk_WhenValidRequest() throws Exception {

    when(uploadFacade.uploadFileToRoom(
            ArgumentMatchers.any(RocketChatCredentials.class),
            ArgumentMatchers.any(RocketChatUploadParameter.class),
            ArgumentMatchers.anyBoolean()))
        .thenReturn(HttpStatus.CREATED);

    MockPart fileToUpload = new MockPart(FORM_PARAM_FILE, "fileToUpload", "content".getBytes());

    mvc.perform(
            multipart(PATH_UPLOAD_FILE_TO_ROOM + "/" + RC_ROOM_ID)
                .part(fileToUpload)
                .param(FORM_PARAM_SEND_NOTIFICATION, FORM_PARAM_SEND_NOTIFICATION_TRUE)
                .param(FORM_PARAM_DESCRIPTION, FORM_PARAM_DESCRIPTION_VALUE)
                .param(FORM_PARAM_MESSAGE, FORM_PARAM_MESSAGE_VALUE)
                .param(FORM_PARAM_TMID, FORM_PARAM_TMID_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(RC_TOKEN_HEADER_PARAMETER_NAME, RC_TOKEN)
                .header(RC_USER_ID_HEADER_PARAMETER_NAME, RC_USER_ID))
        .andExpect(status().isCreated());
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_ReturnOk_WhenValidRequest() throws Exception {

    when(uploadFacade.uploadFileToFeedbackRoom(
            ArgumentMatchers.any(RocketChatCredentials.class),
            ArgumentMatchers.any(RocketChatUploadParameter.class),
            ArgumentMatchers.anyBoolean()))
        .thenReturn(HttpStatus.CREATED);

    MockPart fileToUpload = new MockPart(FORM_PARAM_FILE, "fileToUpload", "content".getBytes());

    mvc.perform(
            multipart(PATH_UPLOAD_FILE_TO_FEEDBACK_ROOM + "/" + RC_ROOM_ID)
                .part(fileToUpload)
                .param(FORM_PARAM_SEND_NOTIFICATION, FORM_PARAM_SEND_NOTIFICATION_TRUE)
                .param(FORM_PARAM_DESCRIPTION, FORM_PARAM_DESCRIPTION_VALUE)
                .param(FORM_PARAM_MESSAGE, FORM_PARAM_MESSAGE_VALUE)
                .param(FORM_PARAM_TMID, FORM_PARAM_TMID_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(RC_TOKEN_HEADER_PARAMETER_NAME, RC_TOKEN)
                .header(RC_USER_ID_HEADER_PARAMETER_NAME, RC_USER_ID))
        .andExpect(status().isCreated());
  }

  /**
   * 202 - Accepted Test
   *
   * @throws Exception
   */
  @Test
  public void updateKey_Should_ReturnAccepted_WhenProvidedWithNewKey() throws Exception {

    when(encryptionService.getMasterKey()).thenReturn(MASTER_KEY_1);

    mvc.perform(
            post(PATH_UPDATE_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(MASTER_KEY_DTO_KEY_2)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  /**
   * 409 - Conflict test
   *
   * @throws Exception
   */
  @Test
  public void updateKey_Should_ReturnConflict_WhenProvidedWithSameKey() throws Exception {

    when(encryptionService.getMasterKey()).thenReturn(MASTER_KEY_1);

    mvc.perform(
            post(PATH_UPDATE_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(MASTER_KEY_DTO_KEY_1)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
  }

  /** Helper methods */
  private String convertObjectToJson(Object object) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }
}

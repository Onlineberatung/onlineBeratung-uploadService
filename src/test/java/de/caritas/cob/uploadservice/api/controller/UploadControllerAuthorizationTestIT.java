package de.caritas.cob.uploadservice.api.controller;

import static de.caritas.cob.uploadservice.helper.PathConstants.PATH_UPDATE_KEY;
import static de.caritas.cob.uploadservice.helper.PathConstants.PATH_UPLOAD_FILE_TO_FEEDBACK_ROOM;
import static de.caritas.cob.uploadservice.helper.PathConstants.PATH_UPLOAD_FILE_TO_ROOM;
import static de.caritas.cob.uploadservice.helper.TestConstants.CSRF_COOKIE;
import static de.caritas.cob.uploadservice.helper.TestConstants.CSRF_HEADER;
import static de.caritas.cob.uploadservice.helper.TestConstants.CSRF_VALUE;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_ROOM_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_TOKEN;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_TOKEN_HEADER_PARAMETER_NAME;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_USER_ID;
import static de.caritas.cob.uploadservice.helper.TestConstants.RC_USER_ID_HEADER_PARAMETER_NAME;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.uploadservice.api.authorization.Authorities.Authority;
import de.caritas.cob.uploadservice.api.facade.UploadFacade;
import de.caritas.cob.uploadservice.api.service.EncryptionService;
import de.caritas.cob.uploadservice.api.service.RocketChatService;
import javax.servlet.http.Cookie;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class UploadControllerAuthorizationTestIT {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private RocketChatService rocketChatService;

  @MockBean
  private EncryptionService encryptionService;

  @MockBean
  private UploadFacade uploadFacade;

  private Cookie csrfCookie;

  @Before
  public void setUp() {
    csrfCookie = new Cookie(CSRF_COOKIE, CSRF_VALUE);
  }

  @Test
  public void updateKey_Should_ReturnUnauthorizedAndCallNoMethods_WhenNoKeycloakAuthorization()
      throws Exception {

    mvc.perform(
        post(PATH_UPDATE_KEY)
            .cookie(csrfCookie)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

    verifyNoMoreInteractions(encryptionService);
  }

  @Test
  @WithMockUser(authorities = {Authority.CONSULTANT_DEFAULT, Authority.USER_DEFAULT})
  public void updateKey_Should_ReturnForbiddenAndCallNoMethods_WhenNoTechnicalDefaultAuthority()
      throws Exception {

    mvc.perform(
        post(PATH_UPDATE_KEY)
            .cookie(csrfCookie)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

    verifyNoMoreInteractions(encryptionService);
  }

  @Test
  @WithMockUser(authorities = {Authority.TECHNICAL_DEFAULT})
  public void updateKey_Should_ReturnForbiddenAndCallNoMethods_WhenNoCsrfTokens() throws Exception {

    mvc.perform(
        post(PATH_UPDATE_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

    verifyNoMoreInteractions(encryptionService);
  }

  @Test
  public void uploadFileToRoom_Should_ReturnUnauthorizedAndCallNoMethods_WhenNoKeycloakAuthorization()
      throws Exception {

    mvc.perform(
        post(PATH_UPLOAD_FILE_TO_ROOM + "/" + RC_ROOM_ID)
            .cookie(csrfCookie)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

    verifyNoMoreInteractions(rocketChatService);
    verifyNoMoreInteractions(uploadFacade);
  }

  @Test
  @WithMockUser
  public void uploadFileToRoom_Should_ReturnForbiddenAndCallNoMethods_WhenNoUserOrConsultantAuthority()
      throws Exception {

    mvc.perform(
        post(PATH_UPLOAD_FILE_TO_ROOM + "/" + RC_ROOM_ID)
            .cookie(csrfCookie)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

    verifyNoMoreInteractions(rocketChatService);
    verifyNoMoreInteractions(uploadFacade);
  }

  @Test
  @WithMockUser(authorities = {Authority.CONSULTANT_DEFAULT, Authority.USER_DEFAULT})
  public void uploadFileToRoom_Should_ReturnForbiddenAndCallNoMethods_WhenNoCsrfTokens()
      throws Exception {

    mvc.perform(
        post(PATH_UPLOAD_FILE_TO_ROOM + "/" + RC_ROOM_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

    verifyNoMoreInteractions(rocketChatService);
    verifyNoMoreInteractions(uploadFacade);
  }

  @Test
  public void uploadFileToFeedbackRoom_Should_Return401AndCallNoMethods_WhenNoKeycloakAuthorization()
      throws Exception {

    mvc.perform(
        post(PATH_UPLOAD_FILE_TO_FEEDBACK_ROOM + "/" + RC_ROOM_ID)
            .cookie(csrfCookie)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

    verifyNoMoreInteractions(rocketChatService);
    verifyNoMoreInteractions(uploadFacade);
  }

  @Test
  @WithMockUser
  public void uploadFileToFeedbackRoom_Should_Return403AndCallNoMethods_WhenNoUserOrConsultantAuthority()
      throws Exception {

    mvc.perform(
        post(PATH_UPLOAD_FILE_TO_FEEDBACK_ROOM + "/" + RC_ROOM_ID)
            .cookie(csrfCookie)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

    verifyNoMoreInteractions(rocketChatService);
    verifyNoMoreInteractions(uploadFacade);
  }

  @Test
  @WithMockUser(authorities = {Authority.CONSULTANT_DEFAULT, Authority.USER_DEFAULT})
  public void uploadFileToFeedbackRoom_Should_ReturnForbiddenAndCallNoMethods_WhenNoCsrfTokens()
      throws Exception {

    mvc.perform(
        post(PATH_UPLOAD_FILE_TO_FEEDBACK_ROOM + "/" + RC_ROOM_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

    verifyNoMoreInteractions(rocketChatService);
    verifyNoMoreInteractions(uploadFacade);
  }

  @Test
  @WithMockUser(authorities = {Authority.ANONYMOUS_DEFAULT})
  public void uploadFileToRoom_Should_ReturnCreated_When_AnonymousAuthority()
      throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "filename", "text/plain",
        "content".getBytes());

    mvc.perform(
        multipart(PATH_UPLOAD_FILE_TO_ROOM + "/" + RC_ROOM_ID)
            .file(file)
            .param("sendNotification", "true")
            .cookie(csrfCookie)
            .header(CSRF_HEADER, CSRF_VALUE)
            .header(RC_TOKEN_HEADER_PARAMETER_NAME, RC_TOKEN)
            .header(RC_USER_ID_HEADER_PARAMETER_NAME, RC_USER_ID)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

}

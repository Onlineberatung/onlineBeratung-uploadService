package de.caritas.cob.uploadservice.api.config.apiclient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import de.caritas.cob.uploadservice.UploadServiceApplication;
import de.caritas.cob.uploadservice.userservice.generated.web.LiveproxyControllerApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UploadServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
public class LiveProxyApiClientConfigIT {

  @Autowired
  private LiveproxyControllerApi liveproxyControllerApi;

  @Value("${user.service.api.liveproxy.url}")
  private String liveProxyApiUrl;

  @Test
  public void configureLiveControllerApi_Should_setCorrectApiUrl() {
    String apiClientUrl = this.liveproxyControllerApi.getApiClient().getBasePath();

    assertThat(apiClientUrl, is(this.liveProxyApiUrl));
  }

}
package de.caritas.cob.uploadservice.config.apiclient;

import de.caritas.cob.uploadservice.userservice.generated.ApiClient;
import de.caritas.cob.uploadservice.userservice.generated.web.UserControllerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceApiControllerFactory {

  @Value("${user.service.api.url}")
  private String userServiceApiUrl;
  @Autowired
  private RestTemplate restTemplate;

  public UserControllerApi createControllerApi() {
    var apiClient = new ApiClient(restTemplate).setBasePath(this.userServiceApiUrl);
    return new UserControllerApi(apiClient);
  }
}

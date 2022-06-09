package de.caritas.cob.uploadservice.config.apiclient;

import de.caritas.cob.uploadservice.userservice.generated.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceApiClientConfig {

  @Value("${user.service.api.url}")
  private String userServiceApiUrl;

  @Bean
  public de.caritas.cob.uploadservice.userservice.generated.web.UserControllerApi userControllerApi(ApiClient apiClient) {
    return new de.caritas.cob.uploadservice.userservice.generated.web.UserControllerApi(apiClient);
  }

  @Bean
  @Primary
  public ApiClient userApiClient(RestTemplate restTemplate) {
    return new ApiClient(restTemplate).setBasePath(this.userServiceApiUrl);
  }

}
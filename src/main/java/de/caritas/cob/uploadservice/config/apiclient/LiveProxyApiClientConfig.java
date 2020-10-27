package de.caritas.cob.uploadservice.config.apiclient;

import de.caritas.cob.uploadservice.userservice.generated.ApiClient;
import de.caritas.cob.uploadservice.userservice.generated.web.LiveproxyControllerApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LiveProxyApiClientConfig {

  @Value("${user.service.api.liveproxy.url}")
  private String liveProxyApiUrl;

  @Bean
  public LiveproxyControllerApi liveproxyControllerApi(ApiClient apiClient) {
    return new LiveproxyControllerApi(apiClient);
  }

  @Bean
  @Primary
  public ApiClient liveProxyApiClient(RestTemplate restTemplate) {
    return new ApiClient(restTemplate).setBasePath(this.liveProxyApiUrl);
  }

}
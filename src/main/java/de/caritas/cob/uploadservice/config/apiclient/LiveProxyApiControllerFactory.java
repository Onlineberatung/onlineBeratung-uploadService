package de.caritas.cob.uploadservice.config.apiclient;

import de.caritas.cob.uploadservice.userservice.generated.ApiClient;
import de.caritas.cob.uploadservice.userservice.generated.web.LiveproxyControllerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LiveProxyApiControllerFactory {

  @Value("${user.service.api.liveproxy.url}")
  private String liveProxyApiUrl;
  @Autowired
  private RestTemplate restTemplate;

  public LiveproxyControllerApi createControllerApi() {
    var apiClient = new ApiClient(restTemplate).setBasePath(this.liveProxyApiUrl);
    return new LiveproxyControllerApi(apiClient);
  }
}

package de.caritas.cob.uploadservice.filter;


import static java.util.Optional.empty;
import static java.util.Optional.of;

import com.google.common.net.InternetDomainName;
import de.caritas.cob.uploadservice.api.service.helper.HttpUrlUtils;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Setter
@Slf4j
@AllArgsConstructor
public class SubdomainExtractor {

  public Optional<String> getCurrentSubdomain(HttpServletRequest request) {
    String url = HttpUrlUtils.removeHttpPrefix(request.getServerName());
    return getSubdomain(url);
  }

  public Optional<String> getSubdomain(String url) {
    var domain = InternetDomainName.from(url);
    if (!domain.hasParent()) {
      return empty();
    }
    if (domain.parts().isEmpty()) {
      return Optional.empty();
    }
    return of(domain.parts().get(0));
  }
}


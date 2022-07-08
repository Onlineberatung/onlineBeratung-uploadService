package de.caritas.cob.uploadservice.filter;


import static java.util.Optional.of;

import java.net.URISyntaxException;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubdomainExtractorTest {

  private static final String MUCOVISCIDOSE = "mucoviscidose";
  private static final String ONLINBEBERATUNG_DE = ".onlineberatung.de";

  @Mock
  HttpServletRequest request;

  @Mock
  Enumeration<String> headerNames;

  @InjectMocks
  SubdomainExtractor subdomainExtractor;

  @Test
  void resolveSubdomain_Should_resolveSubdomain() throws URISyntaxException {
    // given
    String url = MUCOVISCIDOSE + ONLINBEBERATUNG_DE;
    // when, then
    AssertionsForClassTypes.assertThat(subdomainExtractor.getSubdomain(url)).isEqualTo(of("mucoviscidose"));
  }

}
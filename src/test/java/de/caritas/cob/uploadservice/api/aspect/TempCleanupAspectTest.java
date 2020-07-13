package de.caritas.cob.uploadservice.api.aspect;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.uploadservice.api.helper.GarbageCollectorHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

@RunWith(MockitoJUnitRunner.class)
public class TempCleanupAspectTest {

  @Mock MultipartResolver multipartResolver;
  @Mock GarbageCollectorHelper garbageCollectorHelper;
  @InjectMocks TempCleanupAspect tempCleanupAspect;

  @Test
  public void cleanUpTempFiles_Should_CleanUpTempFiles() {

    MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    when(multipartResolver.resolveMultipart(Mockito.any()))
        .thenReturn(new MockMultipartHttpServletRequest());

    tempCleanupAspect.cleanUpTempFiles();

    verify(multipartResolver, times(1))
        .cleanupMultipart(Mockito.any(MultipartHttpServletRequest.class));
    verify(garbageCollectorHelper, times(1)).runGarbageCollector();
  }
}

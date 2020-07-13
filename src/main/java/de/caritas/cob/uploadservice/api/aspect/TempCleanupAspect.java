package de.caritas.cob.uploadservice.api.aspect;

import de.caritas.cob.uploadservice.api.helper.GarbageCollectorHelper;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/** Aspect for forcing a delete of the temp files of the upload */
@Aspect
@Component
public class TempCleanupAspect {

  @Autowired private MultipartResolver multipartResolver;
  @Autowired private GarbageCollectorHelper garbageCollectorHelper;

  /**
   * Do a cleanup via the {@link MultipartResolver} and run the garbage collector to force a cleanup
   * of the temporary files
   */
  @After("@annotation(de.caritas.cob.uploadservice.api.aspect.TempCleanup)")
  public void cleanUpTempFiles() {
    multipartResolver.cleanupMultipart(getMultipartHttpServletRequestFromRequestContext());
    /* Necessary for deleting all temp files */
    garbageCollectorHelper.runGarbageCollector();
  }

  /**
   * Resolve {@link MultipartHttpServletRequest} from the current {@link
   * javax.servlet.http.HttpServletRequest}
   *
   * @return a MultipartHttpServletRequest instance
   */
  private MultipartHttpServletRequest getMultipartHttpServletRequestFromRequestContext() {
    return multipartResolver.resolveMultipart(
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
  }
}

package de.caritas.cob.uploadservice.api.helper;

import org.springframework.stereotype.Component;

/** Helper for running the garbage collector */
@Component
public class GarbageCollectorHelper {

  /** Runs the garbage collector */
  public void runGarbageCollector() {
    System.gc();
  }
}

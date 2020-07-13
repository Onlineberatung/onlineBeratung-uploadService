package de.caritas.cob.uploadservice.api.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Custom annotation for {@link TempCleanupAspect} */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TempCleanup {}

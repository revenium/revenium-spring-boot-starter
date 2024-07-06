package io.revenium.metering.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for metering method invocations.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Metered {
    /**
     * The subscription ID to use for the metering request.
     */
    String subscriptionId() default "";

    /**
     * The source ID to use for the metering request.
     */
    String sourceId() default "";

    /**
     * The elements to include in the metering request.
     */
    String elements() default "";
}
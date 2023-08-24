package org.skyme.annotation;

import java.lang.annotation.*;

/**
 * @author Skyme
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Table {


    String value() default "";
}

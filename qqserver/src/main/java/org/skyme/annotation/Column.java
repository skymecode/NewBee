package org.skyme.annotation;

import java.lang.annotation.*;

/**
 * @author Skyme
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface Column {
    String value() default "";
}

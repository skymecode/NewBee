package org.skyme.annotation;

import java.lang.annotation.*;

/**
 * @author Skyme
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface ManyToOne {
    String value() default "";
}

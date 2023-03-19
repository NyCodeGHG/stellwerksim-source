package de.deltaga.eb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Message {
   int maximum() default 0;

   boolean dropOnMax() default false;

   int parallelism() default 1;

   String threadgroup() default "";
}

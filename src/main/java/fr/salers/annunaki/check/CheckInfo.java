package fr.salers.annunaki.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckInfo {

    String name() default "change";

    String type() default "A";

    String description() default "Not set";

    boolean experimental() default false;

    int maxVl() default 10;

    boolean punish() default true;

    boolean enabled() default true;
}

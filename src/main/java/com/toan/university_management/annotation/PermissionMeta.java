package com.toan.university_management.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionMeta {
    String value() default "";
    String description() default "";
    String module() default "";
    boolean isPublic() default false;
}
